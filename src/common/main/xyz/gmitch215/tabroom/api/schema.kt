package xyz.gmitch215.tabroom.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents a tournament.
 */
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