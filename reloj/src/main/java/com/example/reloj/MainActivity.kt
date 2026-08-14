package com.example.reloj

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.*
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Text

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val hora = intent.getStringExtra("EXTRA_HORA") ?: "07:30 AM"
    val medicamento = intent.getStringExtra("EXTRA_MEDICAMENTO") ?: "Paracetamol - 500mg"

    setContent {
      PantallaReloj(
        hora = hora,
        medicamento = medicamento,
        onDispensar = {
          Toast.makeText(this, "Activando pastillero...", Toast.LENGTH_LONG).show()
        }
      )
    }
  }
}

@Composable
fun PantallaReloj(hora: String, medicamento: String, onDispensar: () -> Unit) {
  ScalingLazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF254445)),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    item {
      // Hora
      Text(
        text = hora,
        color = Color(0xFFBFA067),
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
      )
    }
    item {
      // Medicamento
      Text(
        text = medicamento,
        color = Color.White,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 4.dp)
      )
    }
    item {
      // Botón Dispensar
      Button(
        onClick = onDispensar,
        // Cambiado backgroundColor por containerColor
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBFA067)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .width(120.dp)
          .height(64.dp)
          .padding(vertical = 4.dp)
      ) {
        Text(
          text = "DISPENSAR\nAHORA",
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
      }
    }
    item {
      // Estado
      Text(
        text = "Alarma Activa",
        color = Color.White.copy(alpha = 0.5f),
        fontSize = 11.sp,
        modifier = Modifier.padding(top = 4.dp)
      )
    }
  }
}
