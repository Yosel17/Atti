package yosel.dev.atti

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import yosel.dev.atti.core.room.config.AppDatabase
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class MyBaseApplication : Application(){

    // Usamos Provider para retrasar la inyección
    @Inject
    lateinit var supabaseClient: Provider<SupabaseClient>

    @Inject
    lateinit var appDatabase: Provider<AppDatabase>

    override fun onCreate() {
        super.onCreate()

        // Lo mandamos a inicializar al hilo secundario (Background)
        CoroutineScope(Dispatchers.IO).launch {
            // El .get() aquí construirá Supabase y Room sin congelar la UI
            supabaseClient.get()
            appDatabase.get().openHelper.readableDatabase
        }
    }
}