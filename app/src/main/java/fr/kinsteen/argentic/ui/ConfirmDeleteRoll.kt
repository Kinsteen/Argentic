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
                Text("Confirm", color = MaterialTheme.colorScheme.error)
            }
        },
        title = {
            Text("Delete roll?")
        },
        text = {
            Text("Do you confirm the deletion of \"$rollName\", and every photo in the roll?")
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
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