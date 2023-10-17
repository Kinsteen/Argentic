package fr.kinsteen.argentic.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.kinsteen.argentic.R
import fr.kinsteen.argentic.data.Roll
import fr.kinsteen.argentic.data.Rolls
import fr.kinsteen.argentic.getCameraProvider
import fr.kinsteen.argentic.rollsStore
import fr.kinsteen.argentic.ui.theme.ArgenticTheme
import fr.kinsteen.argentic.utils.DataStoreUtils
import fr.kinsteen.argentic.utils.capture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("RestrictedApi")
@Composable
fun  CameraCapture(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraSelectorId by rememberSaveable { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val flipRotateAnimation by animateFloatAsState(if (cameraSelectorId == CameraSelector.LENS_FACING_BACK) 0f else 360f,
        label = "flip rotate anim"
    )
    var showNewRollDialog by remember { mutableStateOf(false) }
    var capturing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
    val imageCaptureUseCase by remember {
        mutableStateOf(
            ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
        )
    }

    val rolls by context.rollsStore.data.collectAsState(initial = Rolls.getDefaultInstance().toBuilder().addRoll(Roll.getDefaultInstance()).build())
    val dataStoreUtils = DataStoreUtils(context = context)
    val selectedRoll by dataStoreUtils.selectedRoll.collectAsState(initial = 0)

    // Fail safe is something is wrong with the roll selection
    runBlocking {
        if (selectedRoll > rolls.rollCount - 1) {
            dataStoreUtils.saveSelectedRoll(0)
        }
    }

    val canCapture = ((rolls.getRoll(selectedRoll).photoCount < rolls.getRoll(selectedRoll).max)
            && (rolls.getRoll(selectedRoll).developedTime == 0L)
            && !capturing)

    val previewAspectRatio: Float
    val configuration = LocalConfiguration.current
    previewAspectRatio = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            4/3f
        }
        else -> {
            3/4f
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        var overlayColor by remember {
            mutableStateOf(Color.White.copy(0f))
        }
        CameraPreview(
            modifier = Modifier.aspectRatio(previewAspectRatio),
            onUseCase = {
                previewUseCase = it
            },
            overlayColor = overlayColor
        )

        Box(
            Modifier
                .align(CenterHorizontally)
                .padding(48.dp, 48.dp)
                .fillMaxWidth()
        ) {
            IconButton(
                modifier = Modifier.align(CenterStart).size(48.dp),
                onClick = {
                    cameraSelectorId = if (cameraSelectorId == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Icon(
                    painterResource(id = R.drawable.flip_camera),
                    contentDescription = "Floating action button.",
                    modifier = Modifier.rotate(flipRotateAnimation).size(48.dp)
                )
            }
            ShutterButton(
                modifier = Modifier.align(Center),
                buttonSize = 64.dp,
                enabled = canCapture,
                onClick = {
                    overlayColor = Color.Black.copy(.8f)
                    capturing = true
                    capture(context, coroutineScope, imageCaptureUseCase, selectedRoll) {
                        overlayColor = Color.White.copy(0f)
                        capturing = false
                    }
                }
            )

            ChooseRoll(modifier = Modifier.align(CenterEnd), selectedRoll = selectedRoll) {
                showNewRollDialog = true
            }
        }

    }

    if (showNewRollDialog) {
        CreateRollDialog(
            onDismissRequest = {
                showNewRollDialog = false
            },
            onConfirm = { name, number ->
                showNewRollDialog = false
                CoroutineScope(Dispatchers.IO).launch {
                    context.rollsStore.updateData { currentRolls ->
                        currentRolls.toBuilder().addRoll(
                            Roll.newBuilder().setName(name).setMax(number.toInt())
                        ).build()
                    }
                }
            }
        )
    }

    LaunchedEffect(previewUseCase, cameraSelectorId) {
        val cameraProvider = context.getCameraProvider()
        val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraSelectorId).build();
        try {
            // Must unbind the use-cases before rebinding them.
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
            )
        } catch (ex: Exception) {
            Log.e("CameraCapture", "Failed to bind camera use cases", ex)
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
fun CameraCapturePreview() {
    ArgenticTheme(darkTheme = true) {
        CameraCapture()
    }
}
