package io.thernal.console.inspector.ui.engine

import androidx.compose.runtime.tooling.CompositionData

/**
 * The set handed to `LocalInspectionTables`. Compose keeps the set itself as an element of a `HashSet` of its own
 * (`recordInspectionTable`), so its hash must not follow its content: the content hash of `mutableSetOf()` changes
 * with every sub-composition Compose adds, the same set then lands in that `HashSet` again and again, and
 * Kotlin/Native's rehash fails with "Have object hashCodes changed?". Equality and hash are this object's identity.
 */
internal class InspectionTables(
    private val tables: MutableSet<CompositionData> = mutableSetOf(),
) : MutableSet<CompositionData> by tables {

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
