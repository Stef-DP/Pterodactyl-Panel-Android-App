package com.stefdp.pterodactylpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R

@Composable
fun MoreActionsButton(
    items: List<MoreActionsMenuItem>,
    enabled: Boolean = true,
    menuColor: Color? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = menuColor ?: MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp)
        ) {
            items.forEach { item ->
                val textColor = item.labelColor ?: MaterialTheme.colorScheme.onSurface
                val iconColor = item.iconColor ?: MaterialTheme.colorScheme.onSurface

                DropdownMenuItem(
                    text = {
                        CodeText(
                            text = item.label,
                            color = if (enabled && item.enabled)
                                textColor
                            else
                                textColor.copy(alpha = 0.5f)
                        )
                    },
                    onClick = {
                        item.onClick()
                        expanded = false
                    },
                    enabled = item.enabled,
                    leadingIcon = {
                        Icon(
                            painter = item.icon,
                            contentDescription = item.iconDescription,
                            tint = if (enabled && item.enabled)
                                iconColor
                            else
                                iconColor.copy(alpha = 0.5f)
                        )
                    }
                )
            }
        }

        val isEnabled = enabled && items.isNotEmpty()

        IconButton(
            icon = painterResource(R.drawable.more_horiz),
            iconContentDescription = "More options",
            iconColor = if (isEnabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            },
            onClick = { expanded = true },
            enabled = isEnabled
        )
    }
}

data class MoreActionsMenuItem(
    val label: String,
    val labelColor: Color? = null,
    val onClick: () -> Unit,
    val icon: Painter,
    val iconColor: Color? = null,
    val iconDescription: String,
    val enabled: Boolean = true
)