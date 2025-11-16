package com.vajra.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.components.JBScrollPane
import com.vajra.config.VajraSettings
import com.vajra.providers.ProviderManager
import com.vajra.utils.EditorUtils
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
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
    
    private val chatArea = JTextPane()
    private val inputField = JTextField()
    private val sendButton = JButton("Send")
    private val providerManager = ProviderManager()
    
    init {
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        // Chat display
        chatArea.isEditable = false
        chatArea.contentType = "text/html"
        val scrollPane = JBScrollPane(chatArea)
        add(scrollPane, BorderLayout.CENTER)
        
        // Input panel
        val inputPanel = JPanel(BorderLayout())
        inputPanel.add(inputField, BorderLayout.CENTER)
        inputPanel.add(sendButton, BorderLayout.EAST)
        add(inputPanel, BorderLayout.SOUTH)
        
        // Welcome message
        appendMessage("Vajra", "Hi! I'm Vajra, your AI coding assistant. How can I help you today?")
    }
    
    private fun setupListeners() {
        sendButton.addActionListener { sendMessage() }
        
        inputField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    sendMessage()
                }
            }
        })
    }
    
    private fun sendMessage() {
        val message = inputField.text.trim()
        if (message.isEmpty()) return
        
        inputField.text = ""
        appendMessage("You", message)
        
        // Run in background thread
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val settings = VajraSettings.getInstance().state
                val provider = providerManager.getProvider(settings.defaultProvider)
                    ?: throw Exception("Provider ${settings.defaultProvider} not found")
                
                if (!provider.isConfigured()) {
                    SwingUtilities.invokeLater {
                        appendMessage("Vajra", "Please configure your API key in Settings > Tools > Vajra")
                    }
                    return@executeOnPooledThread
                }
                
                // Add code context if available
                var contextualMessage = message
                val selectedText = EditorUtils.getSelectedText(project)
                if (selectedText != null) {
                    val language = EditorUtils.getLanguage(project) ?: "code"
                    contextualMessage = "Here's my $language code:\n```$language\n$selectedText\n```\n\nQuestion: $message"
                }
                
                // Make synchronous call (provider methods are suspend, but we'll make them blocking)
                val response = runBlocking {
                    provider.sendMessage(contextualMessage, settings.defaultModel)
                }
                
                SwingUtilities.invokeLater {
                    appendMessage("Vajra", response)
                }
                
            } catch (e: Exception) {
                SwingUtilities.invokeLater {
                    appendMessage("Error", e.message ?: "Unknown error occurred")
                }
            }
        }
    }
    
    private fun appendMessage(sender: String, message: String) {
        val doc = chatArea.styledDocument
        val senderStyle = if (sender == "You") "color: #4CAF50;" else if (sender == "Error") "color: #F44336;" else "color: #2196F3;"
        
        val html = """
            <div style="margin: 10px; padding: 10px; background-color: #f5f5f5; border-radius: 5px;">
                <strong style="$senderStyle">$sender:</strong><br/>
                <div style="margin-top: 5px;">
                    ${message.replace("\n", "<br/>")}
                </div>
            </div>
        """.trimIndent()
        
        val currentHtml = chatArea.text
        chatArea.text = currentHtml + html
        chatArea.caretPosition = doc.length
    }
}
