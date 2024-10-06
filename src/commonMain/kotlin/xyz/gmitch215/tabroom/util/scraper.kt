package xyz.gmitch215.tabroom.util

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import xyz.gmitch215.tabroom.api.Entry
import xyz.gmitch215.tabroom.api.Event
import xyz.gmitch215.tabroom.api.Judge
import xyz.gmitch215.tabroom.api.Tournament
import xyz.gmitch215.tabroom.api.TournamentRecord
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

    val year = subtitle.substringBefore(" ").trim()
    val location = subtitle.substringAfterLast(" ").trim()

    return Tournament(name, descHtml, desc, year, location)
}

internal const val EVENT_LINKS_SELECTOR = "div.menu > div.sidenote > a.half.marvertno"
internal const val ENTRY_EVENT_SELECTOR = "div.menu > div.sidenote > a.full"

internal suspend fun getEvents(entries: Document, events: Document): List<Event> = coroutineScope {
    val eventLinks = events.querySelectorAll(EVENT_LINKS_SELECTOR)
    val entryLinks = entries.querySelectorAll(ENTRY_EVENT_SELECTOR)

    val eventList = mutableListOf<Event>()

    launch {
        for (link in eventLinks) {
            val name = link.textContent
            val eventHref = link.attributes["href"] ?: continue

            launch {
                val eventDoc = "https://www.tabroom.com/index/tourn/$eventHref".fetchDocument()

                val entryHref = entryLinks.firstOrNull { it.textContent == name }?.attributes["href"]
                val entryDoc = entryHref?.let { "https://www.tabroom.com$it".fetchDocument() }

                val event = getEvent(name, entryDoc, eventDoc)
                eventList.add(event)
            }
        }
    }.join()

    return@coroutineScope eventList
}

internal const val ROW_SELECTOR = "tbody > tr"
internal const val EVENT_INFO_SELECTOR = "div.menu > div.sidenote > a.nowrap.half.marvertno"
internal const val EVENT_INFO_KEY_SELECTOR = "div.main > div.row > span.third.semibold"
internal const val EVENT_INFO_VALUE_SELECTOR = "div.main > div.row > span.twothirds"

private suspend fun getEvent(name: String, entry: Document?, events: Document): Event = coroutineScope {
    val entries = mutableListOf<Entry>()

    if (entry != null) {
        val rows = entry.querySelectorAll(ROW_SELECTOR)

        for (row in rows) {
            val school = row.children[0].textContent
            val location = row.children[1].textContent
            val entryName = row.children[2].textContent
            val code = row.children[3].textContent

            entries.add(Entry(school, location, entryName, code))
        }
    }

    val event = events.querySelectorAll(EVENT_INFO_SELECTOR).first { it.textContent == name }
    val eventHref = event.attributes["href"] ?: return@coroutineScope Event(name, 0, emptyMap(), entries)
    val eventDoc = "https://www.tabroom.com/index/tourn/$eventHref".fetchDocument()

    // Event Attributes
    val id = eventHref.substringAfter('=').substringBefore('&').toInt()
    val keys = eventDoc.querySelectorAll(EVENT_INFO_KEY_SELECTOR)
    val values = eventDoc.querySelectorAll(EVENT_INFO_VALUE_SELECTOR)
    val fields = keys.zip(values).map { (key, value) -> key.textContent to value.textContent }.toMap()

    // Prelimination Records
    val prelimHref = eventHref.replace("events.mhtml", "ranked_list.mhtml")
    val prelims = "https://tabroom.com/index/tourn/results/$prelimHref".fetchDocument()
    val rows = prelims.querySelectorAll(ROW_SELECTOR)

    // Top Person is undefeated
    val roundCount = rows.first().children[0].textContent.toInt()

    if (entries.isEmpty())
        for (row in rows) {
            val wins = row.children[0].textContent.toInt()
            val losses = (roundCount - wins).coerceAtLeast(-1)

            val name = row.children[1].textContent
            val code = row.children[2].textContent
            val school = row.children[3].textContent

            val record = TournamentRecord(wins, losses)
            entries.add(Entry(school, "", name, code, record))
        }
    else
        for (entry in entries) {
            val row = rows.first { it.children[2].textContent == entry.code }

            val wins = row.children[0].textContent.toInt()
            val record = TournamentRecord(wins, roundCount - wins)
            entry.prelimination = record
        }

    return@coroutineScope Event(name, id, fields, entries)
}

internal const val JUDGES_LIST_SELECTOR = "div.menu > div.sidenote > div.nospace"
internal const val JUDGES_LINK_SELECTOR = "$JUDGES_LIST_SELECTOR > span.third.nospace > a.padvertless"

internal suspend fun getAllJudges(root: Document): Map<String, List<Judge>> = coroutineScope {
    val judges = mutableMapOf<String, List<Judge>>()
    val judgeLinks = root.querySelectorAll(JUDGES_LIST_SELECTOR)
    val judgeLists = root.querySelectorAll(JUDGES_LINK_SELECTOR)

    launch {
        for ((i, link) in judgeLinks.withIndex())
            launch {
                val type = link.children[0].textContent
                val listLink = judgeLists[i].attributes["href"] ?: return@launch
                val doc = "https://www.tabroom.com/$listLink".fetchDocument()
                
                judges.put(type, getJudges(doc))
            }
    }.join()

    return@coroutineScope judges
}

internal fun getJudges(doc: Document): List<Judge> {
    val judgeList = mutableListOf<Judge>()
    val rows = doc.querySelectorAll(ROW_SELECTOR)

    for (row in rows) {
        val hasParadigm = row.children[0].children.isNotEmpty()
        val firstName = row.children[1].textContent
        val lastName = row.children[2].textContent
        val school = row.children[3].textContent
        val location = row.children[4].textContent

        judgeList.add(Judge(firstName, lastName, school, location, hasParadigm))
    }

    return judgeList
}