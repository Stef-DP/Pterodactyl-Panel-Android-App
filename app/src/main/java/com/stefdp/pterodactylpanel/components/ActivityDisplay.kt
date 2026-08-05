package com.stefdp.pterodactylpanel.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.network.client.models.ActivityLog
import com.stefdp.pterodactylpanel.ui.theme.AvatarColors
import com.stefdp.pterodactylpanel.ui.theme.PterodactylPanelTheme
import com.stefdp.pterodactylpanel.utils.VariableRegex
import com.stefdp.pterodactylpanel.utils.parseActivityVariables
import io.github.feliperce.avatarkt.Avatar
import io.github.feliperce.avatarkt.AvatarVariant
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.Instant

@Composable
fun ActivityDisplay(
    activity: ActivityLog,
    onOpenMetadata: () -> Unit,
) {
    val actorName = if (activity.attributes.relationships == null) {
        "Unknown"
    } else if (activity.attributes.relationships.actor.attributes == null) {
        "System"
    } else {
        activity.attributes.relationships.actor.attributes.username
    }

    val userAgent = activity.attributes.properties["useragent"]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BASE_CORNER_RADIUS.dp))
            .background(MaterialTheme.colorScheme.outline)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        var showUserAgent by rememberSaveable {
            mutableStateOf(false)
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    name = actorName,
                    variant = AvatarVariant.BEAM,
                    colors = AvatarColors
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val event = activity.attributes.event

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(actorName)

                                withStyle(
                                    style = SpanStyle(
                                        color = LocalContentColor.current.copy(alpha = 0.5f),
                                    )
                                ) {
                                    append(" — ")
                                }

                                append(event.toString())
                            }
                        )

                        if (activity.attributes.isApi) {
                            Text(
                                text = "API",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = LocalContentColor.current.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    val eventDescription = event.description

                    val boldEventDescription = eventDescription.replace(VariableRegex, "<b>{{$1}}</b>")

                    val parsedEventDescription = parseActivityVariables(
                        input = boldEventDescription,
                        properties = activity.attributes.properties
                    )

                    Text(
                        text = AnnotatedString.fromHtml(parsedEventDescription)
                    )

                    val timeAgo = HumanReadable.timeAgo(
                        Instant.parse(activity.attributes.timestamp)
                    )

                    val userIp = activity.attributes.ip

                    Text(
                        text = buildAnnotatedString {
                            append(userIp)

                            withStyle(
                                style = SpanStyle(
                                    color = LocalContentColor.current.copy(alpha = 0.5f),
                                )
                            ) {
                                append(" | ")
                            }

                            append(timeAgo)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            if (userAgent is String || activity.attributes.hasAdditionalMetadata) {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    if (activity.attributes.hasAdditionalMetadata) {
                        IconButton(
                            onClick = onOpenMetadata,
                            icon = painterResource(R.drawable.assignment),
                            iconContentDescription = "View Activity Details"
                        )
                    }

                    if (userAgent is String && activity.attributes.hasAdditionalMetadata) {
                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )
                    }

                    if (userAgent is String) {
                        IconButton(
                            onClick = {
                                showUserAgent = !showUserAgent
                            },
                            icon = painterResource(R.drawable.desktop_windows),
                            iconContentDescription = "View User Agent"
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showUserAgent && userAgent is String
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "User Agent",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = userAgent as? String ?: "",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = LocalContentColor.current.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

val previewActivity = ActivityLog(
    attributes = ActivityLog.Attributes(
        id = "1",
        batch = null,
        event = ActivityLog.Attributes.Event.SERVER_FILE_RENAME_ONE,
        isApi = true,
        ip = "127.0.0.1",
        description = "idk",
        properties = mapOf(
            "useragent" to "hello",
            "directory" to mapOf(
                "files" to listOf(
                    mapOf(
                        "from" to "old_file",
                        "to" to "new_file"
                    )
                )
            )
        ),
        hasAdditionalMetadata = true,
        timestamp = "2026-06-08T20:23:55+02:00",
        relationships = ActivityLog.Attributes.Relationships(
            actor = ActivityLog.Attributes.Relationships.Actor(
                attributes = ActivityLog.Attributes.Relationships.Actor.Attributes(
                    uuid = "1da617ee-3cdc-4651-a1c5-7cfbaf1f22dc",
                    identifier = "user_dwtbp3r43rdfdiofpt526hzc3q",
                    username = "stef",
                    email = "me@stefdp.com",
                    image = "https://gravatar.com/avatar/bd0bc63436016cb66627527b80144c35",
                    twoFactorAuthenticationEnabled = false,
                    createdAt = "2025-11-30T23:33:15+01:00"
                )
            )
        )
    )
)

@Preview
@Composable
fun ActivityDisplayPreview() {
    PterodactylPanelTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    ActivityDisplay(
                        activity = previewActivity,
                        onOpenMetadata = {}
                    )
                }
            }
        }
    }
}