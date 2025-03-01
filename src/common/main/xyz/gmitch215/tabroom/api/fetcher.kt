package xyz.gmitch215.tabroom.api

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import xyz.gmitch215.tabroom.util.TabroomUrls
import xyz.gmitch215.tabroom.util.fetchDocument
import xyz.gmitch215.tabroom.util.getAllJudges
import xyz.gmitch215.tabroom.util.getEvents
import xyz.gmitch215.tabroom.util.getTournament

/**
 * Gets a tournament by its ID.
 * @param id The ID of the tournament.
 * @return The tournament.
 */
suspend fun getTournament(id: Int): Tournament = coroutineScope {
    val urls = TabroomUrls(id)

    val home = urls.home.fetchDocument()
    val tourney = getTournament(home)

    coroutineScope {
        launch {
            val entries = async { urls.entries.fetchDocument() }
            val eventsDoc = async { urls.events.fetchDocument() }
            val events = getEvents(entries.await(), eventsDoc.await())

            (tourney.events as MutableList).addAll(events)
        }

        launch {
            val judgesDoc = urls.judges.fetchDocument()
            val judges = getAllJudges(judgesDoc)

            (tourney.judges as MutableMap).putAll(judges)
        }
    }

    return@coroutineScope tourney
}