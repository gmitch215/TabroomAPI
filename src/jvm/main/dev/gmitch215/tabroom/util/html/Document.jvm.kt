package dev.gmitch215.tabroom.util.html

import org.jsoup.Jsoup

private fun org.jsoup.nodes.Element.convert(): Element {
    return Element(
        tagName = tagName(),
        innerHTML = html(),
        textContent = text(),
        attributes = attributes().asList().associate { it.key to it.value },
        children = children().map { it.convert() }
    )
}

internal actual fun Document.querySelectorAll(selector: String): List<Element> {
    val doc = Jsoup.parse(html)
    return doc.select(selector).map { it.convert() }
}