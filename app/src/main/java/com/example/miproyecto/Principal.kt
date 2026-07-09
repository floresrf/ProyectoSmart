package com.example.miproyecto

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Principal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recibir los datos (si por alguna razón falla, por defecto será "user")
        val username = intent.getStringExtra("USERNAME") ?: "Invitado"
        val rolUsuario = intent.getStringExtra("ROLE") ?: "user"

        Toast.makeText(this, "Hola $username", Toast.LENGTH_SHORT).show()

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val btnregresar: Button = findViewById(R.id.btnRegresar)

        // Pasamos el rolUsuario al adaptador
        val adapter = ViewPagerAdapter(this, rolUsuario)
        viewPager.adapter = adapter

        // Conectar el TabLayout con el ViewPager de forma dinámica
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Le pedimos el título exacto a la lista del adaptador
            tab.text = adapter.titulos[position]
        }.attach()

        // Boton Salir
        btnregresar.setOnClickListener {
            finish()
        }
    }
}