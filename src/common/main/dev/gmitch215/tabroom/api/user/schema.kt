@file:OptIn(ExperimentalJsExport::class)

package dev.gmitch215.tabroom.api.user

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a logged-in user on Tabroom.
 */
@JsExport
data class User(
    /**
     * The user's email.
     */
    val email: String,
    /**
     * The user's first name.
     */
    val firstName: String,
    /**
     * The user's middle name.
     */
    val middleName: String?,
    /**
     * The user's last name.
     */
    val lastName: String,
    /**
     * The user's username.
     */
    val phoneNumber: String,
    /**
     * The user's pronouns.
     */
    val pronouns: String,
    /**
     * The user's time zone.
     */
    val timeZone: String,
    /**
     * The user's home address.
     */
    val address: String?,
    /**
     * The user's city.
     */
    val city: String,
    /**
     * The user's state.
     */
    val state: String,
    /**
     * The user's country.
     */
    val country: String,
    /**
     * The user's zip code.
     */
    val zipCode: Int,
    /**
     * The user's NSDA information.
     */
    val nsda: NSDAUser?
)

/**
 * Represents a user connected to a NSDA account.
 */
@JsExport
data class NSDAUser(
    /**
     * The user's full name.
     */
    val fullName: String,
    /**
     * The user's NSDA ID.
     */
    val memberId: Int,
    /**
     * The amount of merit points this user has.
     */
    val meritPoints: Int,
    /**
     * The amount of merit points this user has to earn to get to the next degree.
     */
    val pointsToNextDegree: Int,
    /**
     * The date of the last points update.
     */
    val lastPointsDate: String
)