package com.aruack.music.di

import androidx.room.Room
import com.aruack.music.core.database.AruackDatabase
import com.aruack.music.core.datastore.UserPreferencesRepository
import com.aruack.music.core.playback.AudioPlayerController
import com.aruack.music.core.playback.PlaybackManager
import com.aruack.music.core.repository.MusicRepository
import com.aruack.music.core.repository.MusicRepositoryImpl
import com.aruack.music.core.source.local.LocalMediaStoreSource
import com.aruack.music.core.source.remote.archive.InternetArchiveMusicSource
import com.aruack.music.core.source.remote.jamendo.JamendoMusicSource
import com.aruack.music.ui.screens.home.HomeViewModel
import com.aruack.music.ui.screens.library.LibraryViewModel
import com.aruack.music.ui.screens.player.NowPlayingViewModel
import com.aruack.music.ui.screens.playlist.PlaylistDetailViewModel
import com.aruack.music.ui.screens.search.SearchViewModel
import com.aruack.music.ui.screens.settings.SettingsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Ktor Direct Client for Legal Public APIs
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }

    // Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AruackDatabase::class.java,
            AruackDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    single { get<AruackDatabase>().songDao() }
    single { get<AruackDatabase>().playlistDao() }
    single { get<AruackDatabase>().favoriteDao() }
    single { get<AruackDatabase>().historyDao() }
    single { get<AruackDatabase>().queueDao() }

    // DataStore User Preferences
    single { UserPreferencesRepository(androidContext()) }

    // Music Sources
    single { LocalMediaStoreSource(androidContext()) }
    single { JamendoMusicSource(get()) }
    single { InternetArchiveMusicSource(get()) }

    // Repository
    single<MusicRepository> {
        MusicRepositoryImpl(
            localSource = get(),
            jamendoSource = get(),
            archiveSource = get(),
            database = get(),
            preferencesRepository = get()
        )
    }

    // Playback Controller & Manager
    single { AudioPlayerController(androidContext()) }
    single { PlaybackManager(get(), get(), get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get()) }
    viewModel { LibraryViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { NowPlayingViewModel(get(), get()) }
    viewModel { (playlistId: Long) -> PlaylistDetailViewModel(playlistId, get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
}
