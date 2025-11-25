package com.vajra.providers

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.vajra.config.VajraSettings
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class OpenAIProvider : AIProvider {
    override val name = "openai"
    override val displayName = "OpenAI"
    // November 2025 available models - using actual OpenAI API model names
    override val models = listOf(
        "gpt-4o",           // Most versatile, current flagship
        "gpt-4o-mini",      // Efficient variant  
        "gpt-4-turbo",      // Still available
        "o1-preview",       // Reasoning model
        "gpt-3.5-turbo"     // Budget-friendly
    )
    
    override fun isConfigured(): Boolean {
        return VajraSettings.getInstance().state.openaiApiKey.isNotEmpty()
    }
    
    override suspend fun sendMessage(message: String, model: String?): String {
        val apiKey = VajraSettings.getInstance().state.openaiApiKey
        if (apiKey.isEmpty()) throw Exception("OpenAI API key not configured")
        
        val client = OkHttpClient()
        val gson = Gson()
        
        val selectedModel = model ?: "gpt-4o"
        
        val requestBody = mapOf(
            "model" to selectedModel,
            "messages" to listOf(mapOf("role" to "user", "content" to message)),
            "temperature" to 0.7,
            "max_tokens" to 4096
        )
        
        val body = gson.toJson(requestBody)
            .toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(body)
            .build()
        
        return client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            if (!response.isSuccessful) {
                // Better error handling - show the actual error message from OpenAI
                val errorDetails = try {
                    val errorJson = gson.fromJson(responseBody, JsonObject::class.java)
                    errorJson.getAsJsonObject("error")?.get("message")?.asString
                        ?: responseBody
                } catch (e: Exception) {
                    responseBody
                }
                throw IOException("OpenAI API error: ${response.code} - $errorDetails")
            }
            val json = gson.fromJson(responseBody, JsonObject::class.java)
            json.getAsJsonArray("choices")
                .get(0).asJsonObject
                .getAsJsonObject("message")
                .get("content").asString
        }
    }
}

class AnthropicProvider : AIProvider {
    override val name = "anthropic"
    override val displayName = "Claude"
    // Updated to actual Anthropic API model names
    override val models = listOf(
        "claude-sonnet-4-20250514",     // Latest Sonnet
        "claude-3-5-sonnet-20241022",   // Claude 3.5 Sonnet
        "claude-3-opus-20240229"        // Claude 3 Opus
    )
    
    override fun isConfigured(): Boolean {
        return VajraSettings.getInstance().state.anthropicApiKey.isNotEmpty()
    }
    
    override suspend fun sendMessage(message: String, model: String?): String {
        val apiKey = VajraSettings.getInstance().state.anthropicApiKey
        if (apiKey.isEmpty()) throw Exception("Anthropic API key not configured")
        
        val client = OkHttpClient()
        val gson = Gson()
        
        val requestBody = mapOf(
            "model" to (model ?: "claude-sonnet-4-20250514"),
            "max_tokens" to 4096,
            "messages" to listOf(mapOf("role" to "user", "content" to message))
        )
        
        val body = gson.toJson(requestBody)
            .toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url("https://api.anthropic.com/v1/messages")
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .header("content-type", "application/json")
            .post(body)
            .build()
        
        return client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            if (!response.isSuccessful) {
                val errorDetails = try {
                    val errorJson = gson.fromJson(responseBody, JsonObject::class.java)
                    errorJson.getAsJsonObject("error")?.get("message")?.asString
                        ?: responseBody
                } catch (e: Exception) {
                    responseBody
                }
                throw IOException("Anthropic API error: ${response.code} - $errorDetails")
            }
            val json = gson.fromJson(responseBody, JsonObject::class.java)
            json.getAsJsonArray("content")
                .get(0).asJsonObject
                .get("text").asString
        }
    }
}

class QwenProvider : AIProvider {
    override val name = "qwen"
    override val displayName = "Qwen-Coder"
    override val models = listOf("qwen2.5-coder-32b-instruct", "qwen2.5-coder-7b-instruct")
    
    override fun isConfigured(): Boolean {
        return VajraSettings.getInstance().state.qwenApiKey.isNotEmpty()
    }
    
    override suspend fun sendMessage(message: String, model: String?): String {
        val apiKey = VajraSettings.getInstance().state.qwenApiKey
        if (apiKey.isEmpty()) throw Exception("Qwen API key not configured")
        
        val client = OkHttpClient()
        val gson = Gson()
        
        val requestBody = mapOf(
            "model" to (model ?: "qwen2.5-coder-7b-instruct"),
            "input" to mapOf(
                "messages" to listOf(mapOf("role" to "user", "content" to message))
            ),
            "parameters" to mapOf(
                "temperature" to 0.7,
                "max_tokens" to 4096
            )
        )
        
        val body = gson.toJson(requestBody)
            .toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(body)
            .build()
        
        return client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            if (!response.isSuccessful) {
                throw IOException("Qwen API error: ${response.code} - $responseBody")
            }
            val json = gson.fromJson(responseBody, JsonObject::class.java)
            json.getAsJsonObject("output")
                .getAsJsonArray("choices")
                .get(0).asJsonObject
                .getAsJsonObject("message")
                .get("content").asString
        }
    }
}

class OllamaProvider : AIProvider {
    override val name = "ollama"
    override val displayName = "Ollama (Local)"
    override val models = listOf("qwen2.5-coder:7b", "deepseek-coder-v2:16b", "codellama:34b")
    
    override fun isConfigured(): Boolean = true
    
    override suspend fun sendMessage(message: String, model: String?): String {
        val endpoint = VajraSettings.getInstance().state.ollamaEndpoint
        val client = OkHttpClient()
        val gson = Gson()
        
        val requestBody = mapOf(
            "model" to (model ?: "qwen2.5-coder:7b"),
            "prompt" to message,
            "stream" to false
        )
        
        val body = gson.toJson(requestBody)
            .toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url("$endpoint/api/generate")
            .post(body)
            .build()
        
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Ollama error: ${response.code}")
            val json = gson.fromJson(response.body?.string(), JsonObject::class.java)
            json.get("response").asString
        }
    }
}

class ProviderManager {
    private val providers = mapOf(
        "openai" to OpenAIProvider(),
        "anthropic" to AnthropicProvider(),
        "qwen" to QwenProvider(),
        "ollama" to OllamaProvider()
    )
    
    fun getProvider(name: String): AIProvider? = providers[name]
    
    fun getAllProviders(): List<AIProvider> = providers.values.toList()
    
    fun getConfiguredProviders(): List<AIProvider> = 
        providers.values.filter { it.isConfigured() }
}