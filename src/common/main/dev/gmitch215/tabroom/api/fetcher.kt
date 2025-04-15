package dev.gmitch215.tabroom.api

import dev.gmitch215.tabroom.util.HOSTNAME
import dev.gmitch215.tabroom.util.TOURNAMENT_SEARCH
import dev.gmitch215.tabroom.util.TournamentUrls
import dev.gmitch215.tabroom.util.USER_AGENT
import dev.gmitch215.tabroom.util.cache
import dev.gmitch215.tabroom.util.client
import dev.gmitch215.tabroom.util.engine
import dev.gmitch215.tabroom.util.fetchDocument
import dev.gmitch215.tabroom.util.getAllJudges
import dev.gmitch215.tabroom.util.getEvents
import dev.gmitch215.tabroom.util.getTournament
import dev.gmitch215.tabroom.util.html.Document
import dev.gmitch215.tabroom.util.html.querySelectorAll
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    val home = urls.home.fetchDocument(false)
    val tourney = getTournament(home)

    coroutineScope {
        launch {
            val entries = async { urls.entries.fetchDocument(false) }
            val eventsDoc = async { urls.events.fetchDocument(false) }
            val events = getEvents(entries.await(), eventsDoc.await())

            (tourney.events as MutableList).addAll(events)
        }

        launch {
            val judgesDoc = urls.judges.fetchDocument(false)
            val judges = getAllJudges(judgesDoc)

            (tourney.judges as MutableMap).putAll(judges)
        }
    }

    return@coroutineScope tourney
}

private const val TOURNAMENT_SEARCH_LINKS = "div.main > table > tbody > tr > td > a"

/**
 * Searches for tournaments by name.
 * @param query The query to search for.
 * @return A list of tournaments that match the query.
 */
@JvmName("searchTournamentsAsync")
@JsName("searchTournamentsAsync")
suspend fun searchTournaments(query: String): List<Tournament> = coroutineScope {
    val tournaments = mutableListOf<Tournament>()

    val cacheKey = "$TOURNAMENT_SEARCH+$query"

    val doc = cache[cacheKey] ?: withContext(engine.dispatcher) {
        val res = client.submitForm(
            TOURNAMENT_SEARCH,
            parameters {
                append("tourn_id", "")
                append("caller", "/index/index.mhtml")
                append(
                    "search", query
                        .replace(' ', '+')
                        .replace("\\", "\\\\")
                )
            }
        ) {
            headers {
                append("User-Agent", USER_AGENT)
                append("Content-Type", "application/x-www-form-urlencoded")
                append("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            }
        }

        if (!res.status.isSuccess()) throw IllegalStateException("Unexpected status code: ${res.status}")

        val doc = Document(TOURNAMENT_SEARCH, res.bodyAsText())
        cache[cacheKey] = doc

        return@withContext doc
    }

    val links = doc.querySelectorAll(TOURNAMENT_SEARCH_LINKS)
    if (links.isEmpty()) return@coroutineScope tournaments

    coroutineScope {
        for (link in links)
            launch {
                val url = "https://$HOSTNAME${link["href"]}"
                val tourneyDoc = url.fetchDocument(false)
                val tourney = getTournament(tourneyDoc)

                tournaments.add(tourney)
            }
    }

    return@coroutineScope tournaments
}