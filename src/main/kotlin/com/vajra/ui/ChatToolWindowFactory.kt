package com.vajra.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import com.vajra.config.VajraSettings
import com.vajra.providers.ProviderManager
import com.vajra.utils.EditorUtils
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

class ChatToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val chatPanel = ChatPanel(project)
        val content = ContentFactory.getInstance().createContent(chatPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}

class ChatPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val messagesPanel = JPanel()
    private val scrollPane: JBScrollPane
    private val inputField = JTextArea()
    private val providerManager = ProviderManager()
    private val modelSelector = JComboBox<String>()

    // Modern Colors
    private val bgColor = JBUI.CurrentTheme.ToolWindow.background()

    init {
        background = bgColor

        // --- 1. Header (Model Selection) ---
        val headerPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(10)
            background = bgColor
            isOpaque = true
        }

        // Populate Model Selector
        updateModelList()
        modelSelector.addActionListener {
            val selected = modelSelector.selectedItem as? String
            if (selected != null) {
                // Parse "Provider: Model" format
                val parts = selected.split(":")
                if (parts.size >= 2) {
                    val settings = VajraSettings.getInstance().state
                    settings.defaultProvider = parts[0].trim().lowercase()
                    settings.defaultModel = parts[1].trim()
                }
            }
        }

        // Styled Combobox
        headerPanel.add(JLabel("Using: "), BorderLayout.WEST)
        headerPanel.add(modelSelector, BorderLayout.CENTER)

        // --- 2. Chat Area (Messages) ---
        messagesPanel.layout = BoxLayout(messagesPanel, BoxLayout.Y_AXIS)
        messagesPanel.background = bgColor
        messagesPanel.border = JBUI.Borders.empty(10)

        // Wrap in a wrapper panel to push messages to the top
        val messagesWrapper = JPanel(BorderLayout())
        messagesWrapper.background = bgColor
        messagesWrapper.add(messagesPanel, BorderLayout.NORTH)

        scrollPane = JBScrollPane(messagesWrapper).apply {
            border = JBUI.Borders.empty()
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        }

        // --- 3. Input Area ---
        val inputPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(15)
            background = bgColor
        }

        val inputContainer = JPanel(BorderLayout()).apply {
            // FIXED: Used JBColor.border() instead of unresolved JBUI.CurrentTheme.Component.borderColor()
            border = BorderFactory.createCompoundBorder(
                RoundedBorder(JBColor.border(), 10),
                JBUI.Borders.empty(5, 10, 5, 10)
            )
            background = JBColor(Color.WHITE, Color(0x2B2B2B))
        }

        inputField.apply {
            lineWrap = true
            wrapStyleWord = true
            rows = 3
            background = JBColor(Color.WHITE, Color(0x2B2B2B))
            border = JBUI.Borders.empty()
            font = JBUI.Fonts.label(13f)
        }

        // Submit on Shift+Enter, Newline on Enter (or vice versa)
        inputField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER && !e.isShiftDown) {
                    e.consume()
                    sendMessage()
                }
            }
        })

        val sendButton = JButton("➤").apply {
            preferredSize = Dimension(40, 40)
            isContentAreaFilled = false
            border = JBUI.Borders.empty()
            foreground = JBUI.CurrentTheme.Link.Foreground.ENABLED
            addActionListener { sendMessage() }
        }

        inputContainer.add(inputField, BorderLayout.CENTER)
        inputContainer.add(sendButton, BorderLayout.EAST)

        inputPanel.add(inputContainer, BorderLayout.CENTER)

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
        add(inputPanel, BorderLayout.SOUTH)

        // Welcome Message
        addMessage("Vajra", "Hi! I'm Vajra. Select a model above and ask me anything.", false)
    }

    private fun updateModelList() {
        modelSelector.removeAllItems()
        val providers = providerManager.getAllProviders()
        val settings = VajraSettings.getInstance().state

        var currentSelectionIndex = 0
        var index = 0

        providers.forEach { provider ->
            provider.models.forEach { model ->
                val item = "${provider.name}: $model"
                modelSelector.addItem(item)

                // Try to match current settings
                if (provider.name == settings.defaultProvider && model == settings.defaultModel) {
                    currentSelectionIndex = index
                }
                index++
            }
        }

        if (modelSelector.itemCount > 0) {
            modelSelector.selectedIndex = currentSelectionIndex
        }
    }

    private fun sendMessage() {
        val message = inputField.text.trim()
        if (message.isEmpty()) return

        inputField.text = ""
        addMessage("You", message, true)

        // Loading state
        val loadingBubble = addMessage("Vajra", "Thinking...", false)

        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val settings = VajraSettings.getInstance().state
                val provider = providerManager.getProvider(settings.defaultProvider)

                if (provider == null || !provider.isConfigured()) {
                    SwingUtilities.invokeLater {
                        updateMessageContent(loadingBubble, "Error: Please configure ${settings.defaultProvider} API key in Settings.")
                    }
                    return@executeOnPooledThread
                }

                // Context Awareness
                var contextMessage = message
                val selectedText = EditorUtils.getSelectedText(project)
                if (selectedText != null) {
                    val lang = EditorUtils.getLanguage(project) ?: "code"
                    contextMessage = """
                        I am working on this $lang code:
                        ```$lang
                        $selectedText
                        ```
                        
                        $message
                    """.trimIndent()
                }

                val response = runBlocking {
                    provider.sendMessage(contextMessage, settings.defaultModel)
                }

                SwingUtilities.invokeLater {
                    updateMessageContent(loadingBubble, response)
                }
            } catch (e: Exception) {
                SwingUtilities.invokeLater {
                    updateMessageContent(loadingBubble, "Error: ${e.message}")
                }
            }
        }
    }

    private fun addMessage(sender: String, content: String, isUser: Boolean): ChatBubblePanel {
        val bubble = ChatBubblePanel(sender, content, isUser)
        messagesPanel.add(bubble)
        messagesPanel.add(Box.createVerticalStrut(10)) // Spacing

        messagesPanel.revalidate()
        messagesPanel.repaint()

        // Auto scroll to bottom
        SwingUtilities.invokeLater {
            val vertical = scrollPane.verticalScrollBar
            vertical.value = vertical.maximum
        }

        return bubble
    }

    private fun updateMessageContent(bubble: ChatBubblePanel, newContent: String) {
        bubble.updateContent(newContent)
        messagesPanel.revalidate()
        messagesPanel.repaint()
    }
}