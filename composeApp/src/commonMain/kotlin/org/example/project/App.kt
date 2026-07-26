package org.example.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val LINES = List(20) { "Line $it" }

/**
 * A SelectionContainer wrapping a LazyColumn that does not fill its container.
 *
 * Pressing the mouse anywhere inside the SelectionContainer but *below the last item* - the bottom
 * padding, or just the empty space under a short list - crashes with
 *
 *     java.lang.IndexOutOfBoundsException: Index 20 out of bounds for length 20
 *         at androidx.compose.foundation.text.selection.MultiSelectionLayout.getCrossStatus(SelectionLayout.kt:184)
 *         at androidx.compose.foundation.text.selection.MultiSelectionLayout.startOrEndSlotToIndex(SelectionLayout.kt:295)
 *         at androidx.compose.foundation.text.selection.MultiSelectionLayout.getStartInfo(SelectionLayout.kt:188)
 *         at androidx.compose.foundation.text.selection.SelectionManager.startSelection(SelectionManager.kt:1406)
 *
 * Pressing in the *top* padding, above the first item, is fine.
 */
@Composable
fun App() {
    SelectionContainer {
        LazyColumn(
            // Any padding will do; this is exaggerated so the empty strip is easy to hit by hand.
            modifier = Modifier.fillMaxSize().padding(vertical = 64.dp),
        ) {
            items(LINES) { line ->
                BasicText(line)
            }
        }
    }
}

/** Same content in a plain Column, to show the crash does not depend on the layout being lazy. */
@Composable
fun NonLazyApp() {
    SelectionContainer {
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 64.dp)) {
            LINES.forEach { line ->
                BasicText(line)
            }
        }
    }
}
