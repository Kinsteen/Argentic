package fr.kinsteen.argentic.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fr.kinsteen.argentic.utils.DataStoreUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val context = LocalContext.current
    val darkMode by DataStoreUtils(context).darkMode.collectAsState(initial = true)

    var showTimePicker by remember { mutableStateOf(false) }
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Go back")
                    }
                }
            )
        }
    ) {
        if (true) {
            Dialog(onDismissRequest = { /*TODO*/ }) {
                Card {
                    Column(modifier = Modifier.padding(32.dp)) {
                        TimePicker(
                            state = timePickerState,

                        )
                        Button(onClick = {}) {
                            Text("Dismiss picker")
                        }
                        Button(onClick = {}) {
                            Text("Confirm selection")
                        }
                    }
                }
            }

        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(modifier = Modifier.align(CenterStart),
                    text = "Dark mode")
                Switch(
                    modifier = Modifier.align(CenterEnd),
                    checked = darkMode,
                    onCheckedChange = {
                    CoroutineScope(Dispatchers.IO).launch {
                        DataStoreUtils(context).saveDarkMode { current -> !current }
                    }
                })
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(modifier = Modifier.align(CenterStart),
                    text = "Roll developping time")
                Button(
                    modifier = Modifier.align(CenterEnd), onClick = {}) {
                    Text(text = "Pick a time")
                }
            }
        }
    }
}

@Composable
@Preview
fun SettingsPreview() {
    Settings {

    }
}