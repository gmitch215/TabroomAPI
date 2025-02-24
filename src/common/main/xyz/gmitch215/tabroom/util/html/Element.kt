package xyz.gmitch215.tabroom.util.html

internal class Element(
    val tagName: String,
    val innerHTML: String,
    val textContent: String,
    val attributes: Map<String, String>,
    val children: List<Element>
) {
    operator fun get(attribute: String): String? = attributes[attribute]
}