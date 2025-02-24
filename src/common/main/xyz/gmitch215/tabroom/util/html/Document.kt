package xyz.gmitch215.tabroom.util.html

internal class Document(
    val url: String,
    val html: String
) {

    val body: Element
        get() = querySelector("body") ?: throw IllegalStateException("Document does not have a body element")

    val bodyElements: String
        get() = body.innerHTML
            .replace(Regex("<script\\b[^>]*>([\\s\\S]*?)</script>"), "")
            .replace(Regex("<style\\b[^>]*>([\\s\\S]*?)</style>"), "")

    val head: Element
        get() = querySelector("head") ?: throw IllegalStateException("Document does not have a head element")

}

internal expect fun Document.querySelectorAll(selector: String): List<Element>
internal fun Document.querySelector(selector: String): Element? = querySelectorAll(selector).firstOrNull()
internal fun Document.getElementById(id: String): Element? = querySelector("#$id")
internal fun Document.getElementsByClassName(className: String): List<Element> = querySelectorAll(".$className")