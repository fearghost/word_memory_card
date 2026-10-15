package com.example.wordmemory.parser

import com.example.wordmemory.domain.model.ParsedWord
import org.junit.Assert.assertEquals
import org.junit.Test

class TextParserTest {

    private val parser = TextParser()

    @Test
    fun `test parsing with colon separator`() {
        val input = "apple: 사과\nbanana: 바나나"
        val result = parser.parse(input)
        assertEquals(2, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
    }

    @Test
    fun `test parsing with hyphen separator`() {
        val input = "apple - 사과\nbanana - 바나나"
        val result = parser.parse(input)
        assertEquals(2, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
    }

    @Test
    fun `test parsing with mixed separators`() {
        val input = "apple: 사과\nbanana - 바나나\norange | 오렌지\ngrape = 포도"
        val result = parser.parse(input)
        assertEquals(4, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
        assertEquals("orange", result[2].word)
        assertEquals("오렌지", result[2].meaning)
        assertEquals("grape", result[3].word)
        assertEquals("포도", result[3].meaning)
    }

    @Test
    fun `test empty input`() {
        val result = parser.parse("")
        assertEquals(0, result.size)
    }

    @Test
    fun `test invalid line`() {
        val input = "invalidline\napple: 사과"
        val result = parser.parse(input)
        assertEquals(2, result.size)
        assertEquals("invalidline", result[0].word)
        assertEquals("apple", result[1].word)
    }
}
