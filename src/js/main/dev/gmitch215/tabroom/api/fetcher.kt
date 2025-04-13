@file:OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)

package dev.gmitch215.tabroom.api

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

/**
 * Fetches a tournament from Tabroom.com using the provided ID.
 * @param id The ID of the tournament to fetch.
 * @return A [Promise] that resolves to a [Tournament] object.
 */
@JsName("getTournament")
@JsExport
fun getTournamentAsPromise(id: Int): Promise<Tournament> = GlobalScope.promise { getTournament(id) }

/**
 * Searches for tournaments on Tabroom.com using the provided query.
 * @param query The search query to use.
 * @return A [Promise] that resolves to a list of [Tournament] objects.
 */
@JsName("searchTournaments")
@JsExport
fun searchTournamentsAsPromise(query: String): Promise<List<Tournament>> = GlobalScope.promise { searchTounaments(query) }