package com.example.reloj

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearDataService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val data = String(messageEvent.data).split("|")
        val hora = data.getOrNull(0) ?: ""
        val medicamento = data.getOrNull(1) ?: ""

        when (messageEvent.path) {
            "/configurar_alarma" -> {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Configuración recibida: $medicamento", Toast.LENGTH_SHORT).show()
                }
            }
            "/activar_alarma" -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("EXTRA_HORA", hora)
                    putExtra("EXTRA_MEDICAMENTO", medicamento)
                }
                startActivity(intent)
            }
        }
    }
}
