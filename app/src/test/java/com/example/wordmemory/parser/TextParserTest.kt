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
    fun `test core parsing with simple input`() = runTest {
        coEvery { mockDictionaryRepository.getMeaning("apple") } returns "사과"
        coEvery { mockDictionaryRepository.getMeaning("banana") } returns "바나나"
        coEvery { mockCloudMatcher.match(any(), any()) } returns emptyList()

        // New logic requires line-based separation for phrases
        val input = "apple\n사과\nbanana\n바나나"
        val result = parser.parse(input)
        
        assertEquals(2, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
    }

    @Test
    fun `test core parsing with newline input`() = runTest {
        coEvery { mockDictionaryRepository.getMeaning("apple") } returns "사과"
        coEvery { mockDictionaryRepository.getMeaning("banana") } returns "바나나"
        coEvery { mockCloudMatcher.match(any(), any()) } returns emptyList()

        val input = "apple\n사과\nbanana\n바나나"
        val result = parser.parse(input)
        
        assertEquals(2, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
    }

    @Test
    fun `test core parsing with mixed noise`() = runTest {
        coEvery { mockDictionaryRepository.getMeaning("apple") } returns "사과"
        coEvery { mockDictionaryRepository.getMeaning("banana") } returns "바나나"
        coEvery { mockCloudMatcher.match(any(), any()) } returns emptyList()

        // Noise should be cleaned, and each pair should be on its own line
        val input = "!!!apple...\n사과!!!\nbanana\n바나나"
        val result = parser.parse(input)
        
        assertEquals(2, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
    }

    @Test
    fun `test empty input`() = runTest {
        val result = parser.parse("")
        assertEquals(0, result.size)
    }
}
