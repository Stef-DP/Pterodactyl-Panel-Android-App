package com.stefdp.pterodactylpanel.screens.application.nestegg.tabs.variables

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.stefdp.pterodactylpanel.components.Container
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar

@Composable
fun VariablesTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationNestEggVariablesTabViewModel = viewModel(),
    egg: ApplicationEgg?,
    refreshIndex: Int
) {
    val state by viewModel.state.collectAsState()

    var lastRefreshIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(egg?.attributes?.id, refreshIndex) {
        if (egg == null) return@LaunchedEffect

        val isFirstLoad = lastRefreshIndex == -1
        val isExplicitRefresh = refreshIndex != lastRefreshIndex && !isFirstLoad

        if (!isExplicitRefresh && !isFirstLoad) return@LaunchedEffect

        lastRefreshIndex = refreshIndex

        viewModel.init(egg)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWithScrollbar(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val variables = state.egg?.attributes?.relationships?.variables?.data

        if (variables.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Variables Available",
                    textAlign = TextAlign.Center,
                )
            }

            return@Column
        }

        variables.forEach { (_, variable) ->
            Container(
                title = {
                    Text(
                        text = variable.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            ) {
                TextInput(
                    value = TextFieldValue(variable.name),
                    onValueChange = {},
                    label = "Name",
                    readOnly = true,
                    modifier = Modifier.fillMaxSize()
                )

                TextInput(
                    value = TextFieldValue(variable.description),
                    onValueChange = {},
                    label = "Description",
                    readOnly = true,
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .height(150.dp)
                )

                TextInput(
                    value = TextFieldValue(variable.envVariable),
                    onValueChange = {},
                    label = "Environment Variable",
                    description = "This variable can be accessed in the startup command by using `${variable.envVariable}`",
                    descriptionColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    readOnly = true,
                    modifier = Modifier.fillMaxSize()
                )

                TextInput(
                    value = TextFieldValue(variable.defaultValue),
                    onValueChange = {},
                    label = "Default Value",
                    readOnly = true,
                    modifier = Modifier.fillMaxSize()
                )

                val permissions = listOfNotNull(
                    if (variable.userViewable) "User Viewable" else null,
                    if (variable.userEditable) "User Editable" else null
                ).joinToString(", ")

                TextInput(
                    value = TextFieldValue(permissions),
                    onValueChange = {},
                    label = "Permissions",
                    readOnly = true,
                    modifier = Modifier.fillMaxSize()
                )

                TextInput(
                    value = TextFieldValue(variable.rules),
                    onValueChange = {},
                    label = "Input Rules",
                    description = buildAnnotatedString {
                        append("These rules are defined using standard ")

                        withLink(
                            link = LinkAnnotation.Url(
                                url = "https://laravel.com/docs/5.7/validation#available-validation-rules",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                        ) {
                            append("Laravel Framework validation rules")
                        }
                    },
                    readOnly = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}