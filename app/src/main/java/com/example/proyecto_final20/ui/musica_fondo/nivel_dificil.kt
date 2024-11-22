package com.example.proyecto_final20.ui.musica_fondo

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.proyecto_final20.R

class NivelIntermedioMusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.nivel_intermedio)  // Música para nivel fácil
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY  // Mantener el servicio en ejecución hasta que sea detenido explícitamente
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener la música al destruir el servicio
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
