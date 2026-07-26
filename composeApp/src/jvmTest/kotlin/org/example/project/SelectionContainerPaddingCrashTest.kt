package org.example.project

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MouseInjectionScope
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.runtime.Composable
import kotlin.test.AfterTest
import kotlin.test.Test

/**
 * Each test presses the mouse inside the SelectionContainer.
 *
 * The two "below the last item" cases fail on Compose Multiplatform 1.12.0-beta02 and pass on
 * 1.11.1; everything else passes on both.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalFoundationApi::class)
class SelectionContainerPaddingCrashTest {
    @AfterTest
    fun restoreFlag() {
        ComposeFoundationFlags.isMouseSelectionBetweenTextEnabled = true
    }

    /** Passes: the press lands above the first selectable, which yields a valid slot. */
    @Test
    fun pressAboveFirstItem() = press({ App() }) { Offset(width / 2f, 4f) }

    /** FAILS on 1.12.0-beta02: IndexOutOfBoundsException from MultiSelectionLayout.crossStatus. */
    @Test
    fun pressBelowLastItem() = press({ App() }) { Offset(width / 2f, height - 4f) }

    /** FAILS on 1.12.0-beta02: same crash, so it is not specific to a lazy layout. */
    @Test
    fun pressBelowLastItemNonLazy() = press({ NonLazyApp() }) { Offset(width / 2f, height - 4f) }

    /** Passes: pressing on a selectable is the normal path and never builds the bad slot. */
    @Test
    fun pressOnItem() = press({ App() }) { Offset(width / 2f, 72f) }

    /** Passes: the crash is gated behind the new mouse-selection-between-text flag. */
    @Test
    fun pressBelowLastItemWithFlagDisabled() {
        ComposeFoundationFlags.isMouseSelectionBetweenTextEnabled = false
        press({ App() }) { Offset(width / 2f, height - 4f) }
    }

    private fun press(
        content: @Composable () -> Unit,
        position: MouseInjectionScope.() -> Offset,
    ) = runComposeUiTest {
        setContent { content() }
        onRoot().performMouseInput {
            moveTo(position())
            press()
            release()
        }
        waitForIdle()
    }
}
