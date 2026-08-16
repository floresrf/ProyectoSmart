package com.example.reloj

import android.util.Log
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearDataService : WearableListenerService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("WearDataService", "¡Servicio de Sincronización Iniciado!")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("WearDataService", "MENSAJE DETECTADO: ${messageEvent.path}")
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
                mostrarNotificacion(hora, medicamento)
            }
        }
    }

    private fun mostrarNotificacion(hora: String, medicamento: String) {
        val channelId = "alarma_medicina"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarmas de Medicina",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para la toma de medicamentos"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_HORA", hora)
            putExtra("EXTRA_MEDICAMENTO", medicamento)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("¡Hora de medicina!")
            .setContentText("Tomar $medicamento a las $hora")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true) // Esto hace que la pantalla aparezca de inmediato
            .build()

        notificationManager.notify(1001, notification)
    }
}
