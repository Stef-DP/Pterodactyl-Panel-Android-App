package com.stefdp.pterodactylpanel.screens.client.server.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stefdp.pterodactylpanel.BASE_CORNER_RADIUS
import com.stefdp.pterodactylpanel.R
import com.stefdp.pterodactylpanel.components.IconButton
import com.stefdp.pterodactylpanel.network.client.models.ServerDatabase
import com.stefdp.pterodactylpanel.screens.client.server.ClientServerViewModel

@Composable
fun DatabaseDisplay(
    database: ServerDatabase,
    viewModel: ClientServerViewModel
) {
    val databaseName = database.attributes.name

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
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.database_fill),
                    contentDescription = "Database Icon"
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = databaseName
                )
            }

            Row(
                modifier = Modifier.weight(0.5f),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(
                    icon = painterResource(R.drawable.visibility),
                    iconContentDescription = "Open Database Details",
                    onClick = {
                        viewModel.setDatabaseToShowDetails(database.attributes.id)
                    },
                    border = true
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                IconButton(
                    icon = painterResource(R.drawable.delete),
                    iconContentDescription = "Delete Database",
                    iconColor = MaterialTheme.colorScheme.error,
                    borderColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        viewModel.setDatabaseToDelete(database.attributes.id)
                    },
                    border = true
                )
            }
        }
    }
}