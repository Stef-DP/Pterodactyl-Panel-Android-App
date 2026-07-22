package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerSchedule
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.createServerSchedule
import com.stefdp.pterodactylpanel.network.client.requests.listServerSchedules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientServerSchedulesTabUiState(
    val isLoading: Boolean = false,
    val schedules: List<ServerSchedule> = emptyList(),
    val scheduleToDisplayDetails: Long? = null,
    val scheduleToDelete: Long? = null,
    val scheduleToEdit: Long? = null,
    val showCreateSchedulePopup: Boolean = false,
    val newScheduleName: TextFieldValue = TextFieldValue(""),
    val newScheduleCronMinute: TextFieldValue = TextFieldValue("*/5"),
    val newScheduleCronHour: TextFieldValue = TextFieldValue("*"),
    val newScheduleCronDayOfMonth: TextFieldValue = TextFieldValue("*"),
    val newScheduleCronMonth: TextFieldValue = TextFieldValue("*"),
    val newScheduleCronDayOfWeek: TextFieldValue = TextFieldValue("*"),
    val newScheduleShowCheatsheet: Boolean = false,
    val newScheduleOnlyWhenOnline: Boolean = true,
    val newScheduleEnabled: Boolean = true,
)

class ClientServerSchedulesTabViewModel : ViewModel() {
    private val _state: MutableStateFlow<ClientServerSchedulesTabUiState> = MutableStateFlow(ClientServerSchedulesTabUiState())
    val state: StateFlow<ClientServerSchedulesTabUiState> = _state.asStateFlow()

    private var serverId: String? = null

    fun init(server: GetServerResponse?) {
        this.serverId = server?.attributes?.identifier
    }

    fun updateSchedules(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val listSchedulesRes = listServerSchedules(
                context = context,
                serverId = serverId!!,
            )

            listSchedulesRes
                .onSuccess { schedules ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            schedules = schedules.data
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSchedulesTabViewModel", "Failed to fetch server schedules: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to fetch server schedules: ${error.message}")
                }
        }
    }

    fun setScheduleToDisplayDetails(scheduleId: Long?) {
        _state.update {
            it.copy(
                scheduleToDisplayDetails = scheduleId
            )
        }
    }

    fun setScheduleToDelete(scheduleId: Long?) {
        _state.update {
            it.copy(
                scheduleToDelete = scheduleId
            )
        }
    }

    fun setScheduleToEdit(scheduleId: Long?) {
        _state.update {
            it.copy(
                scheduleToEdit = scheduleId
            )
        }
    }

    fun showCreateSchedulePopup() {
        _state.update {
            it.copy(
                showCreateSchedulePopup = true
            )
        }
    }

    fun hideCreateSchedulePopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateSchedulePopup = false,
                newScheduleName = TextFieldValue(""),
                newScheduleCronMinute = TextFieldValue("*/5"),
                newScheduleCronHour = TextFieldValue("*"),
                newScheduleCronDayOfMonth = TextFieldValue("*"),
                newScheduleCronMonth = TextFieldValue("*"),
                newScheduleCronDayOfWeek = TextFieldValue("*"),
                newScheduleShowCheatsheet = false,
                newScheduleOnlyWhenOnline = true,
                newScheduleEnabled = true
            )
        }
    }

    fun createSchedule(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val createScheduleRes = createServerSchedule(
                context = context,
                serverId = serverId!!,
                name = _state.value.newScheduleName.text.trim(),
                minute = _state.value.newScheduleCronMinute.text.trim(),
                hour = _state.value.newScheduleCronHour.text.trim(),
                dayOfMonth = _state.value.newScheduleCronDayOfMonth.text.trim(),
                month = _state.value.newScheduleCronMonth.text.trim(),
                dayOfWeek = _state.value.newScheduleCronDayOfWeek.text.trim(),
                onlyWhenOnline = _state.value.newScheduleOnlyWhenOnline,
                isActive = _state.value.newScheduleEnabled
            )

            createScheduleRes
                .onSuccess {
                    hideCreateSchedulePopup(true)

                    _state.update {
                        it.copy(
                            isLoading = false,

                        )
                    }

                    updateSchedules(
                        context = context,
                        onError = onError,
                        onSuccess = onSuccess
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSchedulesTabViewModel", "Failed to create server schedule: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create server schedule: ${error.message}")
                }
        }
    }

    fun setNewScheduleName(name: TextFieldValue) {
        _state.update {
            it.copy(
                newScheduleName = name
            )
        }
    }

    fun setNewScheduleCronMinute(cronMinute: TextFieldValue) {
        _state.update {
            it.copy(
                newScheduleCronMinute = cronMinute
            )
        }
    }

    fun setNewScheduleCronHour(cronHour: TextFieldValue) {
        _state.update {
            it.copy(
                newScheduleCronHour = cronHour
            )
        }
    }

    fun setNewScheduleCronDayOfMonth(cronDayOfMonth: TextFieldValue) {
        _state.update {
            it.copy(
                newScheduleCronDayOfMonth = cronDayOfMonth
            )
        }
    }

    fun setNewScheduleCronMonth(cronMonth: TextFieldValue) {
        _state.update {
            it.copy(
                newScheduleCronMonth = cronMonth
            )
        }
    }

    fun setNewScheduleCronDayOfWeek(cronDayOfWeek: TextFieldValue) {
        _state.update {
            it.copy(
                newScheduleCronDayOfWeek = cronDayOfWeek
            )
        }
    }

    fun setNewScheduleShowCheatsheet(showCheatsheet: Boolean) {
        _state.update {
            it.copy(
                newScheduleShowCheatsheet = showCheatsheet
            )
        }
    }

    fun setNewScheduleOnlyWhenOnline(onlyWhenOnline: Boolean) {
        _state.update {
            it.copy(
                newScheduleOnlyWhenOnline = onlyWhenOnline
            )
        }
    }

    fun setNewScheduleEnabled(enabled: Boolean) {
        _state.update {
            it.copy(
                newScheduleEnabled = enabled
            )
        }
    }
}