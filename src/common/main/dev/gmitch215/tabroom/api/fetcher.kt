package dev.gmitch215.tabroom.api

import dev.gmitch215.tabroom.util.TournamentUrls
import dev.gmitch215.tabroom.util.fetchDocument
import dev.gmitch215.tabroom.util.getAllJudges
import dev.gmitch215.tabroom.util.getEvents
import dev.gmitch215.tabroom.util.getTournament
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.js.JsName
import kotlin.jvm.JvmName

/**
 * Gets a tournament by its ID.
 * @param id The ID of the tournament.
 * @return The tournament.
 */
@JvmName("getTournamentAsync")
@JsName("getTournamentAsync")
suspend fun getTournament(id: Int): Tournament = coroutineScope {
    val urls = TournamentUrls(id)

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