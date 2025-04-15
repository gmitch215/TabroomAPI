package dev.gmitch215.tabroom.util.html

import dev.gmitch215.tabroom.util.fetchDocument
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestDocument {

    @Test
    fun testDocument() = runTest {
        val doc = "https://example.com".fetchDocument(false)

        assertEquals("https://example.com", doc.url)
        assertFalse { doc.html.isEmpty() }

        assertEquals("head", doc.head.tagName)
        assertFalse { doc.head.innerHTML.isEmpty() }

        assertEquals("body", doc.body.tagName)
        assertFalse { doc.body.innerHTML.isEmpty() }
        assertFalse { doc.bodyElements.isEmpty() }

        assertNull(doc.getElementById("nonexistent"))
        assertTrue { doc.getElementsByClassName("nonexistent").isEmpty() }
        assertNull(doc.inputValue("nonexistent"))
    }

}