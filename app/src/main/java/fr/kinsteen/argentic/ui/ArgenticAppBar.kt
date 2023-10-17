package fr.kinsteen.argentic.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import fr.kinsteen.argentic.R
import fr.kinsteen.argentic.ui.theme.ArgenticTheme

@Composable
fun ArgenticAppBar(modifier: Modifier = Modifier, onMenuClick: (String) -> Unit = {}) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf("Camera", "Darkroom")
    val itemsIcons = listOf(R.drawable.camera, R.drawable.camera_roll)

    NavigationBar(modifier = modifier) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(painterResource(itemsIcons[index]), contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    onMenuClick(item)
                })
        }
    }
}

@Preview
@Composable
fun ArgenticAppBarPreview() {
    ArgenticTheme(darkTheme = true) {
        ArgenticAppBar()
    }
}