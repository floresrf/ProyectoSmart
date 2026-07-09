package com.example.miproyecto

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity):
    FragmentStateAdapter(fragmentActivity){

    //numero de pestañas
    override fun getItemCount(): Int = 3

    //dependiendo de la posicion delvuelve un valor
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> Fragmento1()
            1-> Fragmento2()
            2-> Fragmento3()
            else-> Fragmento1()
        }
    }
}