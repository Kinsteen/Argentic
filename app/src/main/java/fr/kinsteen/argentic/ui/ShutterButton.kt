package fr.kinsteen.argentic.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.kinsteen.argentic.ui.theme.ArgenticTheme

@Composable
fun ShutterButton(
    buttonSize: Dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val color = if (enabled) MaterialTheme.colorScheme.onBackground else Color.Gray
    Box(modifier.drawWithContent {
        drawCircle(color, radius = (buttonSize / 2 - 8.dp).toPx())
        drawCircle(color, radius = (buttonSize / 2).toPx(), style = Stroke(width = 8f))
    }.size(buttonSize).clickable(enabled = enabled, onClick = onClick, role = Role.Button, onClickLabel = "Shutter Button")) {

    }
}

@Composable
@Preview
fun ShutterButtonPreview() {
    ArgenticTheme(darkTheme = true) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(500.dp)) {
            ShutterButton(64.dp, enabled = true)
        }
    }
}