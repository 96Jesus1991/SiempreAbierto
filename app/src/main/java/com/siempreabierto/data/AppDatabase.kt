package com.siempreabierto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.siempreabierto.data.dao.*
import com.siempreabierto.data.entities.*

/**
 * Base de datos principal de Siempre Abierto
 * * Toda la información se guarda LOCALMENTE en el dispositivo.
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
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
        }
    }
}

/**
 * Callback para inicializar datos si fuera necesario
 */
class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        super.onCreate(db)
        // Aquí podrías precargar datos iniciales
    }
}
