package com.stefdp.pterodactylpanel.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neoutils.highlight.compose.extension.toColor
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.ui.theme.AvatarColors
import com.stefdp.pterodactylpanel.ui.theme.getButtonColors
import io.github.feliperce.avatarkt.Avatar
import io.github.feliperce.avatarkt.AvatarVariant

@Composable
fun UserAvatar(
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    val user = LocalLoggedUser.current

    val username = user?.attributes?.username ?: "Unknown"

    Button(
        onClick = onClick,
        colors = getButtonColors().copy(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        enabled = enabled
    ) {
        Avatar(
            name = username,
            variant = AvatarVariant.BEAM,
            colors = AvatarColors
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = username,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (enabled)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
        )
    }
}