package ru.tech.imageresizershrinker.core.ui.widget.preferences.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem

@Composable
fun ImagePreviewPreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.Image,
        title = stringResource(R.string.image_preview),
        subtitle = stringResource(R.string.image_preview_sub),
        color = color,
        modifier = modifier
    )
}