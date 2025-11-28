package com.vajra.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import java.awt.Font
import javax.swing.SwingUtilities

/**
 * Service to display AI responses inline in the editor
 * Similar to Cursor's inline diff view and GitHub Copilot's ghost text
 */
class VajraInlineService {
    
    companion object {
        fun getInstance(): VajraInlineService = 
            ApplicationManager.getApplication().getService(VajraInlineService::class.java)
    }
    
    /**
     * Show AI response as ghost text inline (like Copilot)
     */
    fun showGhostText(editor: Editor, text: String, offset: Int) {
        SwingUtilities.invokeLater {
            val document = editor.document
            val line = document.getLineNumber(offset)
            val lineEndOffset = document.getLineEndOffset(line)
            
            // Create ghost text appearance
            val textAttributes = TextAttributes().apply {
                foregroundColor = JBColor.GRAY
                fontType = Font.ITALIC
            }
            
            // Add as inline hint
            val inlayModel = editor.inlayModel
            val inlay = inlayModel.addInlineElement(
                lineEndOffset,
                true,
                VajraInlineRenderer(text)
            )
            
            // Auto-remove after 30 seconds
            ApplicationManager.getApplication().invokeLater({
                inlay?.dispose()
            }, com.intellij.openapi.application.ModalityState.nonModal())
        }
    }
    
    /**
     * Show AI response as a diff-like overlay (like Cursor)
     */
    fun showInlineDiff(
        project: Project,
        editor: Editor, 
        originalText: String,
        aiSuggestion: String,
        startOffset: Int,
        endOffset: Int
    ) {
        SwingUtilities.invokeLater {
            val markupModel = editor.markupModel
            
            // Highlight the original code
            val highlighter = markupModel.addRangeHighlighter(
                startOffset,
                endOffset,
                HighlighterLayer.SELECTION,
                TextAttributes().apply {
                    backgroundColor = JBColor(0xE8F5E9, 0x1B5E20)
                },
                HighlighterTargetArea.EXACT_RANGE
            )
            
            // Show the suggestion in a balloon popup
            showInlinePopup(editor, aiSuggestion, startOffset, {
                // Accept callback
                WriteCommandAction.runWriteCommandAction(project) {
                    editor.document.replaceString(startOffset, endOffset, aiSuggestion)
                    highlighter.dispose()
                }
            }, {
                // Reject callback
                highlighter.dispose()
            })
        }
    }
    
    /**
     * Show popup with Accept/Reject buttons
     */
    private fun showInlinePopup(
        editor: Editor,
        suggestion: String,
        offset: Int,
        onAccept: () -> Unit,
        onReject: () -> Unit
    ) {
        val panel = VajraInlinePopupPanel(suggestion, onAccept, onReject)
        
        val point = editor.offsetToXY(offset)
        val component = editor.contentComponent
        
        val popup = com.intellij.openapi.ui.popup.JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null)
            .setRequestFocus(false)
            .setFocusable(true)
            .setResizable(true)
            .setMovable(true)
            .setTitle("Vajra Suggestion")
            .setCancelOnClickOutside(true)
            .setCancelCallback {
                onReject()
                true
            }
            .createPopup()
        
        popup.show(RelativePoint(component, point))
    }
    
    /**
     * Show explanation as a documentation-style popup
     */
    fun showExplanationPopup(editor: Editor, explanation: String, offset: Int) {
        SwingUtilities.invokeLater {
            val panel = VajraExplanationPanel(explanation)
            
            val point = editor.offsetToXY(offset)
            val component = editor.contentComponent
            
            val popup = com.intellij.openapi.ui.popup.JBPopupFactory.getInstance()
                .createComponentPopupBuilder(panel, null)
                .setRequestFocus(false)
                .setFocusable(true)
                .setResizable(true)
                .setMovable(true)
                .setTitle("Vajra Explanation")
                .setCancelOnClickOutside(true)
                .createPopup()
            
            popup.show(RelativePoint(component, point))
        }
    }
    
    /**
     * Add a gutter icon with AI suggestion indicator
     */
    fun addGutterIcon(editor: Editor, line: Int, tooltip: String) {
        SwingUtilities.invokeLater {
            val document = editor.document
            val startOffset = document.getLineStartOffset(line)
            
            val markupModel = editor.markupModel
            val highlighter = markupModel.addLineHighlighter(
                line,
                HighlighterLayer.ADDITIONAL_SYNTAX,
                null
            )
            
            // Add icon in gutter
            highlighter?.gutterIconRenderer = VajraGutterIconRenderer(tooltip)
        }
    }
}
