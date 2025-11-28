package com.vajra.ui

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.InlayPresentation
import com.intellij.codeInsight.hints.presentation.PresentationFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Provides inline AI suggestions and explanations directly in the editor
 * Similar to GitHub Copilot's inline experience
 */
@Suppress("UnstableApiUsage")
class VajraInlayProvider : InlayHintsProvider<NoSettings> {
    
    override val key: SettingsKey<NoSettings> = SettingsKey("vajra.hints")
    override val name: String = "Vajra AI Hints"
    override val previewText: String = "// AI suggestion will appear here"
    
    override fun createSettings(): NoSettings = NoSettings()
    
    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: NoSettings,
        sink: InlayHintsSink
    ): InlayHintsCollector {
        return VajraInlayCollector(editor)
    }
    
    override fun createConfigurable(settings: NoSettings): ImmediateConfigurable {
        return object : ImmediateConfigurable {
            override fun createComponent(listener: ChangeListener): JComponent = JPanel()
            override val mainCheckboxText: String = "Show Vajra AI hints inline"
        }
    }
}

@Suppress("UnstableApiUsage")
class VajraInlayCollector(private val editor: Editor) : FactoryInlayHintsCollector(editor) {
    
    override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
        // This will be populated by our actions when AI responses are ready
        return true
    }
}

/**
 * Stores AI responses to be shown as inline hints
 */
object VajraHintsCache {
    private val hints = mutableMapOf<Int, String>() // offset -> hint text
    
    fun addHint(offset: Int, text: String) {
        hints[offset] = text
    }
    
    fun getHint(offset: Int): String? = hints[offset]
    
    fun clearHints() {
        hints.clear()
    }
}
