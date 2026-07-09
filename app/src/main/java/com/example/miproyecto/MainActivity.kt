package com.example.miproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var ctUsuario: EditText
    private lateinit var ctContraseña: EditText
    private lateinit var btnAcceder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ctUsuario = findViewById(R.id.ctUsuario)
        ctContraseña = findViewById(R.id.ctContraseña)
        btnAcceder = findViewById(R.id.btnAcceder)

        btnAcceder.setOnClickListener {
            verificarCredencialesEnServidor()
        }
    }

    private fun verificarCredencialesEnServidor() {
        val urlLogin = "https://web-api-movil-rene.onrender.com/api/usuarios/login"

        val userText = ctUsuario.text.toString().trim()
        val passText = ctContraseña.text.toString().trim()

        if (userText.isEmpty() || passText.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val jsonBody = JSONObject().apply {
            put("username", userText)
            put("password", passText)
        }

        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, urlLogin, jsonBody,
            { response ->
                // Éxito: El servidor regresó Status 200 (Match perfecto en Neon)
                val mensaje = response.getString("mensaje")
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

                val intento = Intent(this, Principal::class.java)
                startActivity(intento)
                finish()
            },
            { error ->
                // Errores controlados de Express (401 Contraseña mal, 404 No existe, 500 Caída)
                val responseNetwork = error.networkResponse
                if (responseNetwork != null && responseNetwork.data != null) {
                    try {
                        val errorJson = JSONObject(String(responseNetwork.data))
                        val mensajeError = errorJson.getString("mensaje")
                        Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error al validar credenciales", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Sin conexión con el servidor", Toast.LENGTH_SHORT).show()
                }
            }
        )

        queue.add(jsonObjectRequest)
    }
}