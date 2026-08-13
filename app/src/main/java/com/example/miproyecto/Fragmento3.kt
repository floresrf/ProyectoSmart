package com.example.miproyecto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton

class Fragmento3 : Fragment() {

    // URL de asignaciones en Render
    private val URL_ASIGNACIONES = "https://api-farmaalert-module.onrender.com/api/asignaciones"

    private lateinit var rvPacientesTurno: RecyclerView
    private lateinit var btnRegresarMenu: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fragmento3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPacientesTurno = view.findViewById(R.id.rvPacientesTurno)
        btnRegresarMenu = view.findViewById(R.id.btnRegresarMenu)

        rvPacientesTurno.layoutManager = LinearLayoutManager(requireContext())

        // 🎯 Cargar únicamente los pacientes del Enfermero ID: 1
        cargarPacientesDeEnfermero(1)

        btnRegresarMenu.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun cargarPacientesDeEnfermero(idEnfermeroTarget: Int) {
        val queue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, URL_ASIGNACIONES, null,
            { response ->
                val listaFiltrada = org.json.JSONArray()

                // Filtrar el JSON en el cliente para obtener solo las asignaciones de id_nurse = 1
                for (i in 0 until response.length()) {
                    val asignacion = response.getJSONObject(i)
                    if (asignacion.optInt("id_nurse") == idEnfermeroTarget) {
                        listaFiltrada.put(asignacion)
                    }
                }

                if (listaFiltrada.length() == 0) {
                    Toast.makeText(requireContext(), "No hay pacientes asignados al enfermero #$idEnfermeroTarget", Toast.LENGTH_SHORT).show()
                }

                // Cargar adaptador con la lista filtrada
                val adapter = PacientesTurnoAdapter(listaFiltrada)
                rvPacientesTurno.adapter = adapter
            },
            { error ->
                Toast.makeText(requireContext(), "Error al cargar la lista de asignaciones", Toast.LENGTH_SHORT).show()
            }
        )

        // Configuración de Timeout por si Render está reanudando el servicio
        jsonArrayRequest.retryPolicy = com.android.volley.DefaultRetryPolicy(
            30000, 2, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        queue.add(jsonArrayRequest)
    }
}