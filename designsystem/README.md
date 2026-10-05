# designsystem

Design system for Console, used by `console-ui` and every addon UI module. Both modules are published, but they are
not part of the bundles: add `console-components` yourself to build an addon UI (a tab, a dock widget panel) with the
same `Ds*` primitives.

---

## Structure

```
designsystem/
  foundation/    # Tokens: colors, typography, shapes, dimensions, theme
  components/    # Composables: Ds* components, modifiers, theme provider
```

---

## Theme

Always wrap UI in `ThemeProvider` at the root — `ConsoleProvider` handles this automatically for console screens. For standalone previews:

```kotlin
@DsPreview
@Composable
private fun Preview() {
    ThemeProvider {
        // your composable
    }
}
```

Access tokens inside any composable:

```kotlin
Theme.colors.content01
Theme.colors.background2
Theme.colors.success
Theme.typography.body02
Theme.typography.label01
Theme.dimens.dp12
Theme.metrics.screenPaddingHorizontal
Theme.rounding.r12
Theme.opacity.S12
```

---

## Components

| Component | Description |
|-----------|-------------|
| `DsScaffold` | Full-screen layout with optional top/bottom bar |
| `DsAppBar` | Top bar with leading, trailing, and center slots |
| `DsCard` | Rounded bordered surface |
| `DsContainer` | Bordered box wrapper |
| `DsButton` | Tinted button; one `color` drives background, border and content |
| `DsChip` | Label chip with optional border (selected state) |
| `DsActionChip` | Icon-only on/off button; the name shows in a tooltip on long press and is the accessibility label |
| `DsChipGroup` | Caption plus chips wrapping onto as many rows as needed; the dock widget panels are built from it |
| `DsQuickAction` | Small square icon button for a folded dock widget's pill; `isPrimary` fills it |
| `DsSwipeActionHost` / `DsSwipeActionPane` | Swipe-to-reveal row actions; panes under one host close each other |
| `DsText` | Styled text |
| `DsIcon` | Icon with size/color control |
| `DsIconButton` | Pressable icon |
| `DsDivider` | 0.5dp horizontal divider |
| `DsSwitch` | Toggle switch |
| `DsTextField` | Input with hint, prefix, suffix slots |
| `DsCollapsible` | Scroll-driven collapsible header + content |
| `DsTabView` | Animated tab row with indicator |
| `DsNavigationBar` | Bottom navigation bar |

### Modifiers

```kotlin
Modifier.pressable(onPress = { })   // press feedback + click
Modifier.applyIf(condition) { }     // conditional modifier
```
