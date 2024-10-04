package xyz.gmitch215.tabroom.util

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import xyz.gmitch215.tabroom.api.Entry
import xyz.gmitch215.tabroom.api.Event
import xyz.gmitch215.tabroom.api.Tournament
import xyz.gmitch215.tabroom.util.html.Document
import xyz.gmitch215.tabroom.util.html.querySelector
import xyz.gmitch215.tabroom.util.html.querySelectorAll

internal const val TOURNAMENT_NAME_SELECTOR = "div.main.index > h2.centeralign.marno"
internal const val TOURNAMENT_SUBTITLE_SELECTOR = "div.main.index > .full.centeralign.marno"
internal const val TOURNAMENT_DESC_SELECTOR = ".thenines.leftalign.plain.martop.whiteback.fullscreen.padvertmore.frontpage"

internal fun getTournament(doc: Document): Tournament {
    val name = doc.querySelector(TOURNAMENT_NAME_SELECTOR)?.textContent ?: "Unknown"
    val descHtml = doc.querySelector(TOURNAMENT_DESC_SELECTOR)?.textContent ?: ""
    val desc = descHtml.replace(Regex("<[^>]*>"), "")
    val subtitle = doc.querySelector(TOURNAMENT_SUBTITLE_SELECTOR)?.textContent ?: ""

    val year = subtitle.substringBefore(" - ").trim()
    val location = subtitle.substringAfter(" - ").trim()

    return Tournament(name, descHtml, desc, year, location)
}

internal const val EVENT_LINKS_SELECTOR = "div.menu > div.sidenote > a"

internal suspend fun getEvents(doc: Document): List<Event> = coroutineScope {
    val links = doc.querySelectorAll(EVENT_LINKS_SELECTOR)

    val events = mutableListOf<Event>()
    launch {
        for (link in links) {
            val name = link.textContent
            val href = link.attributes["href"] ?: continue

            launch {
                val eventDoc = "https://www.tabroom.com/$href".fetchDocument()
                val event = getEvent(name, eventDoc)
                events.add(event)
            }
        }
    }.join()

    return@coroutineScope events
}

internal const val ENTRY_ROW_SELECTOR = "tbody > tr"

private suspend fun getEvent(name: String, doc: Document): Event = coroutineScope {
    val entries = mutableListOf<Entry>()
    val rows = doc.querySelectorAll(ENTRY_ROW_SELECTOR)

    for (row in rows)
        launch {
            val school = row.children[0].textContent
            val location = row.children[1].textContent
            val entryName = row.children[2].textContent
            val code = row.children[3].textContent

            entries.add(Entry(school, location, entryName, code))
        }

    return@coroutineScope Event(name, entries)
}

