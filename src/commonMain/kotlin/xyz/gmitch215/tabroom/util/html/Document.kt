package xyz.gmitch215.tabroom.util.html

internal class Document(
    val url: String,
    val html: String
)

internal fun String.toDocument(url: String): Document = Document(url, this)

internal expect fun Document.querySelectorAll(selector: String): List<Element>
internal fun Document.querySelector(selector: String): Element? = querySelectorAll(selector).firstOrNull()
internal fun Document.getElementById(id: String): Element? = querySelector("#$id")
internal fun Document.getElementsByClassName(className: String): List<Element> = querySelectorAll(".$className")