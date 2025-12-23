package com.siempreabierto.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siempreabierto.data.DatabaseProvider
import com.siempreabierto.data.entities.Place
import com.siempreabierto.data.entities.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de inicio
 */
class HomeViewModel : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // DAOs
    private val placeDao = DatabaseProvider.placeDao()
    private val userSettingsDao = DatabaseProvider.userSettingsDao()

    init {
        loadInitialData()
    }

    /**
     * Cargar datos iniciales
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Cargar configuración del usuario
                val settings = userSettingsDao.getSettings()
                
                // Contar lugares por categoría
                val workshopCount = placeDao.countByCategory("workshop")
                val gasStationCount = placeDao.countByCategory("gas_station")
                val parkingCount = placeDao.countByCategory("parking")
                val totalPlaces = placeDao.countActive()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userSettings = settings,
                    totalPlaces = totalPlaces,
                    workshopCount = workshopCount,
                    gasStationCount = gasStationCount,
                    parkingCount = parkingCount,
                    userName = settings?.nickname
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Cargar lugares recientes
     */
    fun loadRecentPlaces() {
        viewModelScope.launch {
            try {
                placeDao.getRecentlyAdded(10).collect { places ->
                    _uiState.value = _uiState.value.copy(
                        recentPlaces = places
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * Buscar lugares
     */
    fun searchPlaces(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }

        viewModelScope.launch {
            try {
                placeDao.search(query, 20).collect { results ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * Refrescar datos
     */
    fun refresh() {
        loadInitialData()
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Estado de la UI de Home
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userSettings: UserSettings? = null,
    val userName: String? = null,
    val totalPlaces: Int = 0,
    val workshopCount: Int = 0,
    val gasStationCount: Int = 0,
    val parkingCount: Int = 0,
    val recentPlaces: List<Place> = emptyList(),
    val searchResults: List<Place> = emptyList()
)
