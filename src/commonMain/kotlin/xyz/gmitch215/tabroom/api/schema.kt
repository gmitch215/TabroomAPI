package xyz.gmitch215.tabroom.api

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
     * The year the tournament takes place in.
     */
    val year: String
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
    val code: String
)