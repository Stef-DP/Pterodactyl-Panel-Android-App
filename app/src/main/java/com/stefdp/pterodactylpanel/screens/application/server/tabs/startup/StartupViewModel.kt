package com.stefdp.pterodactylpanel.screens.application.server.tabs.startup

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.application.models.ApplicationNest
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServer
import com.stefdp.pterodactylpanel.network.application.models.ApplicationServerVariable
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestEggsQueryInclude
import com.stefdp.pterodactylpanel.network.application.models.requests.ListNestsQueryInclude
import com.stefdp.pterodactylpanel.network.application.requests.updateServerStartup
import com.stefdp.pterodactylpanel.network.models.plus
import com.stefdp.pterodactylpanel.network.models.toQueryString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApplicationServerStartupTabUiState(
    val isLoading: Boolean = true,
    val startupCommand: TextFieldValue = TextFieldValue(""),
    val defaultStartupCommand: TextFieldValue = TextFieldValue(""),
    val nest: Set<String> = emptySet(),
    val egg: Set<String> = emptySet(),
    val skipEggInstallScript: Boolean = false,
    val dockerImage: Set<String> = emptySet(),
    val customDockerImage: TextFieldValue = TextFieldValue(""),
    val variables: List<ApplicationServerVariable> = emptyList(),
    val variableContent: Map<String, TextFieldValue> = emptyMap(),
    val nests: List<ApplicationNest> = emptyList(),
    val nestsLoading: Boolean = true,
)

private const val TAG = "ApplicationServerStartupTabViewModel"

class ApplicationServerStartupTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ApplicationServerStartupTabUiState> = MutableStateFlow(ApplicationServerStartupTabUiState())
    val state: StateFlow<ApplicationServerStartupTabUiState> = _state.asStateFlow()

    private var server: ApplicationServer? = null

    fun init(
        context: Context,
        server: ApplicationServer?
    ) {
        viewModelScope.launch {
            this@ApplicationServerStartupTabViewModel.server = server

            _state.update {
                it.copy(
                    isLoading = false,
                    startupCommand = TextFieldValue(server?.attributes?.relationships?.egg?.attributes?.startup ?: ""),
                )
            }

            val nests = listNests(context)

            _state.update {
                it.copy(
                    nests = nests,
                    nestsLoading = false,
                )
            }

            setNest(
                server?.attributes?.nest?.toString()
                    ?.let { setOf(it) }
                    ?: emptySet()
            )

            setEgg(
                server?.attributes?.egg?.toString()
                    ?.let { setOf(it) }
                    ?: emptySet()
            )
        }
    }

    private suspend fun listNests(
        context: Context
    ): List<ApplicationNest> {
        val outputNests = mutableListOf<ApplicationNest>()

        var currentPage = 1L
        var hasNextPage = true

        while (hasNextPage) {
            val nestsRes = com.stefdp.pterodactylpanel.network.application.requests.listNests(
                context = context,
                page = currentPage,
                include = listOf(
                    ListNestsQueryInclude.EGGS,
                    ListNestsQueryInclude.EGGS + ListNestEggsQueryInclude.VARIABLES
                ).toQueryString()
            )

            if (nestsRes.isFailure) break

            val nests = nestsRes.getOrNull() ?: break

            outputNests.addAll(nests.data)

            val nextLink = nests.meta.pagination.links.next

            if (!nextLink.isNullOrEmpty()) {
                currentPage++
            } else {
                hasNextPage = false
            }
        }

        return outputNests
    }

    fun setNest(selectedNest: Set<String>) {
        _state.update {
            it.copy(
                nest = selectedNest
            )
        }

        val nestId = selectedNest.firstOrNull()?.toLongOrNull()
        val nest = _state.value.nests.find { it.attributes.id == nestId }

        if (nest == null) {
            setEgg(emptySet())

            return
        }

        val eggs = nest.attributes.relationships?.eggs?.data
        val firstEgg = eggs?.firstOrNull()?.attributes?.id?.toString()

        if (firstEgg == null) {
            setEgg(emptySet())

            return
        }

        setEgg(setOf(firstEgg))
    }

    fun setEgg(selectedEgg: Set<String>) {
        _state.update {
            it.copy(
                egg = selectedEgg
            )
        }

        val nestId = _state.value.nest.firstOrNull()?.toLongOrNull()
        val eggId = selectedEgg.firstOrNull()?.toLongOrNull()

        val nest = _state.value.nests.find { it.attributes.id == nestId }
        val eggs = nest?.attributes?.relationships?.eggs?.data
        val egg = eggs?.find { it.attributes.id == eggId }

        if (egg == null) {
            setDockerImage(emptySet())
            setVariables(emptyList())
            setVariableContents(emptyMap())

            return
        }

        val isServerOriginalEgg = eggId == server?.attributes?.egg

        val dockerImage = if (isServerOriginalEgg) {
            server?.attributes?.container?.image ?: egg.attributes.dockerImage
        } else {
            egg.attributes.dockerImage
        }

        val startupCommand = if (isServerOriginalEgg) {
            server?.attributes?.container?.startupCommand ?: egg.attributes.startup
        } else {
            egg.attributes.startup
        }

        val defaultStartupCommand = egg.attributes.startup

        val variables = if (isServerOriginalEgg) {
            server?.attributes?.relationships?.variables?.data ?: egg.attributes.relationships?.variables?.data ?: emptyList()
        } else {
            egg.attributes.relationships?.variables?.data ?: emptyList()
        }

        if (dockerImage !in egg.attributes.dockerImages.map { it.value }) {
            setCustomDockerImage(TextFieldValue(dockerImage))
            setDockerImage(emptySet())
        } else {
            setDockerImage(setOf(dockerImage))
        }

        setVariables(variables)
        setStartupCommand(TextFieldValue(startupCommand))

        setVariableContents(
            variables.associate {
                val existingVariable = server?.attributes?.relationships?.variables?.data?.find { variable -> variable.attributes.envVariable == it.attributes.envVariable }

                it.attributes.envVariable to TextFieldValue(
                    existingVariable?.attributes?.serverValue ?: it.attributes.defaultValue
                )
            }
        )

        _state.update {
            it.copy(
                defaultStartupCommand = TextFieldValue(defaultStartupCommand)
            )
        }
    }

    fun setSkipEggInstallScript(skipEggInstallScript: Boolean) {
        _state.update {
            it.copy(
                skipEggInstallScript = skipEggInstallScript
            )
        }
    }

    fun setDockerImage(dockerImage: Set<String>) {
        _state.update {
            it.copy(
                dockerImage = dockerImage
            )
        }
    }

    fun setCustomDockerImage(customDockerImage: TextFieldValue) {
        _state.update {
            it.copy(
                customDockerImage = customDockerImage
            )
        }
    }

    fun setStartupCommand(startupCommand: TextFieldValue) {
        _state.update {
            it.copy(
                startupCommand = startupCommand
            )
        }
    }

    private fun setVariableContents(variables: Map<String, TextFieldValue>) {
        _state.update {
            it.copy(
                variableContent = variables
            )
        }
    }

    fun setVariableContent(variable: String, content: TextFieldValue) {
        _state.update {
            it.copy(
                variableContent = it.variableContent
                    .toMutableMap()
                    .apply {
                        put(variable, content)
                    }
                    .toMap()
            )
        }
    }

    fun setVariables(variables: List<ApplicationServerVariable>) {
        _state.update {
            it.copy(
                variables = variables
            )
        }
    }

    fun updateServer(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (server == null) {
                onError("Missing Server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val updateServerRes = updateServerStartup(
                context = context,
                serverId = server!!.attributes.id,
                startup = _state.value.startupCommand.text.trim(),
                environment = _state.value.variableContent.mapValues { it.value.text.trim() },
                egg = _state.value.egg.firstOrNull()?.toLongOrNull() ?: server!!.attributes.egg,
                image = _state.value.dockerImage.firstOrNull()
                    ?: _state.value.customDockerImage.text.trim()
                    .ifEmpty { server!!.attributes.relationships?.egg?.attributes?.dockerImage },
                skipEggInstallScript = _state.value.skipEggInstallScript
            )

            updateServerRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.error(TAG, "Failed to update server startup: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update server startup: ${error.message}")
                }
        }
    }
}