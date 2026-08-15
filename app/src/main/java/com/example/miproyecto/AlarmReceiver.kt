package com.example.miproyecto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val hora = intent.getStringExtra("EXTRA_HORA") ?: "7:30"
        val medicamento = intent.getStringExtra("EXTRA_MEDICAMENTO") ?: "Medicina"

        Toast.makeText(context, "¡Es hora de: $medicamento!", Toast.LENGTH_LONG).show()

        // Notificar al reloj que la alarma debe activarse (abrir pantalla)
        enviarActivacionAlReloj(context, hora, medicamento)
    }

    private fun enviarActivacionAlReloj(context: Context, hora: String, medicamento: String) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nodes = Wearable.getNodeClient(appContext).connectedNodes.await()
                val mensaje = "$hora|$medicamento"

                for (node in nodes) {
                    Wearable.getMessageClient(appContext)
                        .sendMessage(node.id, "/activar_alarma", mensaje.toByteArray())
                        .await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
