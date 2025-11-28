package com.vajra.ui

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.*
import javax.swing.*

/**
 * Renders ghost text inline in the editor (like GitHub Copilot)
 */
class VajraInlineRenderer(private val text: String) : EditorCustomElementRenderer {
    
    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val editor = inlay.editor
        val fontMetrics = editor.contentComponent.getFontMetrics(
            editor.colorsScheme.getFont(EditorFontType.PLAIN)
        )
        return fontMetrics.stringWidth(text)
    }
    
    override fun paint(
        inlay: Inlay<*>,
        g: Graphics,
        targetRect: Rectangle,
        textAttributes: com.intellij.openapi.editor.markup.TextAttributes
    ) {
        val g2d = g as Graphics2D
        val editor = inlay.editor
        
        // Set ghost text appearance
        g2d.color = JBColor.GRAY
        g2d.font = editor.colorsScheme.getFont(EditorFontType.ITALIC)
        
        // Draw the text
        val fontMetrics = g2d.fontMetrics
        g2d.drawString(
            text,
            targetRect.x,
            targetRect.y + fontMetrics.ascent
        )
    }
}

/**
 * Panel for inline popup with Accept/Reject buttons
 */
class VajraInlinePopupPanel(
    suggestion: String,
    onAccept: () -> Unit,
    onReject: () -> Unit
) : JPanel(BorderLayout()) {
    
    init {
        border = JBUI.Borders.empty(10)
        preferredSize = Dimension(500, 300)
        
        // Suggestion text area
        val textArea = JTextArea(suggestion).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            font = Font("Monospaced", Font.PLAIN, 12)
            background = JBColor(0xF5F5F5, 0x2B2B2B)
            border = JBUI.Borders.empty(5)
        }
        
        val scrollPane = JBScrollPane(textArea)
        add(scrollPane, BorderLayout.CENTER)
        
        // Buttons panel
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
            val acceptButton = JButton("Accept (Tab)").apply {
                addActionListener {
                    onAccept()
                    SwingUtilities.getWindowAncestor(this@VajraInlinePopupPanel)?.let {
                        if (it is Window) it.dispose()
                    }
                }
            }
            
            val rejectButton = JButton("Reject (Esc)").apply {
                addActionListener {
                    onReject()
                    SwingUtilities.getWindowAncestor(this@VajraInlinePopupPanel)?.let {
                        if (it is Window) it.dispose()
                    }
                }
            }
            
            add(acceptButton)
            add(rejectButton)
        }
        
        add(buttonPanel, BorderLayout.SOUTH)
        
        // Keyboard shortcuts
        registerKeyboardAction(
            { onAccept() },
            KeyStroke.getKeyStroke("TAB"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )
        
        registerKeyboardAction(
            { onReject() },
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )
    }
}

/**
 * Panel for showing explanations
 */
class VajraExplanationPanel(explanation: String) : JPanel(BorderLayout()) {
    
    init {
        border = JBUI.Borders.empty(10)
        preferredSize = Dimension(600, 400)
        
        // Explanation text pane with HTML support
        val textPane = JEditorPane("text/html", formatExplanation(explanation)).apply {
            isEditable = false
            background = JBColor(0xFFFFFF, 0x2B2B2B)
            border = JBUI.Borders.empty(5)
        }
        
        val scrollPane = JBScrollPane(textPane)
        add(scrollPane, BorderLayout.CENTER)
        
        // Close button
        val closeButton = JButton("Close").apply {
            addActionListener {
                SwingUtilities.getWindowAncestor(this@VajraExplanationPanel)?.let {
                    if (it is Window) it.dispose()
                }
            }
        }
        
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        buttonPanel.add(closeButton)
        add(buttonPanel, BorderLayout.SOUTH)
    }
    
    private fun formatExplanation(text: String): String {
        return """
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; padding: 10px; }
                    code { background-color: #f0f0f0; padding: 2px 4px; border-radius: 3px; }
                    pre { background-color: #f5f5f5; padding: 10px; border-radius: 5px; overflow-x: auto; }
                </style>
            </head>
            <body>
                ${text.replace("\n", "<br/>")}
            </body>
            </html>
        """.trimIndent()
    }
}

/**
 * Gutter icon renderer for AI suggestions
 */
class VajraGutterIconRenderer(private val tooltip: String) : GutterIconRenderer() {
    
    private val icon = com.intellij.ui.IconManager.getInstance().getIcon(
        "/icons/vajra-icon.svg",
        VajraGutterIconRenderer::class.java
    )
    
    override fun getIcon(): Icon = icon
    
    override fun getTooltipText(): String = tooltip
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VajraGutterIconRenderer) return false
        return tooltip == other.tooltip
    }
    
    override fun hashCode(): Int = tooltip.hashCode()
}
