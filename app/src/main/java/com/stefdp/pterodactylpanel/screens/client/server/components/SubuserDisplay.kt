package com.stefdp.pterodactylpanel.screens.client.server.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gravatar.types.Email
import com.gravatar.ui.components.atomic.Avatar
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.LocalLoggedUser
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.IconButton
import com.stefdp.pterodactylpanel.network.client.models.ServerSubuser

@Composable
fun SubuserDisplay(
    subuser: ServerSubuser,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    hasUpdatePermission: Boolean,
    hasDeletePermission: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.outline)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Avatar(
                    email = Email(subuser.attributes.email),
                    size = 50.dp,
                    modifier = Modifier
                        .clip(CircleShape)
                )

                Text(
                    text = subuser.attributes.email
                )
            }

            val localLoggedUser = LocalLoggedUser.current

            val isSelf by rememberSaveable(
                localLoggedUser,
                subuser
            ) {
                val userEmail = localLoggedUser?.attributes?.email

                if (userEmail == null) {
                    mutableStateOf(true)
                } else {
                    mutableStateOf(userEmail == subuser.attributes.email)
                }
            }

            if (!isSelf) {
                Row(
                    modifier = Modifier.weight(0.5f),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (hasUpdatePermission) {
                        IconButton(
                            icon = painterResource(R.drawable.edit),
                            iconContentDescription = "Edit User",
                            onClick = onEdit,
                            border = true
                        )
                    }

                    if (hasDeletePermission) {
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        IconButton(
                            icon = painterResource(R.drawable.delete),
                            iconContentDescription = "Delete Database",
                            iconColor = MaterialTheme.colorScheme.error,
                            borderColor = MaterialTheme.colorScheme.error,
                            onClick = onDelete,
                            border = true
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (subuser.attributes.twoFactorAuthenticationEnabled) {
                    Icon(
                        painter = painterResource(R.drawable.person_shield),
                        contentDescription = "2FA Enabled"
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.lock_open),
                        contentDescription = "2FA Disabled",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Text(
                    text = "2FA " + if (subuser.attributes.twoFactorAuthenticationEnabled) "ENABLED" else "DISABLED",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = (subuser.attributes.permissions.size - 1).toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "PERMISSIONS",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}