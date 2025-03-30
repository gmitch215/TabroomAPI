package dev.gmitch215.tabroom.util.html

import com.fleeksoft.ksoup.Ksoup

private fun com.fleeksoft.ksoup.nodes.Element.convert(): Element {
    return Element(
        tagName(),
        html(),
        text(),
        attributes().associate { it.key to it.value },
        children().map { it.convert() }
    )
}

internal actual fun Document.querySelectorAll(selector: String): List<Element> {
    val doc = Ksoup.parse(html)
    return doc.select(selector).map { it.convert() }
}