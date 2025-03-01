package xyz.gmitch215.tabroom.api

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
@OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)
fun getTournamentAsPromise(id: Int): Promise<Tournament> = GlobalScope.promise { getTournament(id) }