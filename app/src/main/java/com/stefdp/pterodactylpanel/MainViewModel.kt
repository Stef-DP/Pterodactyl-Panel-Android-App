package com.stefdp.pterodactylpanel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.stefdp.pterodactylpanel.network.application.requests.listUsers
import com.stefdp.pterodactylpanel.network.client.models.User
import com.stefdp.pterodactylpanel.network.client.requests.getAccount
import com.stefdp.pterodactylpanel.utils.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val loggedUser: User? = null,
)

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow<MainUiState>(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    suspend fun updateLoggedUser(context: Context): Result<User> {
        val tag = "MainActivity[updateLoggedUser]"

        Logger.debug(tag, "Checking is user is already logged in...")

        val accountRes = getAccount(
            context = context
        )

        accountRes
            .onSuccess { user ->
                Logger.debug(tag, "User is logged in as ${user.attributes.username}")

                _state.update {
                    it.copy(loggedUser = user)
                }

                return@updateLoggedUser Result.success(user)
            }
            .onFailure { error ->
                Logger.debug(tag, "User is not logged in")
                Logger.error(tag, "Failed to fetch user data: ${error.message}", error)

                _state.update {
                    it.copy(loggedUser = null)
                }

                return@updateLoggedUser Result.failure(error)
            }

        return Result.failure(
            Exception("Something went wrong...")
        )
    }
}