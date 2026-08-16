package com.example.miproyecto // Asegúrate de que coincida con tu paquete real

import android.util.Log
import android.os.Handler
import android.os.Looper
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.Wearable
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Locale

class Fragmento1 : Fragment() {

  private lateinit var tvHora: TextView
  private lateinit var tvMinuto: TextView
  private lateinit var tvAM: TextView
  private lateinit var tvPM: TextView
  private lateinit var layoutHoraSelector: LinearLayout
  private lateinit var ctNombreAlarma: TextInputEditText
  private lateinit var btnGuardarAlarma: MaterialButton

  private val diasSeleccionados = mutableSetOf<Int>()
  private var hora24 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
  private var minuto = Calendar.getInstance().get(Calendar.MINUTE)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_fragmento1, container, false)

    tvHora = view.findViewById(R.id.tvHora)
    tvMinuto = view.findViewById(R.id.tvMinuto)
    tvAM = view.findViewById(R.id.tvAM)
    tvPM = view.findViewById(R.id.tvPM)
    layoutHoraSelector = view.findViewById(R.id.layoutHoraSelector)
    ctNombreAlarma = view.findViewById(R.id.ctNombreAlarma)
    btnGuardarAlarma = view.findViewById(R.id.btnGuardarAlarma)

    // Inicializar la vista con el tiempo actual del sistema
    val hora12 = if (hora24 % 12 == 0) 12 else hora24 % 12
    tvHora.text = String.format(Locale.getDefault(), "%02d", hora12)
    tvMinuto.text = String.format(Locale.getDefault(), "%02d", minuto)

    if (hora24 >= 12) {
      tvAM.setTextColor(Color.parseColor("#80FFFFFF"))
      tvPM.setTextColor(Color.WHITE)
    } else {
      tvAM.setTextColor(Color.WHITE)
      tvPM.setTextColor(Color.parseColor("#80FFFFFF"))
    }

    layoutHoraSelector.setOnClickListener { abrirTimePicker() }
    configurarDiasSemanales(view)

    btnGuardarAlarma.setOnClickListener { programarYEnviarAlarma() }

    return view
  }

  private fun abrirTimePicker() {
    val ctx = context ?: return
    
    try {
      val timePicker = TimePickerDialog(
        ctx,
        { _, selectedHour, selectedMinute ->
          hora24 = selectedHour
          minuto = selectedMinute

          val hora12 = if (selectedHour % 12 == 0) 12 else selectedHour % 12
          tvHora.text = String.format(Locale.getDefault(), "%02d", hora12)
          tvMinuto.text = String.format(Locale.getDefault(), "%02d", selectedMinute)

          if (selectedHour >= 12) {
            tvAM.setTextColor(Color.parseColor("#80FFFFFF"))
            tvPM.setTextColor(Color.WHITE)
          } else {
            tvAM.setTextColor(Color.WHITE)
            tvPM.setTextColor(Color.parseColor("#80FFFFFF"))
          }
        },
        hora24,
        minuto,
        false
      )
      timePicker.show()
    } catch (e: Exception) {
      Toast.makeText(ctx, "Error al abrir el selector de hora", Toast.LENGTH_SHORT).show()
    }
  }

  private fun configurarDiasSemanales(view: View) {
    val mapaDias = mapOf(
      R.id.btnLunes to Calendar.MONDAY,
      R.id.btnMartes to Calendar.TUESDAY,
      R.id.btnMiercoles to Calendar.WEDNESDAY,
      R.id.btnJueves to Calendar.THURSDAY,
      R.id.btnViernes to Calendar.FRIDAY,
      R.id.btnSabado to Calendar.SATURDAY,
      R.id.btnDomingo to Calendar.SUNDAY
    )

    diasSeleccionados.addAll(listOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.THURSDAY))
    // También añadimos el día de hoy por defecto para facilitar las pruebas
    diasSeleccionados.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))

    mapaDias.forEach { (viewId, calendarDay) ->
      val tvDia = view.findViewById<TextView>(viewId)
      
      // Actualizar el estado visual inicial según los días seleccionados por defecto
      if (diasSeleccionados.contains(calendarDay)) {
        tvDia.setBackgroundColor(Color.parseColor("#BFA067"))
        tvDia.setTextColor(Color.WHITE)
      } else {
        tvDia.setBackgroundColor(Color.parseColor("#254445"))
        tvDia.setTextColor(Color.parseColor("#80FFFFFF"))
      }

      tvDia?.setOnClickListener {
        if (diasSeleccionados.contains(calendarDay)) {
          diasSeleccionados.remove(calendarDay)
          tvDia.setBackgroundColor(Color.parseColor("#254445"))
          tvDia.setTextColor(Color.parseColor("#80FFFFFF"))
        } else {
          diasSeleccionados.add(calendarDay)
          tvDia.setBackgroundColor(Color.parseColor("#BFA067"))
          tvDia.setTextColor(Color.WHITE)
        }
      }
    }
  }

  private fun programarYEnviarAlarma() {
    val etiqueta = ctNombreAlarma.text.toString().trim()

    if (etiqueta.isEmpty()) {
      Toast.makeText(requireContext(), "Escribe un nombre para la alarma", Toast.LENGTH_SHORT).show()
      return
    }

    val amPmText = if (hora24 >= 12) "PM" else "AM"
    val horaTexto = "${tvHora.text}:${tvMinuto.text} $amPmText"

    val contextSeguro = context ?: return
    val alarmManager = contextSeguro.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 1. Verificación de permiso para Android 12+ (Evita que la app truene al levantar la alarma)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      if (!alarmManager.canScheduleExactAlarms()) {
        Toast.makeText(contextSeguro, "Permite programar alarmas exactas para continuar", Toast.LENGTH_LONG).show()
        val intentPermiso = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        startActivity(intentPermiso)
        return
      }
    }

    // 2. Definir la hora objetivo
    val hoy = Calendar.getInstance()
    val calendar = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, hora24)
      set(Calendar.MINUTE, minuto)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }

    // Lógica para encontrar el próximo día válido según la selección
    if (diasSeleccionados.isEmpty()) {
      if (calendar.before(hoy)) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
      }
    } else {
      var diasEncontrado = false

      // Probamos desde hoy (0) hasta dentro de 7 días
      for (i in 0..7) {
        val diaPrueba = (hoy.get(Calendar.DAY_OF_WEEK) + i - 1) % 7 + 1
        if (diasSeleccionados.contains(diaPrueba)) {
          val tempCal = hoy.clone() as Calendar
          tempCal.add(Calendar.DAY_OF_MONTH, i)
          tempCal.set(Calendar.HOUR_OF_DAY, hora24)
          tempCal.set(Calendar.MINUTE, minuto)
          tempCal.set(Calendar.SECOND, 0)
          tempCal.set(Calendar.MILLISECOND, 0)

          // Si es hoy y la hora ya pasó (por segundos), buscamos el siguiente día
          if (tempCal.after(hoy)) {
            calendar.timeInMillis = tempCal.timeInMillis
            diasEncontrado = true
            break
          }
        }
      }

      if (!diasEncontrado) {
        Toast.makeText(contextSeguro, "No se encontró un día válido en la selección", Toast.LENGTH_SHORT).show()
        return
      }
    }

    // 3. Crear el Intent para el Receiver con ID ÚNICO
    val intent = Intent(contextSeguro, AlarmReceiver::class.java).apply {
      putExtra("EXTRA_HORA", horaTexto)
      putExtra("EXTRA_MEDICAMENTO", etiqueta)
    }

    // Usar un ID basado en el tiempo pero truncado de forma segura para evitar overflow negativo si se desea,
    // o simplemente usar un número aleatorio positivo.
    val uniqueId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    val pendingIntent = PendingIntent.getBroadcast(
      contextSeguro,
      uniqueId,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 4. Programar la alarma en el sistema
    alarmManager.setExactAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      calendar.timeInMillis,
      pendingIntent
    )

    // 5. Enviar actualización al reloj
    enviarAlarmaAlReloj(horaTexto, etiqueta)

    Toast.makeText(contextSeguro, "Alarma guardada y enviada al reloj", Toast.LENGTH_SHORT).show()
  }

  private fun enviarAlarmaAlReloj(hora: String, medicamento: String) {
    // Usa lifecycleScope para cancelar el envío si te cambias de pestaña
    lifecycleScope.launch(Dispatchers.IO) {
      try {
        // Se obtiene un contexto de aplicación seguro que no expira al cambiar de vista
        val appContext = context?.applicationContext ?: return@launch

        val nodes = Wearable.getNodeClient(appContext).connectedNodes.await()
        
        Log.d("Sincronizacion", "Buscando relojes... Nodos encontrados: ${nodes.size}")

        if (nodes.isEmpty()) {
          Handler(Looper.getMainLooper()).post {
            Toast.makeText(appContext, "Reloj no detectado. Revisa la conexión del emulador.", Toast.LENGTH_LONG).show()
          }
          return@launch
        }

        val mensaje = "$hora|$medicamento"

        for (node in nodes) {
          Log.d("Sincronizacion", "Enviando configuración a nodo: ${node.displayName} (ID: ${node.id})")
          
          // Enviamos usando el cliente de mensajes directamente al ID del nodo
          Wearable.getMessageClient(appContext)
            .sendMessage(node.id, "/configurar_alarma", mensaje.toByteArray())
            .addOnSuccessListener {
                Log.d("Sincronizacion", "¡Mensaje enviado con éxito al buffer de Google Play Services!")
            }
            .addOnFailureListener { e ->
                Log.e("Sincronizacion", "Fallo al entregar el mensaje al buffer", e)
            }
        }
      } catch (e: Exception) {
        Log.e("Sincronizacion", "Error al enviar mensaje al reloj", e)
      }
    }
  }
}