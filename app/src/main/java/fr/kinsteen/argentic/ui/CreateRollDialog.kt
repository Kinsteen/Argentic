package fr.kinsteen.argentic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import fr.kinsteen.argentic.ui.theme.ArgenticTheme

@Composable
fun CreateRollDialog(onDismissRequest: () -> Unit, onConfirm: (name: String, number: String) -> Unit) {
    var rollName by remember { mutableStateOf("") }
    //var rollNumber by remember { mutableStateOf("") }
    var rollNumberRaw by remember { mutableFloatStateOf(0f) }
    var showError by remember { mutableStateOf(false) }

    val minRollSize = 5
    val maxRollSize = 30
    val steps = maxRollSize - minRollSize
    val rollNumber by remember {
        derivedStateOf { (rollNumberRaw * steps).toInt() }
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier
                .padding(32.dp)) {
                ProvideTextStyle(MaterialTheme.typography.headlineLarge) {
                    Text(
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 24.dp),
                        text = "Create the new Roll")
                }
                OutlinedTextField(
                    modifier = Modifier.width(600.dp),
                    value = rollName,
                    isError = showError,
                    onValueChange = { rollName = it},
                    label = {
                        Text("Roll name")
                    })
                Slider(value = rollNumberRaw, onValueChange = {rollNumberRaw = it}, steps = 30)
                Text("Number of pictures: ${minRollSize + rollNumber}")
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 24.dp, 0.dp, 0.dp)) {
                    TextButton(modifier = Modifier,
                        onClick = {
                            if (rollName.isNotEmpty()) {
                                onConfirm(rollName, (minRollSize + rollNumber).toString())
                            } else {
                                showError = true
                            }
                        }) {
                        Text("Create")
                    }
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