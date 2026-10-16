package com.example.wordmemory.parser

import com.example.wordmemory.domain.model.ParsedWord
import com.example.wordmemory.data.local.room.dictionary.DictionaryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TextParserTest {

    private val mockDictionaryRepository = mockk<DictionaryRepository>(relaxed = true)
    private val mockCloudMatcher = mockk<CloudLLMMatcher>(relaxed = true)
    private val parser = TextParser(mockDictionaryRepository, mockCloudMatcher)

    @Test
    fun `test parsing with explicit labels`() = runTest {
        coEvery { mockDictionaryRepository.getMeaning("apple") } returns "사과"
        coEvery { mockDictionaryRepository.getMeaning("banana") } returns "바나나"
        coEvery { mockCloudMatcher.match(any(), any()) } returns emptyList()

        val input = """
            Word: apple
            Meaning: 사과
            Word: banana
            Meaning: 바나나
        """.trimIndent()
        val result = parser.parse(input)
        
        assertEquals(2, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
    }

    @Test
    fun `test parsing with multiple items and repeating labels`() = runTest {
        coEvery { mockDictionaryRepository.getMeaning("apple") } returns "사과"
        coEvery { mockDictionaryRepository.getMeaning("banana") } returns "바나나"
        coEvery { mockDictionaryRepository.getMeaning("orange") } returns "오렌지"
        coEvery { mockCloudMatcher.match(any(), any()) } returns emptyList()

        val input = """
            Word: apple
            Meaning: 사과
            Word: banana
            Meaning: 바나나
            Word: orange
            Meaning: 오렌지
        """.trimIndent()
        val result = parser.parse(input)
        
        assertEquals(3, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
        assertEquals("orange", result[2].word)
        assertEquals("오렌지", result[2].meaning)
    }

    @Test
    fun `test noise filtering with labels`() = runTest {
        coEvery { mockDictionaryRepository.getMeaning("apple") } returns "사과"
        coEvery { mockDictionaryRepository.getMeaning("banana") } returns "바나나"
        coEvery { mockDictionaryRepository.getMeaning("orange") } returns "오렌지"
        coEvery { mockCloudMatcher.match(any(), any()) } returns emptyList()

        val input = """
            Word: apple
            Meaning: 사과
            Page 2
            Word: banana
            Meaning: 바나나
            Some random text 123
            Word: orange
            Meaning: 오렌지
        """.trimIndent()
        val result = parser.parse(input)
        
        assertEquals(3, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
        assertEquals("orange", result[2].word)
        assertEquals("오렌지", result[2].meaning)
    }

    @Test
    fun `test empty input`() = runTest {
        val result = parser.parse("")
        assertEquals(0, result.size)
    }

    @Test
    fun `test ignoring text before first label`() = runTest {
        coEvery { mockDictionaryRepository.getMeaning(any()) } returns null
        coEvery { mockCloudMatcher.match(any(), any()) } returns listOf(
            ParsedWord("apple", "사과")
        )

        val input = "Some random noise\nWord: apple\nMeaning: 사과".replace("\\n", "\n")
        val result = parser.parse(input)
        
        assertEquals(1, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
    }
}