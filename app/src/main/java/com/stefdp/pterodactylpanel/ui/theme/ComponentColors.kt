package com.stefdp.pterodactylpanel.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.stefdp.pterodactylpanel.components.ButtonType

@Composable
fun getOutlinedTextFieldColors(displayDisabledColor: Boolean = true) = OutlinedTextFieldDefaults.colors(
    selectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    ),
    cursorColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedBorderColor = MaterialTheme.colorScheme.outline,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = if (displayDisabledColor)
        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    else
        MaterialTheme.colorScheme.surface,
    errorContainerColor = MaterialTheme.colorScheme.surface,
    disabledBorderColor = if (displayDisabledColor)
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    else
        MaterialTheme.colorScheme.outline,
)

@Composable
fun getButtonColors(
    buttonType: ButtonType = ButtonType.PRIMARY,
    hideDisabledColors: Boolean = false
): ButtonColors {
    val containerColor = when (buttonType) {
        ButtonType.PRIMARY -> MaterialTheme.colorScheme.primary
        ButtonType.SECONDARY -> MaterialTheme.colorScheme.secondary
        ButtonType.TERTIARY -> MaterialTheme.colorScheme.background
        ButtonType.ERROR -> MaterialTheme.colorScheme.error
        ButtonType.WARNING -> Yellow
        ButtonType.SUCCESS -> Green
    }

    val contentColor = when (buttonType) {
        ButtonType.PRIMARY -> MaterialTheme.colorScheme.onPrimary
        ButtonType.SECONDARY -> MaterialTheme.colorScheme.onSecondary
        ButtonType.TERTIARY -> MaterialTheme.colorScheme.onSurface
        ButtonType.ERROR -> MaterialTheme.colorScheme.onError
        ButtonType.WARNING -> MaterialTheme.colorScheme.onPrimary
        ButtonType.SUCCESS -> MaterialTheme.colorScheme.onPrimary
    }

    return ButtonDefaults.buttonColors().copy(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = if (hideDisabledColors) containerColor else containerColor.copy(alpha = 0.5f),
        disabledContentColor = if (hideDisabledColors) contentColor else contentColor.copy(alpha = 0.5f)
    )
}

@Composable
fun getSwitchColors() = SwitchDefaults.colors().copy(
    checkedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
    checkedTrackColor = MaterialTheme.colorScheme.primary,
    checkedBorderColor = Color.Transparent,
    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
    uncheckedTrackColor = MaterialTheme.colorScheme.background,
    uncheckedBorderColor = MaterialTheme.colorScheme.primary,
    disabledCheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    disabledCheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    disabledCheckedBorderColor = Color.Transparent,
    disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
    disabledUncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    disabledUncheckedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
)