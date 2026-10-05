package io.thernal.console.inspector.ui.engine.tree

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SourceNamingTest {
    @Test
    fun `generic names and getters are not treated as composables`() {
        assertTrue(isGenericName("remember"))
        assertTrue(isGenericName("Layout"))
        assertTrue(isGenericName("<get-current>"))
        assertTrue(isGenericName(""))
        assertFalse(isGenericName("HomeScreen"))
    }

    @Test
    fun `lower-case value helpers count as framework without tags`() {
        assertTrue(isFrameworkNode(name = "collectAsState", file = "SnapshotFlow.kt", appTags = emptySet()))
    }

    @Test
    fun `well-known library files count as framework without tags`() {
        assertTrue(isFrameworkNode(name = "Text", file = "Text.kt", appTags = emptySet()))
        assertTrue(isFrameworkNode(name = "Anything", file = "LazyList.kt", appTags = emptySet()))
    }

    @Test
    fun `unknown upper-case composables count as app code without tags`() {
        assertFalse(isFrameworkNode(name = "HomeScreen", file = "HomeScreen.kt", appTags = emptySet()))
    }

    @Test
    fun `app files sharing a prefix with a library file stay app code`() {
        assertFalse(isFrameworkNode(name = "BasicScreen", file = "BasicScreen.kt", appTags = emptySet()))
        assertFalse(isFrameworkNode(name = "WindowSettings", file = "WindowSettings.kt", appTags = emptySet()))
        assertFalse(isFrameworkNode(name = "ScrollToTop", file = "ScrollToTop.kt", appTags = emptySet()))
        assertTrue(isFrameworkNode(name = "BasicText", file = "BasicText.kt", appTags = emptySet()))
    }

    @Test
    fun `an empty name counts as framework instead of failing`() {
        assertTrue(isFrameworkNode(name = "", file = "HomeScreen.kt", appTags = emptySet()))
    }

    @Test
    fun `with tags only matching composables count as app code`() {
        val tags = setOf("screen")

        assertFalse(isFrameworkNode(name = "HomeScreen", file = "Home.kt", appTags = tags))
        assertFalse(isFrameworkNode(name = "Card", file = "ProfileScreen.kt", appTags = tags))
        assertTrue(isFrameworkNode(name = "Text", file = "Text.kt", appTags = tags))
    }

    @Test
    fun `the inspector's own overlay files are recognised`() {
        assertTrue(isInspectorOwnFile("InspectorDrawLayer.kt"))
        assertTrue(isInspectorOwnFile("InspectorBackdrop.kt"))
        assertTrue(isInspectorOwnFile("InspectorCapture.kt"))
        assertFalse(isInspectorOwnFile("HomeScreen.kt"))
        assertFalse(isInspectorOwnFile(null))
    }
}
