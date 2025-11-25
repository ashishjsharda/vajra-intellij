package com.vajra.config

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@Service
@State(name = "VajraSettings", storages = [Storage("vajra.xml")])
class VajraSettings : PersistentStateComponent<VajraSettings.State> {
    
    data class State(
        var openaiApiKey: String = "",
        var anthropicApiKey: String = "",
        var qwenApiKey: String = "",
        var deepseekApiKey: String = "",
        var mistralApiKey: String = "",
        var geminiApiKey: String = "",
        var groqApiKey: String = "",
        var openrouterApiKey: String = "",
        var huggingfaceApiKey: String = "",
        var ollamaEndpoint: String = "http://localhost:11434",
        var defaultProvider: String = "openai",  // Changed to openai
        var defaultModel: String = "gpt-4o",     // Updated to actual model name
        var temperature: Double = 0.7,
        var maxTokens: Int = 4096,
        var enableCostTracking: Boolean = false
    )
    
    private var state = State()
    
    override fun getState(): State = state
    
    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, this.state)
    }
    
    companion object {
        fun getInstance(): VajraSettings = service()
    }
}