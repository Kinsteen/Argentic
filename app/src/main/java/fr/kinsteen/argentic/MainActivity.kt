package fr.kinsteen.argentic

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import fr.kinsteen.argentic.data.Rolls
import fr.kinsteen.argentic.data.RollsSerializer
import fr.kinsteen.argentic.ui.ArgenticAppBar
import fr.kinsteen.argentic.ui.CameraCapture
import fr.kinsteen.argentic.ui.Settings
import fr.kinsteen.argentic.ui.Unroll
import fr.kinsteen.argentic.ui.theme.ArgenticTheme
import fr.kinsteen.argentic.utils.DataStoreUtils

val Context.rollsStore: DataStore<Rolls> by dataStore(fileName = "rolls.db", serializer = RollsSerializer)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channelId = "alarm_id"
        val channelName = "Developed rolls notification"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val darkMode by DataStoreUtils(this).darkMode.collectAsState(initial = true)
            ArgenticTheme(darkTheme = darkMode) {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier) {
                    MainContent(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var showNavigationBar by remember { mutableStateOf(true) }
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
        var showNotificationDialog by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = permissionState) {
            if (!permissionState.status.isGranted) {
                showNotificationDialog = true;
            }
        }

        if (showNotificationDialog) {
            AlertDialog(
                onDismissRequest = { showNotificationDialog = false},
                confirmButton = {
                    TextButton(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Confirm")
                    }
                },
                title = {
                    Text("Grant notifications permission?")
                },
                text = {
                    Text("To have the best experience possible, this app needs to be able to send " +
                            "you notifications, for example when a roll has been developed and " +
                            "your photos are available. If you do not grant this permission, the app will " +
                            "still function.")
                },
                dismissButton = {
                    TextButton(onClick = { showNotificationDialog = false }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                showNavigationBar,
                enter = slideInVertically { it / 6 },
                exit = slideOutVertically { it / 6 }
            ) {
                ArgenticAppBar(onMenuClick = {
                    navController.navigate(it) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                })
            }
        }
    ) {contentPadding ->
        NavHost(
            navController = navController,
            startDestination = "Camera",
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            composable("Camera",
                enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            ) {
                showNavigationBar = true;
                CameraCapture()
            }
            composable("Darkroom",
                enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },) {
                showNavigationBar = true;
                Unroll(
                    modifier = Modifier,
                    goToSettings = {
                        navController.navigate("Settings") {
                        }
                    }
                )
            }
            composable("Settings") {
                showNavigationBar = false;
                Settings(onBack = {
                    navController.popBackStack()
                })
            }
        }
    }
}
