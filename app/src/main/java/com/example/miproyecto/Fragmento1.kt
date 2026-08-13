package com.example.miproyecto // Ajusta según el paquete de tu proyecto

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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
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
  private var hora24 = 7
  private var minuto = 30

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

    layoutHoraSelector.setOnClickListener { abrirTimePicker() }
    configurarDiasSemanales(view)

    btnGuardarAlarma.setOnClickListener { programarYEnviarAlarma() }

    return view
  }

  private fun abrirTimePicker() {
    val timePicker = TimePickerDialog(
      requireContext(),
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

    mapaDias.forEach { (viewId, calendarDay) ->
      val tvDia = view.findViewById<TextView>(viewId)
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

    // Abrir la pantalla del reloj (MainActivity) enviando los datos
    val intent = Intent(requireContext(), MainActivity::class.java).apply {
      putExtra("EXTRA_HORA", horaTexto)
      putExtra("EXTRA_MEDICAMENTO", etiqueta)
    }
    startActivity(intent)
  }
}
