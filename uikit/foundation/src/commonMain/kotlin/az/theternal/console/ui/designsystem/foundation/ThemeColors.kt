package az.theternal.console.ui.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Semantic color tokens for the Console design system.
 *
 * Consume via `DsTheme.colors` — never construct directly in UI code.
 *
 * ---
 *
 * ## Naming convention
 *
 * Incremental suffixes (`1`, `2`, `3`…) always denote a **scale**, not an arbitrary
 * variant. The direction of the scale is defined per group:
 *
 * | Group        | 1 → N direction          |
 * |--------------|--------------------------|
 * | `content`    | highest contrast → lowest |
 * | `background` | deepest layer → most elevated surface |
 * | `primary`    | accent → deep container  |
 *
 * Named suffixes (`Content`, `Muted`, etc.) are **not** part of the scale — they
 * represent a semantically distinct role within the same family (see below).
 *
 * ---
 *
 * ## Groups
 *
 * ### Content — `content1` … `content4`
 * Text and icon colors, ordered by decreasing contrast against backgrounds.
 * - `content1` — primary body text, high-emphasis labels
 * - `content2` — secondary text, supporting labels
 * - `content3` — disabled text, placeholders, muted metadata
 * - `content4` — ghost text (e.g. input hints), barely visible decorations
 *
 * ### Primary — `primary`, `primary2`, `primary3`, `primaryContent`
 * - `primary`        — main accent; use for interactive elements, active indicators
 * - `primary2`       — slightly subdued accent; hover states, secondary interactive
 * - `primary3`       — deep container shade; tinted backgrounds behind primary content
 * - `primaryContent` — text/icon color **on top of a `primary`-filled surface** (always white)
 *                      This is NOT part of the numeric scale — it is a foreground pair.
 *
 * ### Backgrounds — `background1`, `background2`, `background3`
 * Surface layers ordered by increasing elevation (perceived closeness to the user).
 * - `background1` — base/page background; the deepest layer
 * - `background2` — slightly raised surfaces: cards, panels, drawers
 * - `background3` — elevated surfaces: chips, inputs, popovers on top of cards
 *
 * ### State colors — `success`, `info`, `warning`, `danger`, `fatal`
 * Semantic accent colors for status communication.
 *
 * Each state token is intended for use **as an accent on a dark or transparent surface**
 * (e.g. colored dot, badge border, icon tint, underline). For filled state surfaces
 * (e.g. a full alert banner) pair the accent with its `*Content` foreground:
 *
 * - `successContent` — text/icon on a `success`-filled surface
 * - `infoContent`    — text/icon on an `info`-filled surface
 * - `warningContent` — text/icon on a `warning`-filled surface
 * - `dangerContent`  — text/icon on a `danger`-filled surface
 *
 * `fatal` has **no** `fatalContent` token — filled fatal surfaces are not part of this
 * design system's current vocabulary. If you need text on a fatal-colored background,
 * use `BrandColors.violet.s900` directly and document the call site.
 *
 * ### Structural — `border`
 * Separator and outline color. One token covers all divider / border usages.
 *
 * ---
 *
 * ## Rules
 * 1. Never use a `*Content` token as a background color.
 * 2. Never use a state accent (`success`, `danger`, …) as foreground text directly
 *    on a filled surface of that same color — use its `*Content` pair instead.
 * 3. Do not add new tokens for one-off values; prefer [BrandColors] with a comment.
 * 4. When adding a new state that needs both accent and a filled-surface variant,
 *    add both the accent token and the `*Content` token together.
 */
@Immutable
data class ThemeColors(
    // Content (text/icon) — decreasing contrast
    val content1: Color,
    val content2: Color,
    val content3: Color,
    val content4: Color,

    // Primary accent
    val primary: Color,
    val primary2: Color,
    val primary3: Color,
    val primaryContent: Color,

    // Backgrounds — increasing elevation
    val background1: Color,
    val background2: Color,
    val background3: Color,

    // Helper states
    val success: Color,
    val successContent: Color,
    val info: Color,
    val infoContent: Color,
    val warning: Color,
    val warningContent: Color,
    val danger: Color,
    val dangerContent: Color,
    val fatal: Color,

    // Structural
    val border: Color,
)

val consoleThemeColors = ThemeColors(
    content1 = BrandColors.neutral.s100,
    content2 = BrandColors.neutral.s200,
    content3 = BrandColors.neutral.s300,
    content4 = BrandColors.neutral.s400,

    primary = BrandColors.blue.s400,
    primary2 = BrandColors.blue.s500,
    primary3 = BrandColors.blue.s900,
    primaryContent = Color.White,

    background1 = BrandColors.neutral.s1000,
    background2 = BrandColors.neutral.s900,
    background3 = BrandColors.neutral.s800,

    success = BrandColors.green.s400,
    successContent = BrandColors.green.s900,
    info = BrandColors.cyan.s400,
    infoContent = BrandColors.cyan.s900,
    warning = BrandColors.amber.s400,
    warningContent = BrandColors.amber.s900,
    danger = BrandColors.red.s400,
    dangerContent = BrandColors.red.s900,
    fatal = BrandColors.violet.s400,

    border = BrandColors.neutral.s700,
)
