package com.vajra.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.vajra.config.VajraSettings
import com.vajra.providers.ProviderManager
import com.vajra.utils.EditorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        promptBuilder: (String, String) -> String
    ) {
        val project = e.project ?: return
        val selectedText = EditorUtils.getSelectedText(project)
        
        if (selectedText.isNullOrEmpty()) {
            Messages.showErrorDialog(project, "Please select some code first", "No Code Selected")
            return
        }
        
        val language = EditorUtils.getLanguage(project) ?: "code"
        val prompt = promptBuilder(language, selectedText)
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val settings = VajraSettings.getInstance().state
                val providerManager = ProviderManager()
                val provider = providerManager.getProvider(settings.defaultProvider)
                
                if (provider == null || !provider.isConfigured()) {
                    Messages.showErrorDialog(
                        project,
                        "Please configure your API key in Settings > Tools > Vajra",
                        "Provider Not Configured"
                    )
                    return@launch
                }
                
                val response = withContext(Dispatchers.IO) {
                    provider.sendMessage(prompt, settings.defaultModel)
                }
                
                Messages.showInfoMessage(project, response, "$actionType Result")
                
            } catch (ex: Exception) {
                Messages.showErrorDialog(project, ex.message ?: "Unknown error", "Error")
            }
        }
    }
}

class ExplainCodeAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(e, "Explain") { language, code ->
            "Explain this $language code:\n```$language\n$code\n```"
        }
    }
}

class RefactorCodeAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(e, "Refactor") { language, code ->
            "Refactor this $language code for better readability and performance:\n```$language\n$code\n```"
        }
    }
}

class DebugCodeAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(e, "Debug") { language, code ->
            "Help me debug this $language code. Find potential issues and suggest fixes:\n```$language\n$code\n```"
        }
    }
}

class OptimizeCodeAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(e, "Optimize") { language, code ->
            "Optimize this $language code for better performance:\n```$language\n$code\n```"
        }
    }
}

class AddCommentsAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(e, "Add Comments") { language, code ->
            "Add comprehensive comments to this $language code:\n```$language\n$code\n```"
        }
    }
}

class GenerateTestsAction : CodeAction() {
    override fun actionPerformed(e: AnActionEvent) {
        executeCodeAction(e, "Generate Tests") { language, code ->
            "Generate unit tests for this $language code:\n```$language\n$code\n```"
        }
    }
}
