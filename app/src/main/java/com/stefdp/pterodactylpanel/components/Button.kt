package com.stefdp.pterodactylpanel.components

import android.content.res.Configuration
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button as NativeButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.ui.theme.getButtonColors

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonType: ButtonType = ButtonType.PRIMARY,
    shape: Shape = RoundedCornerShape(BASE_CORNER_RADIUS.dp),
    hideDisabledColors: Boolean = false,
    colors: ButtonColors = getButtonColors(buttonType, hideDisabledColors),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable (RowScope.() -> Unit),
) {
    NativeButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

enum class ButtonType {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    ERROR,
    SUCCESS,
    WARNING
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL or Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ButtonPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    Button(
                        onClick = {},
                        enabled = true,
                    ) {
                        Text("Enabled Button")
                    }

                    Button(
                        onClick = {},
                        enabled = false,
                    ) {
                        Text("Disabled Button")
                    }

                    Button(
                        onClick = {},
                        buttonType = ButtonType.SECONDARY,
                        enabled = true,
                    ) {
                        Text("Enabled Button")
                    }

                    Button(
                        onClick = {},
                        buttonType = ButtonType.SECONDARY,
                        enabled = false,
                    ) {
                        Text("Disabled Button")
                    }

                    Button(
                        onClick = {},
                        buttonType = ButtonType.ERROR,
                        enabled = true,
                    ) {
                        Text("Enabled Button")
                    }

                    Button(
                        onClick = {},
                        buttonType = ButtonType.ERROR,
                        enabled = false,
                    ) {
                        Text("Disabled Button")
                    }
                }
            }
        }
    }
}