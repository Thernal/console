package io.thernal.console.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonFormattingTest {

    @Test
    fun `pretty prints a flat object`() {
        val pretty = """{"id":42,"name":"John"}""".toPrettyJsonOrNull()

        val expected = """
            {
              "id": 42,
              "name": "John"
            }
        """.trimIndent()
        assertEquals(expected, pretty)
    }

    @Test
    fun `pretty prints nested objects and arrays`() {
        val pretty = """{"a":1,"b":[2,3],"c":{"d":true}}""".toPrettyJsonOrNull()

        val expected = """
            {
              "a": 1,
              "b": [
                2,
                3
              ],
              "c": {
                "d": true
              }
            }
        """.trimIndent()
        assertEquals(expected, pretty)
    }

    @Test
    fun `keeps empty object and array inline`() {
        val pretty = """{"obj":{},"arr":[]}""".toPrettyJsonOrNull()

        val expected = """
            {
              "obj": {},
              "arr": []
            }
        """.trimIndent()
        assertEquals(expected, pretty)
    }

    @Test
    fun `formats a top-level array`() {
        val pretty = """[1,"two",null]""".toPrettyJsonOrNull()

        val expected = """
            [
              1,
              "two",
              null
            ]
        """.trimIndent()
        assertEquals(expected, pretty)
    }

    @Test
    fun `preserves special characters inside strings`() {
        val pretty = """{"text":"a,b:{}[] \"quoted\""}""".toPrettyJsonOrNull()

        val expected = """
            {
              "text": "a,b:{}[] \"quoted\""
            }
        """.trimIndent()
        assertEquals(expected, pretty)
    }

    @Test
    fun `accepts already formatted input`() {
        val input = """
            {
                "a": 1
            }
        """.trimIndent()

        val expected = """
            {
              "a": 1
            }
        """.trimIndent()
        assertEquals(expected, input.toPrettyJsonOrNull())
    }

    @Test
    fun `returns null for malformed json`() {
        assertNull("""{"a":}""".toPrettyJsonOrNull())
        assertNull("""{"a":1,}""".toPrettyJsonOrNull())
        assertNull("""{"a" 1}""".toPrettyJsonOrNull())
        assertNull("""{"a":1""".toPrettyJsonOrNull())
        assertNull("""{"a":1}trailing""".toPrettyJsonOrNull())
    }

    @Test
    fun `returns null for non-json text`() {
        assertNull("plain text body".toPrettyJsonOrNull())
        assertNull("".toPrettyJsonOrNull())
        assertNull("42".toPrettyJsonOrNull())
    }
}
