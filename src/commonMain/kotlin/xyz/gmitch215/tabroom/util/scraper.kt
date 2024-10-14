package xyz.gmitch215.tabroom.util

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import xyz.gmitch215.tabroom.api.CurrentSeasonResult
import xyz.gmitch215.tabroom.api.DebateLevel
import xyz.gmitch215.tabroom.api.DoubleEntryRecord
import xyz.gmitch215.tabroom.api.Entry
import xyz.gmitch215.tabroom.api.EntryRecord
import xyz.gmitch215.tabroom.api.Event
import xyz.gmitch215.tabroom.api.Judge
import xyz.gmitch215.tabroom.api.SingleEntryRecord
import xyz.gmitch215.tabroom.api.Tournament
import xyz.gmitch215.tabroom.api.TournamentRecord
import xyz.gmitch215.tabroom.api.TournamentResult
import xyz.gmitch215.tabroom.api.parseMultiJudge
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
        launch {
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

                            entry.record = getRecord(recordDoc, isDouble)
                        }

                    entries.add(entry)
                }
            }
        }.join()
    }

    val event = events.querySelectorAll(EVENT_INFO_SELECTOR).first { it.textContent == name }
    val eventHref = event.attributes["href"] ?: return@coroutineScope Event(name, 0, emptyMap(), entries)

    val fieldsId = async {
        val eventDoc = "https://www.tabroom.com/index/tourn/$eventHref".fetchDocument()

        // Event Attributes
        val id = eventHref.substringAfter('=').substringBefore('&').toInt()
        val keys = eventDoc.querySelectorAll(EVENT_INFO_KEY_SELECTOR)
        val values = eventDoc.querySelectorAll(EVENT_INFO_VALUE_SELECTOR)

        id to keys.zip(values).map { (key, value) -> key.textContent to value.textContent }.toMap()
    }

    // Prelimination Records
    launch {
        val prelimHref = eventHref.replace("events.mhtml", "ranked_list.mhtml")
        val prelims = "https://tabroom.com/index/tourn/results/$prelimHref".fetchDocument()
        val rows = prelims.querySelectorAll(ROW_SELECTOR)

        // Top Person is undefeated
        val roundCount = rows.first().children[0].textContent.toInt()

        if (entries.isEmpty())
            for (row in rows) {
                val wins = row.children[0].textContent.toDouble()
                val losses = (roundCount - wins).coerceAtLeast(-1.0)

                val name = row.children[1].textContent
                val code = row.children[2].textContent
                val school = row.children[3].textContent

                val record = TournamentRecord(wins, losses)
                entries.add(Entry(school, "", name, code, record))
            }
        else
            for (entry in entries) {
                val row = rows.first { it.children[2].textContent == entry.code }

                val wins = row.children[0].textContent.toDouble()
                val record = TournamentRecord(wins, roundCount - wins)
                entry.prelimination = record
            }
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

internal const val RECORD_TITLE = "div.blankfull > div > span.nospace > h3"
internal const val CURRENT_SEASON_TABLE = "div.blankfull > #team_season > table > tbody"

internal const val SPEAKER_1_CURRENT_GRID = "div.blankfull > #seasonal_grid_speaker1_this_yr > table > tbody"
internal const val SPEAKER_1_PAST_GRID = "div.blankfull > #seasonal_grid_speaker1_past > table > tbody"
internal const val SPEAKER_2_CURRENT_GRID = "div.blankfull > #seasonal_grid_speaker2_this_yr > table > tbody"
internal const val SPEAKER_2_PAST_GRID = "div.blankfull > #seasonal_grid_speaker2_past > table > tbody"
internal const val TOGETHER_CURRENT_GRID = "div.blankfull > #seasonal_grid_together_past > table > tbody"
internal const val TOGETHER_PAST_GRID = "div.blankfull > #seasonal_grid_together_past > table > tbody"

internal suspend fun getRecord(doc: Document, isDouble: Boolean): EntryRecord = coroutineScope {
    // FIXME: Records are loaded by JavaScript
    val title = async { doc.querySelector(RECORD_TITLE)?.textContent ?: "Unknown" }

    val currentSeason = mutableListOf<CurrentSeasonResult>()
    val currentSeasonTable = doc.querySelector(CURRENT_SEASON_TABLE) ?:
        if (isDouble)
            return@coroutineScope DoubleEntryRecord(title.await(), currentSeason, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        else {
            return@coroutineScope SingleEntryRecord(title.await(), currentSeason, emptyList(), emptyList())
        }

    launch {
        for (row in currentSeasonTable.children) {
            launch {
                println(row.children[0].textContent)
                val level = DebateLevel.fromString(row.children[0].textContent)
                if (level == null) return@launch

                val prelimsRaw = row.children[1].textContent
                val elimsRaw = row.children[3].textContent

                val prelimWins = prelimsRaw.substringAfter('(').substringBefore('/').toInt()
                val prelimCount = prelimsRaw.substringAfter('/').substringBefore(')').toInt()
                val elimWins = elimsRaw.substringAfter('(').substringBefore('/').toInt()
                val elimCount = elimsRaw.substringAfter('/').substringBefore(')').toInt()

                currentSeason.add(CurrentSeasonResult(level, prelimWins, prelimCount, elimWins, elimCount))
            }
        }
    }.join()

    if (isDouble) {
        val currentResults = async { parseTournamentGrid(doc, TOGETHER_CURRENT_GRID) }
        val firstOthersCurrent = async { parseTournamentGrid(doc, SPEAKER_1_CURRENT_GRID) }
        val secondOthersCurrent = async { parseTournamentGrid(doc, SPEAKER_2_CURRENT_GRID) }
        val previousResults = async { parseTournamentGrid(doc, TOGETHER_PAST_GRID) }
        val firstOthersPrevious = async { parseTournamentGrid(doc, SPEAKER_1_PAST_GRID) }
        val secondOthersPrevious = async { parseTournamentGrid(doc, SPEAKER_2_PAST_GRID) }

        return@coroutineScope DoubleEntryRecord(
            title.await(),
            currentSeason,
            currentResults.await(),
            firstOthersCurrent.await(),
            secondOthersCurrent.await(),
            previousResults.await(),
            firstOthersPrevious.await(),
            secondOthersPrevious.await()
        )
    } else {
        val currentResults = async { parseTournamentGrid(doc, SPEAKER_1_CURRENT_GRID) }
        val previousResults = async { parseTournamentGrid(doc, SPEAKER_1_PAST_GRID) }
        return@coroutineScope SingleEntryRecord(
            title.await(),
            currentSeason,
            currentResults.await(),
            previousResults.await()
        )
    }
}

internal fun parseTournamentGrid(doc: Document, selector: String): List<TournamentResult> {
    val table = doc.querySelector(selector) ?: return emptyList()

    val results = mutableListOf<TournamentResult>()

    for (row in table.children) {
        val name = row.children[0].textContent
        val date = row.children[2].textContent

        val division0 = row.children[1].textContent
        val division = DebateLevel.fromString(division0) ?: throw IllegalArgumentException("Unrecognized Division '$division0'")

        val prelimination = TournamentRecord.fromString(row.children[3].textContent)
        val trials = parseMultiJudge(row.children[4].textContent)
        val doubles = parseMultiJudge(row.children[5].textContent)
        val octos = parseMultiJudge(row.children[6].textContent)
        val quarters = parseMultiJudge(row.children[7].textContent)
        val semis = parseMultiJudge(row.children[8].textContent)
        val finals = parseMultiJudge(row.children[9].textContent)

        results.add(TournamentResult(name, division, date, prelimination, trials, doubles, octos, quarters, semis, finals))
    }

    return results
}