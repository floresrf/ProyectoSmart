package com.example.reloj

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      setShowWhenLocked(true)
      setTurnScreenOn(true)
      val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
      keyguardManager.requestDismissKeyguard(this, null)
    } else {
      window.addFlags(
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
      )
    }

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
    findViewById<TextView>(R.id.tvEstadoAlarma).text = "Alarma Recibida"

    // 5. Listener para el botón
    btnDispensar.setOnClickListener {
      Toast.makeText(this, "Activando pastillero...", Toast.LENGTH_LONG).show()
      finish()
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)

    val hora = intent.getStringExtra("EXTRA_HORA") ?: ""
    val medicamento = intent.getStringExtra("EXTRA_MEDICAMENTO") ?: ""

    findViewById<TextView>(R.id.tvHoraAlarma).text = hora
    findViewById<TextView>(R.id.tvMedicamento).text = medicamento
  }
}