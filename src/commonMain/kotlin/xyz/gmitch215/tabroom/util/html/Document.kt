package xyz.gmitch215.tabroom.util.html

internal class Document(
    val url: String,
    val html: String
)

internal expect fun Document.querySelectorAll(selector: String): List<Element>
internal fun Document.querySelector(selector: String): Element? = querySelectorAll(selector).firstOrNull()