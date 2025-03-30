package dev.gmitch215.tabroom.util

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import dev.gmitch215.tabroom.api.Ballot
import dev.gmitch215.tabroom.api.Entry
import dev.gmitch215.tabroom.api.Event
import dev.gmitch215.tabroom.api.Judge
import dev.gmitch215.tabroom.api.Tournament
import dev.gmitch215.tabroom.util.html.Document
import dev.gmitch215.tabroom.util.html.querySelector
import dev.gmitch215.tabroom.util.html.querySelectorAll

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

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

    coroutineScope {
        for (link in eventLinks) {
            val name = link.textContent
            val eventHref = link.attributes["href"] ?: continue

            launch {
                val eventDoc = async { "https://www.tabroom.com/index/tourn/$eventHref".fetchDocument() }

                val entryHref = entryLinks.firstOrNull { it.textContent == name }?.attributes["href"]
                val entryDoc = async { entryHref?.let { "https://www.tabroom.com$it".fetchDocument() } }

                val event = getEvent(name, entryDoc.await(), eventDoc.await())
                eventList.add(event)
            }
        }
    }

    return@coroutineScope eventList
}

internal const val ROW_SELECTOR = "tbody > tr"
internal const val EVENT_INFO_SELECTOR = "div.menu > div.sidenote > a.nowrap.half.marvertno"
internal const val EVENT_INFO_KEY_SELECTOR = "div.main > div.row > span.third.semibold"
internal const val EVENT_INFO_VALUE_SELECTOR = "div.main > div.row > span.twothirds"

private suspend fun getEvent(name: String, entry: Document?, events: Document): Event = coroutineScope {
    val entries = mutableListOf<Entry>()

    if (entry != null) {
        coroutineScope {
            val rows = entry.querySelectorAll(ROW_SELECTOR)

            for (row in rows) {
                launch {
                    val school = row.children[0].textContent
                    val location = row.children[1].textContent
                    val entryName = row.children[2].textContent
                    val code = row.children[3].textContent
                    val entry = Entry(school, location, entryName, code)

                    if (row.children.size < 5) {
                        entries.add(entry)
                        return@launch
                    }

                    val recordLink = row.children[4].children[0].attributes["href"]
                    if (recordLink != null)
                        launch {
                            val recordDoc = "https://www.tabroom.com$recordLink".fetchDocument()
                            val isDouble = recordLink.substringAfter("&id2=").isNotEmpty()

                            (entry.ballots as MutableMap).putAll(getRecord(recordDoc, isDouble))
                        }

                    entries.add(entry)
                }
            }
        }
    }

    val event = events.querySelectorAll(EVENT_INFO_SELECTOR).first { it.textContent == name }
    val eventHref = event.attributes["href"] ?: return@coroutineScope Event(name, 0, emptyMap(), entries)

    val fieldsId = async {
        val eventDoc = "https://www.tabroom.com/index/tourn/$eventHref".fetchDocument()

        // Event Attributes
        val id = eventHref.substringAfter('=').substringBefore('&').toInt()
        val keys = eventDoc.querySelectorAll(EVENT_INFO_KEY_SELECTOR)
        val values = eventDoc.querySelectorAll(EVENT_INFO_VALUE_SELECTOR)

        id to keys.zip(values).associate { (key, value) -> key.textContent to value.textContent }
    }

    val (id, fields) = fieldsId.await()
    return@coroutineScope Event(name, id, fields, entries)
}

internal const val JUDGES_LIST_SELECTOR = "div.menu > div.sidenote > div.nospace"
internal const val JUDGES_LINK_SELECTOR = "$JUDGES_LIST_SELECTOR > span.third.nospace > a.padvertless"

internal suspend fun getAllJudges(root: Document): Map<String, List<Judge>> = coroutineScope {
    val judges = mutableMapOf<String, List<Judge>>()
    val judgeLinks = root.querySelectorAll(JUDGES_LIST_SELECTOR)
    val judgeLists = root.querySelectorAll(JUDGES_LINK_SELECTOR)

    coroutineScope {
        for ((i, link) in judgeLinks.withIndex())
            launch {
                val type = link.children[0].textContent
                val listLink = judgeLists[i].attributes["href"] ?: return@launch
                if (listLink.contains("paradigms")) return@launch

                val doc = "https://www.tabroom.com$listLink".fetchDocument()
                judges.put(type, getJudges(doc))
            }
    }

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

internal const val RECORD_TITLE = "div.blankfull > div > span.nospace > h3"

internal suspend fun getRecord(doc: Document, isDouble: Boolean): Map<Int, Ballot> = coroutineScope {
    val map = mutableMapOf<Int, Ballot>()
    val data = json.decodeFromString<JsonObject>(doc.html.substringAfter("var panels = ").substringBefore(";").trim())

    coroutineScope {
        for ((id, ballotJson) in data.entries) {
            if (ballotJson !is JsonObject) continue
            if (ballotJson.isEmpty()) continue

            launch {
                val ballot = json.decodeFromJsonElement<Ballot>(ballotJson)
                map.put(id.toInt(), ballot)
            }
        }
    }

    return@coroutineScope map
}