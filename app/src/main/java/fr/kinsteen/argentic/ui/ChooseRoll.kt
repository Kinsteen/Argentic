package fr.kinsteen.argentic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import fr.kinsteen.argentic.R
import fr.kinsteen.argentic.data.Roll
import fr.kinsteen.argentic.data.Rolls
import fr.kinsteen.argentic.rollsStore
import fr.kinsteen.argentic.utils.DataStoreUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChooseRoll(
    modifier: Modifier = Modifier,
    selectedRoll: Int,
    onAddRoll: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val rolls by context.rollsStore.data.collectAsState(
        Rolls.getDefaultInstance().toBuilder().addRoll(
        Roll.getDefaultInstance()))

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        FloatingActionButton(onClick = { expanded = true }) {
            Icon(painterResource(id = R.drawable.camera_roll), contentDescription = "Localized description")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for ((i, roll) in rolls.rollList.withIndex()) {
                DropdownMenuItem(
                    text = { Text(roll.name) },
                    trailingIcon = {
                        if (selectedRoll == i) {
                            Icon(Icons.Filled.Check, "Selected roll")
                        }
                    },
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            DataStoreUtils(context).saveSelectedRoll(i)
                        }
                    })
            }
            Divider()
            DropdownMenuItem(
                text = { Text("New roll") },
                onClick = {
                    onAddRoll()
                })
        }
    }
}

@Preview
@Composable
fun ChooseRollPreview() {
    ChooseRoll(modifier = Modifier.background(Color.White), selectedRoll = 0) {

    }
}