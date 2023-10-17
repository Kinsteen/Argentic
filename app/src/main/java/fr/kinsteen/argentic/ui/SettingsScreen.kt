package fr.kinsteen.argentic.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import fr.kinsteen.argentic.dataStore
import fr.kinsteen.argentic.getDarkMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val context = LocalContext.current
    val darkMode by getDarkMode(context).collectAsState(initial = true)

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
                        Icon(Icons.Default.ArrowBack, "Go back")
                    }
                }
            )
        }
    ) {
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
                        context.dataStore.edit { pref ->
                            val d: Boolean = pref[booleanPreferencesKey("dark_mode")] ?: true
                            pref[booleanPreferencesKey("dark_mode")] = !d
                        }
                    }
                })
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(modifier = Modifier.align(CenterStart),
                    text = "Dark mode")
                Switch(
                    modifier = Modifier.align(CenterEnd),
                    checked = darkMode,
                    onCheckedChange = {
                        CoroutineScope(Dispatchers.IO).launch {
                            context.dataStore.edit { pref ->
                                val d: Boolean = pref[booleanPreferencesKey("dark_mode")] ?: true
                                pref[booleanPreferencesKey("dark_mode")] = !d
                            }
                        }
                    })
            }
        }
    }
}