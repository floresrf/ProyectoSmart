package com.example.reloj // Reemplaza por el paquete exacto de tu proyecto

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

  private lateinit var tvHoraAlarma: TextView
  private lateinit var tvMedicamento: TextView
  private lateinit var btnDispensarAhora: MaterialButton
  private lateinit var tvEstadoAlarma: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // 1. Vincular componentes del XML
    tvHoraAlarma = findViewById(R.id.tvHoraAlarma)
    tvMedicamento = findViewById(R.id.tvMedicamento)
    btnDispensarAhora = findViewById(R.id.btnDispensarAhora)
    tvEstadoAlarma = findViewById(R.id.tvEstadoAlarma)

    // 2. Obtener datos enviados desde la alarma / Intent
    val horaRecibida = intent.getStringExtra("EXTRA_HORA")
    val medicamentoRecibido = intent.getStringExtra("EXTRA_MEDICAMENTO")

    // 3. Actualizar la vista con la alarma configurada
    if (!horaRecibida.isNullOrEmpty()) {
      tvHoraAlarma.text = horaRecibida
    }

    if (!medicamentoRecibido.isNullOrEmpty()) {
      tvMedicamento.text = medicamentoRecibido
    }

    // 4. Acción del botón DISPENSAR AHORA
    btnDispensarAhora.setOnClickListener {
      Toast.makeText(this, "Activando pastillero...", Toast.LENGTH_LONG).show()
    }
  }
}
