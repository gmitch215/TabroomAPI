package xyz.gmitch215.tabroom.util.html

import org.jsoup.Jsoup

internal actual fun Document.querySelectorAll(selector: String): List<Element> {
    val doc = Jsoup.parse(html)
    return doc.select(selector).map { element ->
        Element(
            tagName = element.tagName(),
            innerHTML = element.html(),
            textContent = element.text(),
            attributes = element.attributes().asList().associate { it.key to it.value }
        )
    }
}