package com.example.reloj

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // 1. Inflar la vista XML
    setContentView(R.layout.activity_main)

    // 2. Recibir extras si existen
    val hora = intent.getStringExtra("EXTRA_HORA") ?: "07:30 AM"
    val medicamento = intent.getStringExtra("EXTRA_MEDICAMENTO") ?: "Paracetamol - 500mg"

    // 3. Vincular vistas con el XML
    val tvHora = findViewById<TextView>(R.id.tvHoraAlarma)
    val tvMed = findViewById<TextView>(R.id.tvMedicamento)
    val btnDispensar = findViewById<Button>(R.id.btnDispensarAhora)

    // 4. Asignar datos a la interfaz
    tvHora.text = hora
    tvMed.text = medicamento

    // 5. Listener para el botón
    btnDispensar.setOnClickListener {
      Toast.makeText(this, "Activando pastillero...", Toast.LENGTH_LONG).show()
    }
  }
}