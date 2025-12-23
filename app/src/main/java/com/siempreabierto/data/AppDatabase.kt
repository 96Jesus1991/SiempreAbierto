package com.siempreabierto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.siempreabierto.data.dao.*
import com.siempreabierto.data.entities.*

/**
 * Base de datos principal de Siempre Abierto
 * 
 * Toda la información se guarda LOCALMENTE en el dispositivo.
 * Sin Firebase, sin servidores externos obligatorios.
 * Privacidad total.
 */
@Database(
    entities = [
        Place::class,
        Restriction::class,
        Contribution::class,
        Helper::class,
        HelpRequest::class,
        Route::class,
        UserSettings::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    
    // DAOs
    abstract fun placeDao(): PlaceDao
    abstract fun restrictionDao(): RestrictionDao
    abstract fun contributionDao(): ContributionDao
    abstract fun helperDao(): HelperDao
    abstract fun routeDao(): RouteDao
    abstract fun userSettingsDao(): UserSettingsDao
    
    companion object {
        private const val DATABASE_NAME = "siempreabierto.db"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Obtener instancia de la base de datos (Singleton)
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        /**
         * Construir la base de datos
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                // Permitir migraciones destructivas en desarrollo
                // TODO: Implementar migraciones reales para producción
                .fallbackToDestructiveMigration()
                // Callback para inicializar datos por defecto
                .addCallback(DatabaseCallback(context))
                .build()
        }
        
        /**
         * Cerrar la base de datos
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}

/**
 * Callback para inicializar la base de datos
 */
class DatabaseCallback(
    private val context: Context
) : RoomDatabase.Callback() {
    
    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        super.onCreate(db)
        // La base de datos se acaba de crear
        // Aquí se pueden insertar datos iniciales si es necesario
    }
    
    override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        super.onOpen(db)
        // La base de datos se ha abierto
    }
}

/**
 * Proveedor de base de datos para inyección de dependencias simple
 */
object DatabaseProvider {
    
    private var database: AppDatabase? = null
    
    /**
     * Inicializar la base de datos
     */
    fun init(context: Context) {
        if (database == null) {
            database = AppDatabase.getInstance(context)
        }
    }
    
    /**
     * Obtener la base de datos
     */
    fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException(
            "Database not initialized. Call DatabaseProvider.init(context) first."
        )
    }
    
    /**
     * Accesos directos a los DAOs
     */
    fun placeDao(): PlaceDao = getDatabase().placeDao()
    fun restrictionDao(): RestrictionDao = getDatabase().restrictionDao()
    fun contributionDao(): ContributionDao = getDatabase().contributionDao()
    fun helperDao(): HelperDao = getDatabase().helperDao()
    fun routeDao(): RouteDao = getDatabase().routeDao()
    fun userSettingsDao(): UserSettingsDao = getDatabase().userSettingsDao()
}
