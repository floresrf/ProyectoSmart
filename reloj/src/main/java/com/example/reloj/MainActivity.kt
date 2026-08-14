package com.example.reloj // Reemplaza por tu paquete real

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

    // Cargar la vista XML directamente
    setContentView(R.layout.activity_main)

    // Referenciar los elementos del XML por ID
    tvHoraAlarma = findViewById(R.id.tvHoraAlarma)
    tvMedicamento = findViewById(R.id.tvMedicamento)
    btnDispensarAhora = findViewById(R.id.btnDispensarAhora)
    tvEstadoAlarma = findViewById(R.id.tvEstadoAlarma)

    // Recibir los datos enviados por la pantalla previa (si existen)
    val horaRecibida = intent.getStringExtra("EXTRA_HORA")
    val medicamentoRecibido = intent.getStringExtra("EXTRA_MEDICAMENTO")

    if (!horaRecibida.isNullOrEmpty()) {
      tvHoraAlarma.text = horaRecibida
    }

    if (!medicamentoRecibido.isNullOrEmpty()) {
      tvMedicamento.text = medicamentoRecibido
    }

    // Mostrar el mensaje al presionar el botón
    btnDispensarAhora.setOnClickListener {
      Toast.makeText(this, "Activando pastillero...", Toast.LENGTH_LONG).show()
    }
  }
}
