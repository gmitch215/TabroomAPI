package xyz.gmitch215.tabroom.util.html

import kotlinx.browser.window
import org.w3c.dom.asList

internal actual fun Document.querySelectorAll(selector: String): List<Element> {
    val doc = window.document.createElement("html")
    doc.innerHTML = html

    return doc.querySelectorAll(selector).asList().filterIsInstance<org.w3c.dom.Element>().map { element ->
        Element(
            tagName = element.nodeName,
            innerHTML = element.innerHTML,
            textContent = element.textContent ?: "",
            attributes = element.attributes.asList().associate { it.name to it.value }
        )
    }
}