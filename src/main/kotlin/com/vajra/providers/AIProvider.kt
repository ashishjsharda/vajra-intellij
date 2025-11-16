package com.vajra.providers

interface AIProvider {
    val name: String
    val displayName: String
    val models: List<String>
    
    fun isConfigured(): Boolean
    suspend fun sendMessage(message: String, model: String? = null): String
}
