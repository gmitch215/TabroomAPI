package xyz.gmitch215.tabroom.util.html

internal interface Element {

    val tagName: String
    val innerHTML: String
    val textContent: String
    val attributes: Map<String, String>

    operator fun get(attribute: String): String? = attributes[attribute]
}