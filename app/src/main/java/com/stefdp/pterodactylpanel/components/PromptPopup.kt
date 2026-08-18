package com.stefdp.pterodactylpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.ui.theme.getButtonColors
import com.stefdp.pterodactylpanel.utils.toAnnotatedString

@Composable
fun PromptPopup(
    showPopup: Boolean,
    isLoading: Boolean = false,
    onCancel: () -> Unit,
    onSuccess: () -> Unit,
    onDismissRequest: () -> Unit,
    title: CharSequence,
    description: CharSequence,
    cancelText: CharSequence = "Cancel",
    successText: CharSequence = "Delete",
    cancelButtonType: ButtonType = ButtonType.SECONDARY,
    successButtonType: ButtonType = ButtonType.PRIMARY,
    buttonLayout: DeletePromptButtonLayout = DeletePromptButtonLayout.HORIZONTAL,
    content: @Composable () -> Unit = { }
) {
    Popup(
        showPopup = showPopup,
        onDismissRequest = onDismissRequest
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CodeText(
                text = title.toAnnotatedString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        onClick = {
                            onDismissRequest()
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "Close prompt",
                )
            }
        }

        CodeText(
            text = description.toAnnotatedString()
        )

        content()

        if (buttonLayout == DeletePromptButtonLayout.HORIZONTAL) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp
                    ),
            ) {
                Button(
                    onClick = onCancel,
                    buttonType = cancelButtonType,
                    enabled = !isLoading
                ) {
                    CodeText(cancelText.toAnnotatedString())
                }

                Button(
                    onClick = {
                        onSuccess()
                    },
                    buttonType = successButtonType,
                    enabled = !isLoading
                ) {
                    CodeText(successText.toAnnotatedString())
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 8.dp
                    ),
            ) {
                Button(
                    onClick = onCancel,
                    buttonType = cancelButtonType,
                    enabled = !isLoading
                ) {
                    CodeText(cancelText.toAnnotatedString())
                }

                Button(
                    onClick = {
                        onSuccess()
                    },
                    buttonType = successButtonType,
                    enabled = !isLoading
                ) {
                    CodeText(successText.toAnnotatedString())
                }
            }
        }
    }
}

enum class DeletePromptButtonLayout {
    HORIZONTAL,
    VERTICAL
}