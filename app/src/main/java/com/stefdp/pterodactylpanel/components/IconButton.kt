package com.stefdp.pterodactylpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    iconContentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    iconColor: Color? = null,
    border: Boolean = false,
    borderColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val mainBoxModifier = modifier
        .size(35.dp)
        .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
        .background(
            color = Color.Transparent,
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        )
        .clickable(
            enabled = enabled,
            onClick = onClick
        )

    val borderModifier = if (border) {
        mainBoxModifier.border(
            width = 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        )
    } else mainBoxModifier

    Box(
        modifier = borderModifier
            .size(35.dp)
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        val color = iconColor ?: MaterialTheme.colorScheme.onSurface

        Icon(
            painter = icon,
            contentDescription = iconContentDescription,
            tint = if (enabled)
                color
            else
                color.copy(alpha = 0.5f),
            modifier = Modifier.size(25.dp)
        )
    }
}