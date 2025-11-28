package com.vajra.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.vajra.config.VajraSettings
import com.vajra.providers.ProviderManager
import com.vajra.ui.VajraInlineService
import com.vajra.utils.EditorUtils
import kotlinx.coroutines.runBlocking
import javax.swing.SwingUtilities

class OpenChatAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        ToolWindowManager.getInstance(project).getToolWindow("Vajra Chat")?.show()
    }
}

class SelectProviderAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val providerManager = ProviderManager()
        val providers = providerManager.getAllProviders()
        
        val options = providers.map { "${it.displayName} (${if (it.isConfigured()) "✓" else "✗"})" }.toTypedArray()
        val selected = Messages.showChooseDialog(
            project,
            "Select AI Provider:",
            "Vajra Provider Selection",
            Messages.getQuestionIcon(),
            options,
            options[0]
        )
        
        if (selected >= 0) {
            val settings = VajraSettings.getInstance().state
            settings.defaultProvider = providers[selected].name
            Messages.showInfoMessage(project, "Switched to ${providers[selected].displayName}", "Provider Changed")
        }
    }
}

class ShowModelStatusAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val settings = VajraSettings.getInstance().state
        val providerManager = ProviderManager()
        
        val status = buildString {
            appendLine("=== Vajra Model Status ===\n")
            appendLine("Current Provider: ${settings.defaultProvider}")
            appendLine("Current Model: ${settings.defaultModel}\n")
            appendLine("Configured Providers:")
            providerManager.getAllProviders().forEach { provider ->
                val status = if (provider.isConfigured()) "✓ Ready" else "✗ Not configured"
                appendLine("- ${provider.displayName}: $status")
            }
        }
        
        Messages.showInfoMessage(project, status, "Vajra Status")
    }
}

abstract class CodeAction : AnAction() {
    protected fun executeCodeAction(
        e: AnActionEvent,
        actionType: String,
        promptBuilder: (String, String) -> String,
        showInline: Boolean = true
    ) {
        val project = e.project ?: return
        val editor = EditorUtils.getActiveEditor(project)
        val selectedText = EditorUtils.getSelectedText(project)
        
        if (selectedText.isNullOrEmpty()) {
            Messages.showErrorDialog(project, "Please select some code first", "No Code Selected")
            return
        }
        
        if (editor == null) {
            Messages.showErrorDialog(project, "No active editor found", "Error")
            return
        }
        
        val selectionStart = editor.selectionModel.selectionStart
        val selectionEnd = editor.selectionModel.selectionEnd
        val language = EditorUtils.getLanguage(project) ?: "code"
        val prompt = promptBuilder(language, selectedText)
        
        // Show loading indicator
        SwingUtilities.invokeLater {
            val inlineService = VajraInlineService.getInstance()
            inlineService.showGhostText(editor, " ⏳ AI thinking...", selectionEnd)
        }
        
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val settings = VajraSettings.getInstance().state
                val providerManager = ProviderManager()
                val provider = providerManager.getProvider(settings.defaultProvider)
                
                if (provider == null || !provider.isConfigured()) {
                    SwingUtilities.invokeLater {
                        Messages.showErrorDialog(
                            project,
                            "Please configure your API key in Settings > Tools > Vajra",
                            "Provider Not Configured"
                        )
                    }
                    return@executeOnPooledThread
                }
                
                val response = runBlocking {
                    provider.sendMessage(prompt, settings.defaultModel)
                }
                
                SwingUtilities.invokeLater {
                    if (showInline) {
                        val inlineService = VajraInlineService.getInstance()
                        
                        // Clean up the response - remove markdown formatting
                        val cleanedResponse = cleanMarkdown(response)
                        
                        when (actionType) {
                            "Explain" -> {
                                // Show explanation as popup with nice formatting
                                inlineService.showExplanationPopup(editor, cleanedResponse, selectionStart)
                            }
                            "Refactor", "Optimize", "Add Comments" -> {
                                // Show as inline diff with Accept/Reject buttons
                                inlineService.showInlineDiff(
                                    project,
                                    editor,
                                    selectedText,
                                    cleanedResponse,
                                    selectionStart,
                                    selectionEnd
                                )
                            }
                            "Debug" -> {
                                // Show debugging advice as popup
                                inlineService.showExplanationPopup(editor, cleanedResponse, selectionStart)
                            }
                            "Generate Tests" -> {
                                // Show test code as ghost text below the function
                                inlineService.showGhostText(editor, "\n\n// Generated Tests:\n$cleanedResponse", selectionEnd)
                            }
                        }
                    } else {
                        // Fallback to old dialog style
                        Messages.showInfoMessage(project, response, "$actionType Result")
                    }
                }
                
            } catch (ex: Exception) {
                SwingUtilities.invokeLater {
                    Messages.showErrorDialog(project, ex.message ?: "Unknown error", "Error")
                }
            }
        }
    }
    
    /**
     * Clean markdown formatting from AI responses
     * Removes code blocks, "Here's the code" preambles, and explanations
     */
    private fun cleanMarkdown(response: String): String {
        var cleaned = response.trim()
        
        // Remove common preambles
        val preambles = listOf(
            "Here's the Java code with comprehensive documentation comments added:",
            "Here's the refactored code:",
            "Here's the optimized code:",
            "Here's the code with comments:",
            "Here is the code:",
            "Here's the code:",
            "```java",
            "```kotlin",
            "```python",
            "```javascript",
            "```",
        )
        
        for (preamble in preambles) {
            if (cleaned.startsWith(preamble, ignoreCase = true)) {
                cleaned = cleaned.substring(preamble.length).trim()
            }
        }
        
        // Remove markdown code blocks
        cleaned = cleaned.replace(Regex("```[a-z]*\n?"), "")
        cleaned = cleaned.replace(Regex("```"), "")
        
        // Remove "### Explanation:" and everything after
        val explanationIndex = cleaned.indexOf("### Explanation:")
        if (explanationIndex > 0) {
            cleaned = cleaned.substring(0, explanationIndex).trim()
        }
        
        // Remove "Explanation:" and everything after (without ###)
        val explainIndex = cleaned.indexOf("\nExplanation:")
        if (explainIndex > 0) {
            cleaned = cleaned.substring(0, explainIndex).trim()
        }
        
        return cleaned.trim()
    }
}

class ExplainCodeAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(
            e = e,
            actionType = "Explain",
            promptBuilder = { language, code ->
                "Explain this $language code in detail:\n```$language\n$code\n```"
            }
        )
    }
}

class RefactorCodeAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(
            e = e,
            actionType = "Refactor",
            promptBuilder = { language, code ->
                """Refactor this $language code for better readability and performance.

CRITICAL INSTRUCTIONS:
- Return ONLY the refactored code
- Do NOT include any explanations before or after
- Do NOT use markdown code blocks (no ```)
- Do NOT add "Here's the refactored code..." or any preamble
- Do NOT add explanations at the end
- Just return the raw refactored code

Code to refactor:
```$language
$code
```"""
            }
        )
    }
}

class DebugCodeAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(
            e = e,
            actionType = "Debug",
            promptBuilder = { language, code ->
                "Help me debug this $language code. Find potential issues and suggest fixes:\n```$language\n$code\n```"
            }
        )
    }
}

class OptimizeCodeAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(
            e = e,
            actionType = "Optimize",
            promptBuilder = { language, code ->
                """Optimize this $language code for better performance.

CRITICAL INSTRUCTIONS:
- Return ONLY the optimized code
- Do NOT include any explanations before or after
- Do NOT use markdown code blocks (no ```)
- Do NOT add "Here's the optimized code..." or any preamble
- Do NOT add explanations at the end
- Just return the raw optimized code

Code to optimize:
```$language
$code
```"""
            }
        )
    }
}

class AddCommentsAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(
            e = e,
            actionType = "Add Comments",
            promptBuilder = { language, code ->
                """Add comprehensive documentation comments to this $language code.
                
CRITICAL INSTRUCTIONS:
- Return ONLY the code with comments added
- Do NOT include any explanations before or after the code
- Do NOT use markdown code blocks (no ```)
- Do NOT add "Here's the code..." or any preamble
- Do NOT add explanations at the end
- Just return the raw code with comments

Code to document:
```$language
$code
```"""
            }
        )
    }
}

class GenerateTestsAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(
            e = e,
            actionType = "Generate Tests",
            promptBuilder = { language, code ->
                "Generate comprehensive unit tests for this $language code:\n```$language\n$code\n```"
            }
        )
    }
}
