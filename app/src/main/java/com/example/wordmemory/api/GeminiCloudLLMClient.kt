package com.example.wordmemory.api

import com.example.wordmemory.domain.model.ParsedWord
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import android.util.Log

/**
 * Phase 3: Self-Healing Cloud LLM Client.
 * Automatically attempts multiple model names to bypass 404 errors 
 * and logs the successful model for future hardcoding.
 */
class GeminiCloudLLMClient(
    private val apiKey: String
) : CloudLLMClient {

    // Candidate models to try in order
    private val candidateModels = listOf(
        "gemini-3.8-flash",
        "gemini-3.5-flash-lite",
        "gemini-3.6-flash"
    )

    // Tracks the last successful model index to avoid re-probing failed models
    private var successfulModelIndex = -1

    // JSON parser for the LLM response
    private val json = Json

    override suspend fun match(
        englishWords: List<String>,
        koreanMeanings: List<String>
    ): List<ParsedWord> {
        if (englishWords.isEmpty() || koreanMeanings.isEmpty()) return emptyList()

        val prompt = """
            You are a precise translation and matching expert.
            I will provide two lists: a list of English words and a list of Korean meanings.
            Your task is to match them into pairs. 
            
            Rules:
            1. Only match words that have a clear semantic connection.
            2. If a word cannot be matched, do not include it in the output.
            3. Return ONLY a valid JSON array of objects. Each object must have "word" and "meaning" keys.
            4. Do not include any conversational text, markdown fences, or explanation.

            English words: ${englishWords.joinToString(", ")}
            Korean meanings: ${koreanMeanings.joinToString(", ")}

            Example Output Format:
            [
              {"word": "apple", "meaning": "사과"},
              {"word": "banana", "meaning": "바나나"}
            ]
        """.trimIndent()

        // Start trying from the last successful model, or from index 0
        val startIndex = if (successfulModelIndex >= 0) successfulModelIndex else 0

        for (i in startIndex until candidateModels.size) {
            val modelName = candidateModels[i]
            val model = GenerativeModel(
                modelName = modelName,
                apiKey = apiKey
            )

            Log.d("GeminiCloudLLMClient", "Attempting match with model: $modelName")

            try {
                val response = model.generateContent(prompt)
                val responseText = response.text ?: ""

                if (responseText.isEmpty()) {
                    throw Exception("Empty response from LLM")
                }

                Log.d("GeminiCloudLLMClient", "Received Response: $responseText")

                // Extract the JSON array using Regex to handle cases where the LLM adds conversational text
                val jsonRegex = Regex("\\[[\\s\\S]*\\]")
                val cleanedJson = jsonRegex.find(responseText)?.value ?: responseText

                val result = parseJsonResponse(cleanedJson)
                
                // If we got a non-empty result, we consider this model successful
                if (result.isNotEmpty()) {
                    successfulModelIndex = i
                    Log.i("GeminiCloudLLMClient", "SUCCESS! Working Model Found: $modelName")
                    return result
                } else {
                    Log.w("GeminiCloudLLMClient", "Model $modelName returned empty result. Trying next...")
                }

            } catch (e: Exception) {
                val message = e.message ?: ""
                // Check if it's a 404 / Not Found error (model name mismatch)
                if (message.contains("not found", ignoreCase = true) || message.contains("404")) {
                    Log.w("GeminiCloudLLMClient", "Model $modelName failed (404/Not Found). Trying next...")
                } else {
                    // For other errors, we now try the next model instead of breaking
                    Log.w("GeminiCloudLLMClient", "Model $modelName failed with error: $message. Trying next...")
                }
            }
        }

        Log.e("GeminiCloudLLMClient", "All candidate models failed to provide a match.")
        return emptyList()
    }

    private fun parseJsonResponse(jsonString: String): List<ParsedWord> {
        return try {
            val array = json.parseToJsonElement(jsonString).jsonArray
            array.map { element ->
                val obj = element.jsonObject
                ParsedWord(
                    word = obj["word"]?.jsonPrimitive?.content ?: "",
                    meaning = obj["meaning"]?.jsonPrimitive?.content ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("GeminiCloudLLMClient", "JSON Parsing Error: ${e.message}")
            emptyList()
        }
    }
}
