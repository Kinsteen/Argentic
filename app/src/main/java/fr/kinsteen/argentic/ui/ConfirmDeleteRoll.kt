package fr.kinsteen.argentic.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ConfirmDeleteRoll(rollName: String, onDismissRequest: () -> Unit, onConfirmation: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        title = {
            Text("Delete roll?")
        },
        text = {
            Text("Every photo in \"$rollName\" will be permanently lost.")
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun DeleteRollPreview() {
    ConfirmDeleteRoll(
        rollName = "Roll 1",
        onDismissRequest = {

        },
        onConfirmation = {

        })
}