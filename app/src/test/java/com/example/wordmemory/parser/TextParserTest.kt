package com.example.wordmemory.parser

import com.example.wordmemory.domain.model.ParsedWord
import org.junit.Assert.assertEquals
import org.junit.Test

class TextParserTest {

    private val parser = TextParser()

    @Test
    fun `test parsing with explicit labels`() {
        // 기본 라벨 기반 파싱 테스트
        val input = "Word: apple\nMeaning: 사과\nWord: banana\nMeaning: 바나나"
        val result = parser.parse(input)
        assertEquals(2, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
        assertEquals("banana", result[1].word)
        assertEquals("바나나", result[1].meaning)
    }

    @Test
    fun `test parsing with multiple items and repeating labels`() {
        // 라벨이 반복되는 구조의 다중 아이템 테스트
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
    fun `test noise filtering with labels`() {
        // 라벨 사이에 잡음(예: 페이지 번호)이 섞여 있어도 단어/뜻만 정확히 추출하는지 테스트
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
    fun `test empty input`() {
        // 빈 입력에 대한 처리
        val result = parser.parse("")
        assertEquals(0, result.size)
    }

    @Test
    fun `test ignoring text before first label`() {
        // 첫 번째 라벨이 나오기 전의 텍스트는 무시되는지 테스트
        val input = "Some random noise\nWord: apple\nMeaning: 사과"
        val result = parser.parse(input)
        assertEquals(1, result.size)
        assertEquals("apple", result[0].word)
        assertEquals("사과", result[0].meaning)
    }
}
