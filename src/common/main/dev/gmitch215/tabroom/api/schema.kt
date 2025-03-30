@file:OptIn(ExperimentalJsExport::class)

package dev.gmitch215.tabroom.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.ExperimentalJsExport
import kotlin.js.ExperimentalJsStatic
import kotlin.js.JsExport
import kotlin.js.JsStatic
import kotlin.jvm.JvmStatic

/**
 * Represents a tournament.
 */
@JsExport
data class Tournament(
    /**
     * The name of the tournament.
     */
    val name: String,
    /**
     * The description of the tournament in HTML format.
     */
    val descriptionHTML: String,
    /**
     * The raw description of the tournament.
     */
    val description: String,
    /**
     * The year the tournament takes place in.
     */
    val year: String,
    /**
     * The location of the tournament.
     */
    val location: String,
    /**
     * The events in the tournament.
     */
    val events: List<Event> = mutableListOf(),
    /**
     * The judges in the tournament.
     */
    val judges: Map<String, List<Judge>> = mutableMapOf(),
)

/**
 * Represents an event in a tournament.
 */
@JsExport
data class Event(
    /**
     * The name of the event.
     */
    val name: String,
    /**
     * The ID of the event.
     */
    val id: Int,
    /**
     * The fields in the event that represent its properties, such as fees, topic, etc.
     */
    val fields: Map<String, String> = emptyMap(),
    /**
     * The entries in the event.
     */
    val entries: List<Entry> = emptyList()
)

/**
 * Represents an entry inside a tournament event.
 */
@JsExport
data class Entry(
    /**
     * The name of the school the entry is from.
     */
    val school: String,
    /**
     * The location of the school.
     */
    val location: String,
    /**
     * The person's full name, who entered the tournament.
     */
    val name: String,
    /**
     * The entry code of the entry.
     */
    val code: String,
    /**
     * All the ballots for this entry.
     */
    val ballots: Map<Int, Ballot> = mutableMapOf()
)

/**
 * Represents a Judge in a tournament.
 */
@JsExport
data class Judge(
    /**
     * The first name of the judge.
     */
    val firstName: String,
    /**
     * The last name of the judge.
     */
    val lastName: String,
    /**
     * The name of the school this judge is affiliated with.
     */
    val school: String,
    /**
     * The location of this judge's origin.
     */
    val location: String,
    /**
     * Whether the judge has a paradigm, or things they want to see in the round.
     */
    val hasParadigm: Boolean,
)

// Records

/**
 * The level of a debate.
 */
@JsExport
@Serializable(with = DebateLevelSerializer::class)
enum class DebateLevel {
    /**
     * The level is novice, and only those who are in their first year of debate can participate.
     */
    NOVICE,

    /**
     * The level is Junior Varsity, and only those who are in their first or second year of debate can participate.
     */
    JV,

    /**
     * The level is Varsity, and anyone can participate.
     */
    VARSITY,

    /**
     * The level is open and anyone can participate.
     */
    OPEN

    ;

    /**
     * The aliases for the level as the appear in Tabroom.
     * For example, "novice" and "n" are aliases for [NOVICE].
     * @return The list of aliases for the level.
     */
    val aliases: List<String>
        get() = when (this) {
            NOVICE -> listOf("n")
            JV -> listOf("junior varsity", "middle")
            VARSITY -> listOf("v", "champ")
            OPEN -> listOf("o", "rr")
        } + name.lowercase()

    companion object {
        /**
         * Converts a string to a [DebateLevel].
         * @param string The string to convert.
         * @return The [DebateLevel] that corresponds to the string, or null if the string does not match any level.
         */
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        @JvmStatic
        fun fromString(string: String): DebateLevel? {
            val lower = string.lowercase()
            return entries.firstOrNull { it.aliases.contains(lower) }
        }
    }
}

/**
 * Represents the serializer for [DebateLevel].
 */
object DebateLevelSerializer : KSerializer<DebateLevel> {
    override val descriptor = PrimitiveSerialDescriptor("DebateLevel", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DebateLevel {
        val str = decoder.decodeString()
        return DebateLevel.fromString(str) ?: error("Unknown DebateLevel '$str'")
    }

    override fun serialize(encoder: Encoder, value: DebateLevel) {
        encoder.encodeString(value.name)
    }
}

/**
 * Represents the side of a debate.
 */
@JsExport
@Serializable(with = DebateSideSerializer::class)
enum class DebateSide {
    /**
     * The affirmative or pro side of the debate.
     */
    AFFIRMATIVE,
    /**
     * The negative or con side of the debate.
     */
    NEGATIVE,

    /**
     * The side is unknown.
     */
    UNKNOWN

    ;

    companion object {
        /**
         * Converts a string to a [DebateSide].
         * @param string The string to convert.
         * @return The [DebateSide] that corresponds to the string, or null if the string does not match any side.
         */
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        @JvmStatic
        fun fromString(string: String): DebateSide {
            return when (string.lowercase()) {
                "affirmative", "aff", "pro" -> AFFIRMATIVE
                "negative", "neg", "con" -> NEGATIVE
                else -> UNKNOWN
            }
        }
    }
}

/**
 * Represents the serializer for [DebateSide].
 */
object DebateSideSerializer : KSerializer<DebateSide> {
    override val descriptor = PrimitiveSerialDescriptor("DebateSide", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DebateSide {
        val str = decoder.decodeString()
        return DebateSide.fromString(str)
    }

    override fun serialize(encoder: Encoder, value: DebateSide) {
        encoder.encodeString(value.name)
    }
}

/**
 * Represents a ballot in a debate.
 */
@JsExport
@Serializable
data class Ballot(
    /**
     * The number of ballots lost.
     *
     * This is the number of ballots lost in the round. For example,
     * if there is only one judge, and the judge votes for the entry,
     * this will be `0`. If there are three judges, and two judges voted
     * for the entry, this will be `1`.
     */
    @SerialName("ballots_lost")
    val ballotsLost: Int = 0,
    /**
     * The number of ballots won.
     *
     * This is the number of ballots won in the round. For example,
     * if there is only one judge, and the judge votes for the entry,
     * this will be `1`. If there are three judges, and two judges voted
     * for the entry, this will be `2`.
     */
    @SerialName("ballots_won")
    val ballotsWon: Int = 0,
    /**
     * The number of ballots for the round.
     *
     * This is equivalent to the number of judges in the round.
     */
    @SerialName("total_ballots")
    val ballotsCount: Int = ballotsLost + ballotsWon,
    /**
     * The decision as a string.
     */
    @SerialName("decision_str")
    val decision: String = "L",
    /**
     * Whether this round is an elimination round.
     */
    @SerialName("elim")
    @Serializable(with = ByteToBooleanSerializer::class)
    val isElimination: Boolean,
    /**
     * The level of the debate.
     */
    @SerialName("event_level")
    val level: DebateLevel = DebateLevel.OPEN,
    /**
     * The name of the event.
     */
    @SerialName("event_name")
    val eventName: String = "Unknown",
    /**
     * The names of the judges, comma separated.
     */
    @SerialName("judge_raw")
    val judge: String = "Unknown",
    /**
     * The debated side.
     */
    val side: DebateSide = DebateSide.UNKNOWN,
    /**
     * The name of the opponent.
     *
     * This is the name of the opponent in the round according
     * to the tournament identifier, not their actual name.
     */
    val opponent: String? = "Unknown",
    /**
     * The round number.
     */
    @SerialName("round_name")
    val round: Int = 0,
    /**
     * The round label.
     *
     * This can be the same as the `round` field, or it can be a different number
     * depending on the tournament.
     */
    @SerialName("round_label")
    @Serializable(with = AnyToStringSerializer::class)
    val roundLabel: Any = round.toString(),
    /**
     * The speaker points of the first speaker, or 0 if not available.
     */
    @SerialName("speaker1_pts")
    @Serializable(with = SpeakerDeserializer::class)
    val speaker1Points: Double = 0.0,
    /**
     * The speaker points of the second speaker, or 0 if not available.
     */
    @SerialName("speaker2_pts")
    @Serializable(with = SpeakerDeserializer::class)
    val speaker2Points: Double = 0.0,
    /**
     * The name of the tournament.
     */
    @SerialName("tourn")
    val tournament: String,
    /**
     * The date of the tournament.
     */
    @SerialName("tourn_start")
    val tournamentDate: String,
    /**
     * Whether this ballot was submitted this year.
     */
    @SerialName("this_yr")
    @Serializable(with = ByteToBooleanSerializer::class)
    val isThisYear: Boolean = false,
) {

    /**
     * Whether the ballot is a win for the entry.
     */
    val isWin: Boolean
        get() = decision == "W" || decision == "1" || decision == "2"

}

private object SpeakerDeserializer : KSerializer<Double> {
    override val descriptor = PrimitiveSerialDescriptor("SpeakerPoints", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double {
        return try {
            decoder.decodeString().trim().toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    override fun serialize(encoder: Encoder, value: Double) {
        encoder.encodeDouble(value)
    }
}

private object ByteToBooleanSerializer : KSerializer<Boolean> {
    override val descriptor = PrimitiveSerialDescriptor("Boolean", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean {
        return decoder.decodeByte() == 1.toByte()
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeByte(if (value) 1 else 0)
    }
}

private object AnyToStringSerializer : KSerializer<Any> {
    override val descriptor = PrimitiveSerialDescriptor("Any", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        return decoder.decodeString()
    }

    override fun serialize(encoder: Encoder, value: Any) {
        encoder.encodeString(value.toString())
    }
}