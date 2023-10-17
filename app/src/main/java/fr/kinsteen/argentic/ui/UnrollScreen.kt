package fr.kinsteen.argentic.ui

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.kinsteen.argentic.AlarmScheduler
import fr.kinsteen.argentic.data.Roll
import fr.kinsteen.argentic.data.Rolls
import fr.kinsteen.argentic.rollsStore
import fr.kinsteen.argentic.ui.theme.ArgenticTheme
import fr.kinsteen.argentic.utils.DataStoreUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId

fun getEpochSeconds(): Long {
    return LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
}

@Composable
fun Unroll(modifier: Modifier, goToSettings: () -> Unit) {
    val context = LocalContext.current

    val rolls by context.rollsStore.data.collectAsState(Rolls.getDefaultInstance().toBuilder().addRoll(
        Roll.newBuilder().setName("Roll - 1").setMax(30)))
    val selectedRoll by DataStoreUtils(context).selectedRoll.collectAsState(initial = 0)

    val cw = ContextWrapper(context)
    val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
    val resolver: ContentResolver = context.contentResolver

    val alarmScheduler = AlarmScheduler(context)
    val textStyle = MaterialTheme.typography.headlineMedium

    var showDeleteRoll by remember { mutableStateOf(false) }
    var rollToDelete by remember { mutableIntStateOf(0) }

    Box {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Darkroom",
                    modifier = Modifier.padding(36.dp),
                    style = MaterialTheme.typography.headlineLarge
                )

                IconButton(modifier = Modifier.align(CenterEnd).padding(end = 16.dp), onClick = goToSettings) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings page"
                    )
                }
            }

            LazyColumn {
                items(rolls.rollCount) {
                    val roll = rolls.getRoll(it)

                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)){
                        ProvideTextStyle(textStyle) {
                            Text(
                                text = roll.name,
                                modifier = Modifier.padding(16.dp, 24.dp)
                            )
                        }
                        Text(
                            text = "Took " + roll.photoCount + " photos, " + (roll.max - roll.photoCount) + " photos left in roll",
                            modifier = Modifier.padding(16.dp)
                        )

                        if (roll.developedTime != 0L) {
                            if (getEpochSeconds() >= roll.developedTime) {
                                Column {
                                    Text("This roll is now developed!")
                                    Button(onClick = {
                                        roll.photoList.forEach { photo ->
                                            val contentValues = ContentValues()
                                            contentValues.put(
                                                MediaStore.Images.Media.DISPLAY_NAME,
                                                photo.name
                                            )
                                            contentValues.put(
                                                MediaStore.Images.Media.MIME_TYPE,
                                                "image/jpg"
                                            )
                                            contentValues.put(
                                                MediaStore.Images.Media.RELATIVE_PATH,
                                                Environment.DIRECTORY_DCIM + File.separator + "Argentic"
                                            )
                                            val imageUri =
                                                resolver.insert(
                                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    contentValues
                                                )

                                            val stream = imageUri?.let { resolver.openOutputStream(it) }

                                            val photoFile = File(directory, photo.name)
                                            stream?.write(photoFile.readBytes())
                                            photoFile.delete()
                                        }

                                        CoroutineScope(Dispatchers.IO).launch {
                                            context.rollsStore.updateData { currentRolls ->
                                                currentRolls.toBuilder().removeRoll(it).build()
                                            }
                                        }
                                    }) {
                                        Text(text = "Send photos to media")
                                    }
                                }

                            } else {
                                Button(onClick = { /*TODO*/ }, enabled = false) {
                                    Text("This roll is being developed...")
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp, 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, End),
                            ) {
                                if (rolls.rollCount > 1) {
                                    Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        onClick = {
                                            rollToDelete = it
                                            showDeleteRoll = true
                                        }) {
                                        Text("Delete roll")
                                    }
                                }

                                Button(onClick = {
                                    val developedTime = LocalDateTime.now().plusMinutes(1)

                                    CoroutineScope(Dispatchers.IO).launch {
                                        context.rollsStore.updateData { currentRolls ->
                                            val updatedRoll = roll.toBuilder()
                                                .setDevelopedTime(
                                                    developedTime.atZone(ZoneId.systemDefault())
                                                        .toEpochSecond()
                                                ).build()
                                            currentRolls.toBuilder().setRoll(it, updatedRoll).build()
                                        }
                                    }

                                    alarmScheduler.schedule(developedTime, roll.name)
                                }) {
                                    Text(text = "Send this to development!")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteRoll) {
            ConfirmDeleteRoll(
                rollName = rolls.getRoll(rollToDelete).name,
                onDismissRequest = {
                    showDeleteRoll = false
                },
                onConfirmation = {
                    showDeleteRoll = false
                    CoroutineScope(Dispatchers.IO).launch {
                        if (selectedRoll > rolls.rollCount - 2) {
                            DataStoreUtils(context).saveSelectedRoll(selectedRoll - 1)
                        }

                        context.rollsStore.updateData { currentRolls ->
                            currentRolls.toBuilder().removeRoll(rollToDelete).build()
                        }
                    }
                }
            )
        }
    }
}

@Composable
@Preview
fun UnrollPreview() {
    ArgenticTheme(darkTheme = true) {
        Unroll(modifier = Modifier.fillMaxSize()) {}
    }
}
