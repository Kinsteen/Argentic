package fr.kinsteen.argentic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fr.kinsteen.argentic.ui.theme.ArgenticTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRollDialog(onDismissRequest: () -> Unit, onConfirm: (name: String, number: String) -> Unit) {
    var rollName by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    val textStyle = MaterialTheme.typography.headlineSmall
    val pad = PaddingValues(16.dp, 4.dp)
    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            ProvideTextStyle(textStyle) {
                Text(
                    modifier = Modifier.padding(pad).padding(0.dp, 8.dp, 0.dp, 0.dp),
                    text = "Define the new Roll")
            }
            OutlinedTextField(
                modifier = Modifier.padding(pad),
                value = rollName,
                isError = showError,
                onValueChange = { rollName = it},
                label = {
                    Text("Roll name")
                })

            OutlinedTextField(
                modifier = Modifier.padding(pad),
                value = rollNumber,
                isError = showError,
                onValueChange = { rollNumber = it},
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = {
                    Text("Number of photos in the roll")
                })
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(modifier = Modifier.padding(pad),
                    onClick = {
                        if (rollName.isNotEmpty() && rollNumber.toIntOrNull() != null) {
                            onConfirm(rollName, rollNumber)
                        } else {
                            showError = true
                        }
                    }) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Composable
@Preview
fun CreateRollDialogPreview() {
    ArgenticTheme {
        CreateRollDialog(
            onConfirm = {_, _ -> },
            onDismissRequest = {}
        )
    }
}