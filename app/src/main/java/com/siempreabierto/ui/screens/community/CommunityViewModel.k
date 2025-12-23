package com.siempreabierto.ui.screens.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siempreabierto.data.DatabaseProvider
import com.siempreabierto.data.entities.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Comunidad
 * Gestión de contribuciones y colaboración
 */
class CommunityViewModel : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    // DAOs
    private val placeDao = DatabaseProvider.placeDao()
    private val contributionDao = DatabaseProvider.contributionDao()
    private val helperDao = DatabaseProvider.helperDao()
    private val userSettingsDao = DatabaseProvider.userSettingsDao()

    init {
        loadUserData()
    }

    /**
     * Cargar datos del usuario
     */
    private fun loadUserData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val settings = userSettingsDao.getSettings()
                val userId = settings?.localUserId ?: return@launch

                // Cargar estadísticas de contribuciones
                val totalContributions = contributionDao.countByUser(userId)
                val placesCreated = contributionDao.countPlacesCreatedByUser(userId)
                val confirmations = contributionDao.countConfirmationsByUser(userId)
                val reports = contributionDao.countReportsByUser(userId)

                // Verificar si es ayudante
                val isHelper = settings.isHelper
                var helperProfile: Helper? = null
                if (isHelper && settings.helperProfileId != null) {
                    helperProfile = helperDao.getHelperById(settings.helperProfileId)
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userSettings = settings,
                    userId = userId,
                    userName = settings.nickname,
                    totalContributions = totalContributions,
                    placesCreated = placesCreated,
                    confirmations = confirmations,
                    reports = reports,
                    isHelper = isHelper,
                    helperProfile = helperProfile
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
     * Cargar contribuciones recientes del usuario
     */
    fun loadRecentContributions() {
        viewModelScope.launch {
            try {
                val userId = _uiState.value.userId ?: return@launch

                contributionDao.getRecentByUser(userId, 20).collect { contributions ->
                    _uiState.value = _uiState.value.copy(
                        recentContributions = contributions
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
     * Cargar lugares añadidos por el usuario
     */
    fun loadUserPlaces() {
        viewModelScope.launch {
            try {
                val userId = _uiState.value.userId ?: return@launch

                placeDao.getByContributor(userId).collect { places ->
                    _uiState.value = _uiState.value.copy(
                        userPlaces = places
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
     * Añadir un nuevo lugar
     */
    fun addPlace(
        name: String,
        category: String,
        latitude: Double,
        longitude: Double,
        address: String?,
        city: String?,
        province: String?,
        region: String,
        phone: String?,
        schedule: String?,
        is24h: Boolean,
        fitsTruck: Boolean?,
        fitsBus: Boolean?,
        notes: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)

                val userId = _uiState.value.userId ?: return@launch

                // Crear el lugar
                val place = Place(
                    name = name,
                    category = category,
                    latitude = latitude,
                    longitude = longitude,
                    address = address,
                    city = city,
                    province = province,
                    region = region,
                    phone = phone,
                    schedule = schedule,
                    is24h = is24h,
                    fitsTruck = fitsTruck,
                    fitsBus = fitsBus,
                    notes = notes,
                    contributedBy = userId,
                    source = "community"
                )

                val placeId = placeDao.insert(place)

                // Registrar contribución
                val contribution = Contribution(
                    targetType = ContributionTargets.PLACE,
                    targetId = placeId,
                    targetName = name,
                    action = ContributionActions.CREATE,
                    userId = userId,
                    userName = _uiState.value.userName
                )
                contributionDao.insert(contribution)

                // Actualizar estadísticas
                userSettingsDao.incrementPlacesAdded()

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    placeSaved = true,
                    placesCreated = _uiState.value.placesCreated + 1,
                    totalContributions = _uiState.value.totalContributions + 1
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Confirmar que un lugar sigue abierto/activo
     */
    fun confirmPlace(placeId: Long, placeName: String) {
        viewModelScope.launch {
            try {
                val userId = _uiState.value.userId ?: return@launch

                // Actualizar lugar
                placeDao.confirmPlace(placeId, userId)

                // Registrar contribución
                val contribution = Contribution(
                    targetType = ContributionTargets.PLACE,
                    targetId = placeId,
                    targetName = placeName,
                    action = ContributionActions.CONFIRM,
                    userId = userId,
                    userName = _uiState.value.userName
                )
                contributionDao.insert(contribution)

                // Actualizar estadísticas
                userSettingsDao.incrementConfirmations()

                _uiState.value = _uiState.value.copy(
                    confirmations = _uiState.value.confirmations + 1,
                    totalContributions = _uiState.value.totalContributions + 1,
                    lastActionMessage = "¡Gracias! Confirmación guardada."
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * Reportar un problema con un lugar
     */
    fun reportPlace(placeId: Long, placeName: String, reason: String) {
        viewModelScope.launch {
            try {
                val userId = _uiState.value.userId ?: return@launch

                // Incrementar contador de reportes
                placeDao.incrementReportCount(placeId)

                // Registrar contribución
                val contribution = Contribution(
                    targetType = ContributionTargets.PLACE,
                    targetId = placeId,
                    targetName = placeName,
                    action = ContributionActions.REPORT,
                    notes = reason,
                    userId = userId,
                    userName = _uiState.value.userName
                )
                contributionDao.insert(contribution)

                _uiState.value = _uiState.value.copy(
                    reports = _uiState.value.reports + 1,
                    totalContributions = _uiState.value.totalContributions + 1,
                    lastActionMessage = "Reporte enviado. ¡Gracias por tu ayuda!"
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * Activar/desactivar modo ayudante
     */
    fun toggleHelperMode(
        isHelper: Boolean,
        canHelpWith: List<String>? = null,
        phone: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        region: String? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)

                val userId = _uiState.value.userId ?: return@launch
                val userName = _uiState.value.userName

                if (isHelper && latitude != null && longitude != null && region != null) {
                    // Crear perfil de ayudante
                    val helper = Helper(
                        nickname = userName ?: "Ayudante anónimo",
                        localUserId = userId,
                        latitude = latitude,
                        longitude = longitude,
                        region = region,
                        phone = phone,
                        phoneVisible = phone != null,
                        canHelpWith = canHelpWith?.joinToString(",") ?: "",
                        vehicleType = _uiState.value.userSettings?.vehicleType ?: "car",
                        isAvailable = true,
                        isActive = true
                    )

                    val helperId = helperDao.insertHelper(helper)
                    userSettingsDao.updateHelperStatus(true, helperId)

                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isHelper = true,
                        helperProfile = helper.copy(id = helperId),
                        lastActionMessage = "¡Genial! Ya eres parte de la red de ayudantes."
                    )

                } else {
                    // Desactivar modo ayudante
                    val helperId = _uiState.value.helperProfile?.id
                    if (helperId != null) {
                        helperDao.setActive(helperId, false)
                    }
                    userSettingsDao.updateHelperStatus(false, null)

                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isHelper = false,
                        helperProfile = null,
                        lastActionMessage = "Has dejado de ser ayudante."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Actualizar disponibilidad de ayudante
     */
    fun updateHelperAvailability(isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                val helperId = _uiState.value.helperProfile?.id ?: return@launch
                helperDao.setAvailability(helperId, isAvailable)

                _uiState.value = _uiState.value.copy(
                    helperProfile = _uiState.value.helperProfile?.copy(isAvailable = isAvailable)
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * Actualizar nickname del usuario
     */
    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateNickname(nickname)
                
                _uiState.value = _uiState.value.copy(
                    userName = nickname
                )

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
        loadUserData()
    }

    /**
     * Limpiar mensaje de acción
     */
    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(lastActionMessage = null)
    }

    /**
     * Limpiar flag de lugar guardado
     */
    fun clearPlaceSaved() {
        _uiState.value = _uiState.value.copy(placeSaved = false)
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Estado de la UI de Comunidad
 */
data class CommunityUiState(
    // Estados de carga
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val lastActionMessage: String? = null,

    // Usuario
    val userSettings: UserSettings? = null,
    val userId: String? = null,
    val userName: String? = null,

    // Estadísticas
    val totalContributions: Int = 0,
    val placesCreated: Int = 0,
    val confirmations: Int = 0,
    val reports: Int = 0,

    // Modo ayudante
    val isHelper: Boolean = false,
    val helperProfile: Helper? = null,

    // Datos
    val recentContributions: List<Contribution> = emptyList(),
    val userPlaces: List<Place> = emptyList(),

    // Flags
    val placeSaved: Boolean = false
)
