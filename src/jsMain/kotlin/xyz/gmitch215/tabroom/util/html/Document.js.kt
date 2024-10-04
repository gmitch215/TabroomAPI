package xyz.gmitch215.tabroom.util.html

import kotlinx.browser.window
import org.w3c.dom.asList

private fun org.w3c.dom.Element.convert(): Element {
    return Element(
        tagName = nodeName,
        innerHTML = innerHTML,
        textContent = textContent ?: "",
        attributes = attributes.asList().associate { it.name to it.value },
        children = children.asList().map { it.convert() }
    )
}

internal actual fun Document.querySelectorAll(selector: String): List<Element> {
    val doc = window.document.createElement("html")
    doc.innerHTML = html

    return doc.querySelectorAll(selector).asList().filterIsInstance<org.w3c.dom.Element>().map { it.convert() }
}