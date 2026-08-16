package com.example.miproyecto

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val rolUsuario: String
) : FragmentStateAdapter(fragmentActivity) {

    val titulos = if (rolUsuario == "admin") {
        listOf("Inicio", "Compras", "Clientes")
    } else {
        listOf("Inicio", "Compras")
    }

    override fun getItemCount(): Int = titulos.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Fragmento1()
            1 -> Fragmento3()
            2 -> if (rolUsuario == "admin") Fragmento2() else Fragmento3()
            else -> Fragmento1()
        }
    }
}
