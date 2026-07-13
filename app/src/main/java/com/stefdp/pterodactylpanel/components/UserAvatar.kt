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
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.ui.theme.getButtonColors

@Composable
fun UserAvatar(
    enabled: Boolean = true,
    onClick: () -> Unit = {}
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
//        Avatar(
//            avatar = avatar,
//            enabled = enabled,
//            isAdmin = user != null && user.role.level <= UserRole.ADMIN.level,
//        )
        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = "User Avatar",
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