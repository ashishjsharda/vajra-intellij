package com.vajra.config

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.*

class VajraConfigurable : Configurable {
    
    private val openaiKeyField = JBPasswordField()
    private val anthropicKeyField = JBPasswordField()
    private val qwenKeyField = JBPasswordField()
    private val deepseekKeyField = JBPasswordField()
    private val mistralKeyField = JBPasswordField()
    private val geminiKeyField = JBPasswordField()
    private val groqKeyField = JBPasswordField()
    private val ollamaEndpointField = JBTextField()
    private val defaultProviderCombo = JComboBox(arrayOf(
        "openai", "anthropic", "qwen", "deepseek", "mistral", 
        "gemini", "groq", "ollama", "openrouter", "huggingface"
    ))
    
    override fun getDisplayName(): String = "Vajra"
    
    override fun createComponent(): JComponent {
        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("OpenAI API Key:"), openaiKeyField, 1, false)
            .addLabeledComponent(JBLabel("Anthropic API Key:"), anthropicKeyField, 1, false)
            .addLabeledComponent(JBLabel("Qwen API Key:"), qwenKeyField, 1, false)
            .addLabeledComponent(JBLabel("DeepSeek API Key:"), deepseekKeyField, 1, false)
            .addLabeledComponent(JBLabel("Mistral API Key:"), mistralKeyField, 1, false)
            .addLabeledComponent(JBLabel("Gemini API Key:"), geminiKeyField, 1, false)
            .addLabeledComponent(JBLabel("Groq API Key:"), groqKeyField, 1, false)
            .addSeparator(5)
            .addLabeledComponent(JBLabel("Ollama Endpoint:"), ollamaEndpointField, 1, false)
            .addLabeledComponent(JBLabel("Default Provider:"), defaultProviderCombo, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
    
    override fun isModified(): Boolean {
        val settings = VajraSettings.getInstance().state
        return openaiKeyField.password.concatToString() != settings.openaiApiKey ||
                anthropicKeyField.password.concatToString() != settings.anthropicApiKey ||
                qwenKeyField.password.concatToString() != settings.qwenApiKey ||
                deepseekKeyField.password.concatToString() != settings.deepseekApiKey ||
                mistralKeyField.password.concatToString() != settings.mistralApiKey ||
                geminiKeyField.password.concatToString() != settings.geminiApiKey ||
                groqKeyField.password.concatToString() != settings.groqApiKey ||
                ollamaEndpointField.text != settings.ollamaEndpoint ||
                defaultProviderCombo.selectedItem != settings.defaultProvider
    }
    
    override fun apply() {
        val settings = VajraSettings.getInstance().state
        settings.openaiApiKey = openaiKeyField.password.concatToString()
        settings.anthropicApiKey = anthropicKeyField.password.concatToString()
        settings.qwenApiKey = qwenKeyField.password.concatToString()
        settings.deepseekApiKey = deepseekKeyField.password.concatToString()
        settings.mistralApiKey = mistralKeyField.password.concatToString()
        settings.geminiApiKey = geminiKeyField.password.concatToString()
        settings.groqApiKey = groqKeyField.password.concatToString()
        settings.ollamaEndpoint = ollamaEndpointField.text
        settings.defaultProvider = defaultProviderCombo.selectedItem as String
    }
    
    override fun reset() {
        val settings = VajraSettings.getInstance().state
        openaiKeyField.text = settings.openaiApiKey
        anthropicKeyField.text = settings.anthropicApiKey
        qwenKeyField.text = settings.qwenApiKey
        deepseekKeyField.text = settings.deepseekApiKey
        mistralKeyField.text = settings.mistralApiKey
        geminiKeyField.text = settings.geminiApiKey
        groqKeyField.text = settings.groqApiKey
        ollamaEndpointField.text = settings.ollamaEndpoint
        defaultProviderCombo.selectedItem = settings.defaultProvider
    }
}
