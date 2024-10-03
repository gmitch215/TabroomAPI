package xyz.gmitch215.tabroom.util.html

import com.fleeksoft.ksoup.Ksoup

internal actual fun Document.querySelectorAll(selector: String): List<Element> {
    val doc = Ksoup.parse(selector)
    return doc.select(selector).map { element ->
        Element(
            element.tagName(),
            element.html(),
            element.text(),
            element.attributes().associate { it.key to it.value }
        )
    }
}