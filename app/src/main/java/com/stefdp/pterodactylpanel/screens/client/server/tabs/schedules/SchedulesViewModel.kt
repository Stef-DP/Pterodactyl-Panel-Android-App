package com.stefdp.pterodactylpanel.screens.client.server.tabs.schedules

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.pterodactylpanel.Logger
import com.stefdp.pterodactylpanel.network.client.models.ServerPowerSignal
import com.stefdp.pterodactylpanel.network.client.models.ServerSchedule
import com.stefdp.pterodactylpanel.network.client.models.ServerScheduleTask
import com.stefdp.pterodactylpanel.network.client.models.responses.GetServerResponse
import com.stefdp.pterodactylpanel.network.client.requests.createServerSchedule
import com.stefdp.pterodactylpanel.network.client.requests.createServerScheduleTask
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerSchedule
import com.stefdp.pterodactylpanel.network.client.requests.deleteServerScheduleTask
import com.stefdp.pterodactylpanel.network.client.requests.executeServerSchedule
import com.stefdp.pterodactylpanel.network.client.requests.listServerSchedules
import com.stefdp.pterodactylpanel.network.client.requests.updateServerSchedule
import com.stefdp.pterodactylpanel.network.client.requests.updateServerScheduleTask
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
    val showCreateScheduleTaskPopup: Boolean = false,
    val scheduleTaskToDelete: Long? = null,
    val scheduleTaskToEdit: Long? = null,
    val newScheduleTaskSelectedAction: ServerScheduleTask.Attributes.Action = ServerScheduleTask.Attributes.Action.COMMAND,
    val newScheduleTaskTimeOffset: TextFieldValue = TextFieldValue("0"),
    val newScheduleTaskPayload: TextFieldValue = TextFieldValue(""),
    val newScheduleTaskContinueOnFailure: Boolean = false,
    val editScheduleName: TextFieldValue = TextFieldValue(""),
    val editScheduleCronMinute: TextFieldValue = TextFieldValue("*/5"),
    val editScheduleCronHour: TextFieldValue = TextFieldValue("*"),
    val editScheduleCronDayOfMonth: TextFieldValue = TextFieldValue("*"),
    val editScheduleCronMonth: TextFieldValue = TextFieldValue("*"),
    val editScheduleCronDayOfWeek: TextFieldValue = TextFieldValue("*"),
    val editScheduleShowCheatsheet: Boolean = false,
    val editScheduleOnlyWhenOnline: Boolean = true,
    val editScheduleEnabled: Boolean = true,
    val editScheduleTaskSelectedAction: ServerScheduleTask.Attributes.Action = ServerScheduleTask.Attributes.Action.COMMAND,
    val editScheduleTaskTimeOffset: TextFieldValue = TextFieldValue("0"),
    val editScheduleTaskPayload: TextFieldValue = TextFieldValue(""),
    val editScheduleTaskContinueOnFailure: Boolean = false,
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

    fun setScheduleToDelete(
        scheduleId: Long?,
        skipLoading: Boolean = false
    ) {
        if (scheduleId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                scheduleToDelete = scheduleId
            )
        }
    }

    fun deleteSchedule(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val scheduleId = _state.value.scheduleToDelete

            if (scheduleId == null) {
                onError("Missing schedule ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val deleteScheduleRes = deleteServerSchedule(
                context = context,
                serverId = serverId!!,
                scheduleId = scheduleId
            )

            deleteScheduleRes
                .onSuccess {
                    updateSchedules(
                        context = context,
                        onError = onError,
                        onSuccess = {
                            setScheduleToDelete(null, true)
                            setScheduleToDisplayDetails(null)

                            onSuccess()
                        }
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSchedulesTabViewModel", "Failed to delete server schedule: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete server schedule: ${error.message}")
                }
        }
    }

    fun setScheduleToEdit(
        schedule: ServerSchedule.Attributes?,
        skipLoading: Boolean = false
    ) {
        if (schedule == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            if (schedule == null) {
                it.copy(
                    scheduleToEdit = null,
                    editScheduleName = TextFieldValue(""),
                    editScheduleCronMinute = TextFieldValue("*/5"),
                    editScheduleCronHour = TextFieldValue("*"),
                    editScheduleCronDayOfMonth = TextFieldValue("*"),
                    editScheduleCronMonth = TextFieldValue("*"),
                    editScheduleCronDayOfWeek = TextFieldValue("*"),
                    editScheduleShowCheatsheet = false,
                    editScheduleOnlyWhenOnline = true,
                    editScheduleEnabled = true
                )
            } else {
                it.copy(
                    scheduleToEdit = schedule.id,
                    editScheduleName = TextFieldValue(schedule.name),
                    editScheduleCronMinute = TextFieldValue(schedule.cron.minute),
                    editScheduleCronHour = TextFieldValue(schedule.cron.hour),
                    editScheduleCronDayOfMonth = TextFieldValue(schedule.cron.dayOfMonth),
                    editScheduleCronMonth = TextFieldValue(schedule.cron.month),
                    editScheduleCronDayOfWeek = TextFieldValue(schedule.cron.dayOfWeek),
                    editScheduleShowCheatsheet = false,
                    editScheduleOnlyWhenOnline = schedule.onlyWhenOnline,
                    editScheduleEnabled = schedule.isActive
                )
            }
        }
    }

    fun updateSchedule(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val scheduleId = _state.value.scheduleToEdit

            if (scheduleId == null) {
                onError("Missing schedule ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val editScheduleRes = updateServerSchedule(
                context = context,
                serverId = serverId!!,
                scheduleId = scheduleId,
                name = _state.value.editScheduleName.text.trim(),
                minute = _state.value.editScheduleCronMinute.text.trim(),
                hour = _state.value.editScheduleCronHour.text.trim(),
                dayOfMonth = _state.value.editScheduleCronDayOfMonth.text.trim(),
                month = _state.value.editScheduleCronMonth.text.trim(),
                dayOfWeek = _state.value.editScheduleCronDayOfWeek.text.trim(),
                onlyWhenOnline = _state.value.editScheduleOnlyWhenOnline,
                isActive = _state.value.editScheduleEnabled
            )

            editScheduleRes
                .onSuccess {
                    updateSchedules(
                        context = context,
                        onError = onError,
                        onSuccess = {
                            setScheduleToEdit(null, true)

                            onSuccess()
                        }
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSchedulesTabViewModel", "Failed to update server schedule: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update server schedule: ${error.message}")
                }
        }
    }

    fun setEditScheduleName(name: TextFieldValue) {
        _state.update {
            it.copy(
                editScheduleName = name
            )
        }
    }

    fun setEditScheduleCronMinute(cronMinute: TextFieldValue) {
        _state.update {
            it.copy(
                editScheduleCronMinute = cronMinute
            )
        }
    }

    fun setEditScheduleCronHour(cronHour: TextFieldValue) {
        _state.update {
            it.copy(
                editScheduleCronHour = cronHour
            )
        }
    }

    fun setEditScheduleCronDayOfMonth(cronDayOfMonth: TextFieldValue) {
        _state.update {
            it.copy(
                editScheduleCronDayOfMonth = cronDayOfMonth
            )
        }
    }

    fun setEditScheduleCronMonth(cronMonth: TextFieldValue) {
        _state.update {
            it.copy(
                editScheduleCronMonth = cronMonth
            )
        }
    }

    fun setEditScheduleCronDayOfWeek(cronDayOfWeek: TextFieldValue) {
        _state.update {
            it.copy(
                editScheduleCronDayOfWeek = cronDayOfWeek
            )
        }
    }

    fun setEditScheduleShowCheatsheet(showCheatsheet: Boolean) {
        _state.update {
            it.copy(
                editScheduleShowCheatsheet = showCheatsheet
            )
        }
    }

    fun setEditScheduleOnlyWhenOnline(onlyWhenOnline: Boolean) {
        _state.update {
            it.copy(
                editScheduleOnlyWhenOnline = onlyWhenOnline
            )
        }
    }

    fun setEditScheduleEnabled(enabled: Boolean) {
        _state.update {
            it.copy(
                editScheduleEnabled = enabled
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

    fun showCreateScheduleTaskPopup() {
        _state.update {
            it.copy(
                showCreateScheduleTaskPopup = true,
                newScheduleTaskSelectedAction = ServerScheduleTask.Attributes.Action.COMMAND,
                newScheduleTaskTimeOffset = TextFieldValue("0"),
                newScheduleTaskPayload = TextFieldValue(""),
                newScheduleTaskContinueOnFailure = false
            )
        }
    }

    fun hideCreateScheduleTaskPopup(skipLoading: Boolean = false) {
        if (_state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                showCreateScheduleTaskPopup = false
            )
        }
    }

    fun createNewScheduleTask(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val scheduleId = _state.value.scheduleToDisplayDetails

            if (scheduleId == null) {
                onError("Missing schedule ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val newScheduleTaskRes = createServerScheduleTask(
                context = context,
                serverId = serverId!!,
                scheduleId = scheduleId,
                action = _state.value.newScheduleTaskSelectedAction,
                timeOffset = _state.value.newScheduleTaskTimeOffset.text.trim().toLong(),
                payload = _state.value.newScheduleTaskPayload.text.trim(),
                continueOnFailure = _state.value.newScheduleTaskContinueOnFailure
            )

            newScheduleTaskRes
                .onSuccess {
                    updateSchedules(
                        context = context,
                        onError = onError,
                        onSuccess = {
                            hideCreateScheduleTaskPopup(true)

                            onSuccess()
                        }
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSchedulesTabViewModel", "Failed to create server schedule task: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to create server schedule task: ${error.message}")
                }
        }
    }

    fun setScheduleTaskToDelete(
        scheduleTaskId: Long?,
        skipLoading: Boolean = false
    ) {
        if (scheduleTaskId == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            it.copy(
                scheduleTaskToDelete = scheduleTaskId
            )
        }
    }

    fun deleteScheduleTask(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val scheduleId = _state.value.scheduleToDisplayDetails

            if (scheduleId == null) {
                onError("Missing schedule ID")

                return@launch
            }

            val taskId = _state.value.scheduleTaskToDelete

            if (taskId == null) {
                onError("Missing task ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val deleteScheduleTaskRes = deleteServerScheduleTask(
                context = context,
                serverId = serverId!!,
                scheduleId = scheduleId,
                taskId = taskId
            )

            deleteScheduleTaskRes
                .onSuccess {
                    updateSchedules(
                        context = context,
                        onError = onError,
                        onSuccess = {
                            setScheduleTaskToDelete(null, true)

                            onSuccess()
                        }
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSchedulesTabViewModel", "Failed to delete server schedule task: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to delete server schedule task: ${error.message}")
                }
        }
    }

    fun setScheduleTaskToEdit(
        scheduleTask: ServerScheduleTask.Attributes?,
        skipLoading: Boolean = false
    ) {
        if (scheduleTask == null && _state.value.isLoading && !skipLoading) return

        _state.update {
            if (scheduleTask == null) {
                it.copy(
                    scheduleTaskToEdit = null,
                    editScheduleTaskSelectedAction = ServerScheduleTask.Attributes.Action.COMMAND,
                    editScheduleTaskTimeOffset = TextFieldValue("0"),
                    editScheduleTaskPayload = TextFieldValue(""),
                    editScheduleTaskContinueOnFailure = false
                )
            } else {
                it.copy(
                    scheduleTaskToEdit = scheduleTask.id,
                    editScheduleTaskSelectedAction = scheduleTask.action,
                    editScheduleTaskTimeOffset = TextFieldValue(scheduleTask.timeOffset.toString()),
                    editScheduleTaskPayload = TextFieldValue(scheduleTask.payload),
                    editScheduleTaskContinueOnFailure = scheduleTask.continueOnFailure
                )
            }
        }
    }

    fun setEditScheduleTaskSelectedAction(action: ServerScheduleTask.Attributes.Action) {
        _state.update {
            it.copy(
                editScheduleTaskPayload = if (action == ServerScheduleTask.Attributes.Action.POWER) {
                    TextFieldValue(ServerPowerSignal.START.toString())
                } else {
                    TextFieldValue("")
                },
                editScheduleTaskSelectedAction = action
            )
        }
    }

    fun setEditScheduleTaskTimeOffset(timeOffset: TextFieldValue) {
        _state.update {
            it.copy(
                editScheduleTaskTimeOffset = timeOffset
            )
        }
    }

    fun setEditScheduleTaskPayload(payload: TextFieldValue) {
        _state.update {
            it.copy(
                editScheduleTaskPayload = payload
            )
        }
    }

    fun setEditScheduleTaskContinueOnFailure(continueOnFailure: Boolean) {
        _state.update {
            it.copy(
                editScheduleTaskContinueOnFailure = continueOnFailure
            )
        }
    }

    fun updateScheduleTask(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val scheduleId = _state.value.scheduleToDisplayDetails

            if (scheduleId == null) {
                onError("Missing schedule ID")

                return@launch
            }

            val taskId = _state.value.scheduleTaskToEdit

            if (taskId == null) {
                onError("Missing task ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val updateScheduleTaskRes = updateServerScheduleTask(
                context = context,
                serverId = serverId!!,
                scheduleId = scheduleId,
                taskId = taskId,
                action = _state.value.editScheduleTaskSelectedAction,
                timeOffset = _state.value.editScheduleTaskTimeOffset.text.trim().toLong(),
                payload = _state.value.editScheduleTaskPayload.text.trim(),
                continueOnFailure = _state.value.editScheduleTaskContinueOnFailure
            )

            updateScheduleTaskRes
                .onSuccess {
                    updateSchedules(
                        context = context,
                        onError = onError,
                        onSuccess = {
                            setScheduleTaskToEdit(null)
                            setScheduleToEdit(null, true)

                            onSuccess()
                        }
                    )
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSchedulesTabViewModel", "Failed to update server schedule task: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to update server schedule task: ${error.message}")
                }
        }
    }

    fun setNewScheduleTaskSelectedAction(action: ServerScheduleTask.Attributes.Action) {
        _state.update {
            it.copy(
                newScheduleTaskPayload = if (action == ServerScheduleTask.Attributes.Action.POWER) {
                    TextFieldValue(ServerPowerSignal.START.toString())
                } else {
                    TextFieldValue("")
                },
                newScheduleTaskSelectedAction = action
            )
        }
    }

    fun setNewScheduleTaskTimeOffset(timeOffset: TextFieldValue) {
        _state.update {
            it.copy(
                newScheduleTaskTimeOffset = timeOffset
            )
        }
    }

    fun setNewScheduleTaskPayload(payload: TextFieldValue) {
        _state.update {
            it.copy(
                newScheduleTaskPayload = payload
            )
        }
    }

    fun setNewScheduleTaskContinueOnFailure(continueOnFailure: Boolean) {
        _state.update {
            it.copy(
                newScheduleTaskContinueOnFailure = continueOnFailure
            )
        }
    }

    fun executeSchedule(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (serverId == null) {
                onError("Missing server ID")

                return@launch
            }

            val scheduleId = _state.value.scheduleToDisplayDetails

            if (scheduleId == null) {
                onError("Missing schedule ID")

                return@launch
            }

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val executeScheduleRes = executeServerSchedule(
                context = context,
                serverId = serverId!!,
                scheduleId = scheduleId,
            )

            executeScheduleRes
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onSuccess()
                }
                .onFailure { error ->
                    Logger.debug("ClientServerSchedulesTabViewModel", "Failed to execute server schedule: ${error.message}")

                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }

                    onError("Failed to execute server schedule: ${error.message}")
                }
        }
    }
}