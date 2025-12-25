package com.siempreabierto.ui.screens.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siempreabierto.data.DatabaseProvider
import com.siempreabierto.data.entities.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private val placeDao = DatabaseProvider.placeDao()
    private val contributionDao = DatabaseProvider.contributionDao()
    private val helperDao = DatabaseProvider.helperDao()
    private val userSettingsDao = DatabaseProvider.userSettingsDao()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val settings = userSettingsDao.getSettings() ?: return@launch
                
                // Nota: Estas funciones requieren que actualices tus DAOs posteriormente
                // Por ahora usamos valores por defecto si fallan para que no crashee
                val totalContributions = settings.totalContributions
                val placesCreated = settings.placesAdded
                val confirmations = settings.confirmationsMade

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userSettings = settings,
                    userId = settings.localUserId,
                    userName = settings.nickname,
                    totalContributions = totalContributions,
                    placesCreated = placesCreated,
                    confirmations = confirmations,
                    isHelper = settings.isHelper
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun addPlaceMock(type: String) {
        // Simulación para la demo (ya que falta el formulario completo)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            // Aquí iría la lógica real de insertar en DB
            kotlinx.coroutines.delay(1000) 
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                placeSaved = true,
                placesCreated = _uiState.value.placesCreated + 1,
                lastActionMessage = "Lugar añadido: $type (Simulado)"
            )
        }
    }

    fun toggleHelperMode(enable: Boolean) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)
                // Actualizamos setting local
                userSettingsDao.updateHelperStatus(enable, if(enable) 123 else null)
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isHelper = enable,
                    lastActionMessage = if(enable) "¡Modo ayudante activado!" else "Modo ayudante desactivado"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }

    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(lastActionMessage = null)
    }
}

data class CommunityUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val lastActionMessage: String? = null,
    val userSettings: UserSettings? = null,
    val userId: String? = null,
    val userName: String? = null,
    val totalContributions: Int = 0,
    val placesCreated: Int = 0,
    val confirmations: Int = 0,
    val isHelper: Boolean = false,
    val placeSaved: Boolean = false
)
