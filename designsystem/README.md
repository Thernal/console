# designsystem

Internal design system for Console. **Not published as a standalone library** — consumed only by `console-compose` and addon UI modules.

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
| `DsChip` | Label chip with optional border (selected state) |
| `DsText` | Styled text |
| `DsIcon` | Icon with size/color control |
| `DsIconButton` | Pressable icon |
| `DsDivider` | 0.5dp horizontal divider |
| `DsSwitch` | Toggle switch |
| `DsTextField` | Input with hint, prefix, suffix slots |
| `DsCollapsible` | Scroll-driven collapsible header + content |
| `DsNavigationBar` | Bottom navigation bar |

### Modifiers

```kotlin
Modifier.pressable(onPress = { })   // press feedback + click
Modifier.applyIf(condition) { }     // conditional modifier
```
