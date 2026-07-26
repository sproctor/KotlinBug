# SelectionContainer crashes on a mouse press below the last selectable (1.12.0-beta02)

Minimal reproduction for a regression introduced in Compose Multiplatform **1.12.0-beta02**.

Pressing the mouse inside a `SelectionContainer` but *below every selectable it contains* - the
list's bottom padding, or just the empty space under a short list - throws:

```
java.lang.IndexOutOfBoundsException: Index 20 out of bounds for length 20
    at androidx.compose.foundation.text.selection.MultiSelectionLayout.getCrossStatus(SelectionLayout.kt:184)
    at androidx.compose.foundation.text.selection.MultiSelectionLayout.startOrEndSlotToIndex(SelectionLayout.kt:295)
    at androidx.compose.foundation.text.selection.MultiSelectionLayout.getStartInfo(SelectionLayout.kt:188)
    at androidx.compose.foundation.text.selection.SelectionAdjustment$Companion.None$lambda$0(SelectionAdjustment.kt:46)
    at androidx.compose.foundation.text.selection.SelectionManager.updateSelection(SelectionManager.kt:1447)
    at androidx.compose.foundation.text.selection.SelectionManager.startSelection(SelectionManager.kt:1406)
    at androidx.compose.foundation.text.selection.SelectionRegistrarImpl.notifySelectionUpdateStart(SelectionRegistrarImpl.kt:233)
    at androidx.compose.foundation.text.modifiers.SelectionModifierNodeKt$DefaultMouseSelectionObserver$1.onStart(SelectionModifierNode.kt:254)
    at androidx.compose.foundation.text.selection.SelectionGesturesKt.mouseSelection(SelectionGestures.kt:294)
```

On desktop this reaches the AWT event thread as an uncaught exception and kills the app.

## Reproducing

Automated, no clicking required:

```shell
./gradlew :composeApp:jvmTest
```

`pressBelowLastItem` and `pressBelowLastItemNonLazy` fail; the other three pass. Setting
`composeMultiplatform` in `gradle/libs.versions.toml` to `1.11.1` makes all of them pass (the
`...WithFlagDisabled` test does not compile on 1.11.1 - the flag does not exist yet - so drop it
when checking that version).

By hand:

```shell
./gradlew :composeApp:run
```

The window shows 20 lines of text with 64dp of vertical padding. Clicking in the empty area below
the last line crashes; clicking above the first line or on a line does not.

## What is happening

1.12 added `ComposeFoundationFlags.isMouseSelectionBetweenTextEnabled` (default **on**), which lets
a mouse drag start a selection from the empty space between selectables. `SelectionManager
.getSelectionLayout` passes it to `SelectionLayoutBuilder` as `allowSelectionBetweenSelectables`,
and `appendSelectableInfoToBuilder` then skips its `isSelected(...)` guard, so **every** selectable
is appended to the layout instead of only the ones the gesture actually touches.

Slots are assigned as: selectable *i* gets the odd slot `2i + 1`, and a position that falls before
selectable *i* gets the even slot `2i`. If the press is after *every* selectable, no slot is ever
assigned, and `SelectionLayoutBuilder.build()` falls back to

```kotlin
val lastSlot = currentSlot + 1   // == 2 * infoList.size
```

for both `startSlot` and `endSlot`. `MultiSelectionLayout.crossStatus` then takes its
`startSlot == endSlot` branch:

```kotlin
// because one of the slots is not-dragging, it must be on a text directly
// because one of the slots is on a text directly and the start/end slots are equal,
// they both must be odd. Given this, dividing the slot by 2 should give us the correct info index.
else -> infoList[startSlot / 2].rawCrossStatus
```

The comment's invariant - that equal slots are odd, i.e. on a selectable - no longer holds once
"between" positions are allowed. Here both slots are `2 * size`, an even "between" slot past the
end, so `startSlot / 2 == size` indexes one past the end of `infoList`.

Before 1.12 this was unreachable: without the flag the `isSelected` guard rejected every
selectable, `infoList` came out empty, and `build()` returned `null`.

Pressing *above* the first selectable does not crash, because that position gets the even slot `0`
and `0 / 2` happens to land on a valid index.

## Notes

- Not specific to lazy layouts: `pressBelowLastItemNonLazy` uses a plain `Column` and crashes the
  same way. `LazyColumn` just makes it easy to hit, since a list shorter than its viewport always
  leaves empty space below the last item.
- Requires more than one selectable. With a single one, `build()` returns a `SingleSelectionLayout`,
  whose `crossStatus` never indexes `infoList`.
- Mouse only. The flag is `isMouseSelectionBetweenTextEnabled && !isInTouchMode`, so touch input
  takes the old path.
- Workaround: `ComposeFoundationFlags.isMouseSelectionBetweenTextEnabled = false` before the first
  composition (covered by the `pressBelowLastItemWithFlagDisabled` test).

Tested against `org.jetbrains.compose` 1.12.0-beta02 with Kotlin 2.4.10 on Linux.
