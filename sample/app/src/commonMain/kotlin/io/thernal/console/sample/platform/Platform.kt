package io.thernal.console.sample.platform

/** Human-readable OS/runtime string for the demo's Details panel (e.g. "Android 14 (API 34)"). */
expect fun platformLabel(): String

/** Device/host string for the demo's Details panel (e.g. "Google Pixel 8"). */
expect fun deviceModel(): String
