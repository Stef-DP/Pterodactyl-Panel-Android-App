package com.stefdp.pterodactylpanel.screens.application.nestegg.tabs.installscript

import android.content.Context
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.neoutils.highlight.compose.remember.rememberTextFieldValue
import com.neoutils.highlight.core.Highlight
import com.stefdp.pterodactylpanel.components.TextInput
import com.stefdp.pterodactylpanel.network.application.models.ApplicationEgg
import com.stefdp.pterodactylpanel.ui.theme.languageToHighlightColors
import com.stefdp.pterodactylpanel.ui.theme.shellHighlightColors
import com.stefdp.pterodactylpanel.utils.verticalScrollWithScrollbar
import kotlin.collections.orEmpty

@Composable
fun InstallScriptTab(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: ApplicationNestEggInstallScriptTabViewModel = viewModel(),
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

    val refreshScrollState = rememberScrollableState { 0f }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(
                state = refreshScrollState,
                orientation = Orientation.Vertical
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = true)
        ) {
            val highlight = Highlight(shellHighlightColors)

            TextInput(
                value = highlight.rememberTextFieldValue(
                    TextFieldValue(state.egg?.attributes?.script?.install ?: "")
                ),
                onValueChange = {},
                label = "Install Script",
                readOnly = true,
                singleLine = false,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        TextInput(
            value = TextFieldValue(state.egg?.attributes?.script?.container ?: ""),
            onValueChange = {},
            label = "Script Container",
            description = "Docker container to use when running this script for the server",
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        TextInput(
            value = TextFieldValue(state.egg?.attributes?.script?.entry ?: ""),
            onValueChange = {},
            label = "Script Entrypoint Command",
            description = "The entrypoint command to use for this script",
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        val reliesOn = state.egg?.attributes?.script?.extends

        if (reliesOn != null) {
            Text(
                text = "The following service options rely on this script: ${reliesOn}"
            )
        }
    }
}