package android.example.homescout.di

import android.content.Context
import android.example.homescout.database.HomeScoutDatabase
import android.example.homescout.utils.Constants.HOMESCOUT_DATABASE_NAME
import android.example.homescout.utils.Constants.TRACKING_PREFERENCES
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext,TRACKING_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(TRACKING_PREFERENCES) }
        )
    }

    @Singleton
    @Provides
    fun provideHomeScoutDatabase(@ApplicationContext appContext: Context) = Room.databaseBuilder(
        appContext,
        HomeScoutDatabase::class.java,
        HOMESCOUT_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideBLEDeviceDao(db: HomeScoutDatabase) = db.getBLEDeviceDao()

    @Singleton
    @Provides
    fun provideMaliciousTrackerDao(db: HomeScoutDatabase) = db.getMaliciousTrackerDao()
}