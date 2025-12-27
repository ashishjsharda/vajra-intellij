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
import java.awt.geom.RoundRectangle2D
import javax.swing.*
import javax.swing.border.AbstractBorder

// --- NEW: Modern Chat Components ---

class ChatBubblePanel(private val sender: String, content: String, private val isUser: Boolean) : JPanel(BorderLayout()) {

    private val contentArea = JEditorPane()

    init {
        isOpaque = false
        border = JBUI.Borders.empty(0, 10)

        // Container for the bubble to control alignment
        val bubbleContainer = JPanel(BorderLayout())
        bubbleContainer.isOpaque = false

        // The actual bubble
        val bubble = object : JPanel(BorderLayout()) {
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                // Modern Colors
                if (isUser) {
                    g2.color = JBColor(Color(0xE3F2FD), Color(0x214283)) // Blue-ish for user
                } else {
                    g2.color = JBColor(Color(0xF5F5F5), Color(0x3C3F41)) // Gray for AI
                }

                g2.fillRoundRect(0, 0, width, height, 16, 16)
                super.paintComponent(g)
            }
        }
        bubble.isOpaque = false
        bubble.border = JBUI.Borders.empty(10)

        // Sender Label (Small text above bubble)
        val senderLabel = JLabel(sender)
        senderLabel.font = JBUI.Fonts.miniFont()
        senderLabel.foreground = JBColor.GRAY
        senderLabel.border = JBUI.Borders.empty(0, 5, 2, 5)

        // Content
        contentArea.contentType = "text/html"
        contentArea.isEditable = false
        contentArea.isOpaque = false
        contentArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)
        contentArea.font = JBUI.Fonts.label(13f)
        updateContent(content)

        bubble.add(contentArea, BorderLayout.CENTER)

        // Layout logic
        val verticalBox = Box.createVerticalBox()

        if (isUser) {
            senderLabel.horizontalAlignment = SwingConstants.RIGHT
            verticalBox.add(senderLabel)

            val row = JPanel(FlowLayout(FlowLayout.RIGHT))
            row.isOpaque = false
            row.add(bubble)
            verticalBox.add(row)
        } else {
            senderLabel.horizontalAlignment = SwingConstants.LEFT
            verticalBox.add(senderLabel)

            val row = JPanel(FlowLayout(FlowLayout.LEFT))
            row.isOpaque = false
            row.add(bubble)
            verticalBox.add(row)
        }

        add(verticalBox, BorderLayout.CENTER)
    }

    fun updateContent(text: String) {
        // Simple Markdown-ish to HTML conversion
        val style = """
            <style>
                body { font-family: ${if (isUser) "sans-serif" else "Consolas, monospace"}; color: ${if (isUser) "#000000" else "#A9B7C6"}; }
                code { background-color: #A9B7C6; color: #000; padding: 2px; }
                pre { background-color: #2b2b2b; color: #a9b7c6; padding: 5px; border-radius: 5px; }
                p { margin: 0; padding: 0; }
            </style>
        """.trimIndent()

        val htmlContent = text
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\n", "<br/>")
            .replace(Regex("```(.*?)<br/>(.*?)```"), "<pre>$2</pre>") // Basic code block

        contentArea.text = "<html><head>$style</head><body>$htmlContent</body></html>"
    }
}

class RoundedBorder(private val color: Color, private val radius: Int) : AbstractBorder() {
    override fun paintBorder(c: Component?, g: Graphics?, x: Int, y: Int, width: Int, height: Int) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = color
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
    }

    override fun getBorderInsets(c: Component?): Insets {
        return Insets(radius / 2, radius / 2, radius / 2, radius / 2)
    }

    override fun getBorderInsets(c: Component?, insets: Insets): Insets {
        insets.left = radius / 2
        insets.top = radius / 2
        insets.right = radius / 2
        insets.bottom = radius / 2
        return insets
    }
}

// --- EXISTING: Inline Rendering (Kept as is for Ghost Text) ---

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