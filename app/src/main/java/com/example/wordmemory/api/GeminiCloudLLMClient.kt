package com.example.wordmemory.api

import com.example.wordmemory.domain.model.ParsedWord
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Phase 3: Real Gemini Cloud LLM Client.
 * Uses Google's Generative AI SDK to perform semantic matching.
 */
class GeminiCloudLLMClient(
    private val apiKey: String
) : CloudLLMClient {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-pro",
        apiKey = apiKey
    )

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

        // Debug logging
        android.util.Log.d("GeminiCloudLLMClient", "Sending Prompt: $prompt")

        return try {
            val response = model.generateContent(prompt)
            val responseText = response.text ?: ""
            
            android.util.Log.d("GeminiCloudLLMClient", "Received Response: $responseText")

            // Extract the JSON array using Regex to handle cases where the LLM adds conversational text
            val jsonRegex = Regex("\\[[\\s\\S]*\\]")
            val cleanedJson = jsonRegex.find(responseText)?.value ?: responseText

            parseJsonResponse(cleanedJson)
        } catch (e: Exception) {
            android.util.Log.e("GeminiCloudLLMClient", "Error during match: ${e.message}")
            emptyList()
        }
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
            emptyList()
        }
    }
}
