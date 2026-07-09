package com.example.miproyecto

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val rolUsuario: String
) : FragmentStateAdapter(fragmentActivity) {

    // Listas para manejar individualmente cada pestaña y su título
    private val fragmentos = mutableListOf<Fragment>()
    val titulos = mutableListOf<String>() // Público para que Principal lo pueda leer

    init {
        // Pestañas base (Todos los usuarios las ven)
        agregarPestaña(Fragmento1(), "Inicio")
        agregarPestaña(Fragmento3(), "Compras")

        // Pestañas condicionales (Control individual por rol)
        if (rolUsuario == "admin") {
            agregarPestaña(Fragmento2(), "Clientes")

        }
    }

    // Función auxiliar para mantener el código limpio
    private fun agregarPestaña(fragmento: Fragment, titulo: String) {
        fragmentos.add(fragmento)
        titulos.add(titulo)
    }

    // El tamaño ahora se calcula automáticamente basándose en la lista
    override fun getItemCount(): Int = fragmentos.size

    // Devuelve el fragmento exacto de la lista
    override fun createFragment(position: Int): Fragment = fragmentos[position]
}