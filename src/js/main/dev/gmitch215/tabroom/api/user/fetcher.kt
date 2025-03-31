@file:OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)

package dev.gmitch215.tabroom.api.user

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

/**
 * Fetches the current user from Tabroom.
 * @throws IllegalStateException if not logged in
 */
@JsName("getCurrentUser")
@JsExport
fun getCurrentUserAsPromise(): Promise<User> = GlobalScope.promise { getCurrentUser() }