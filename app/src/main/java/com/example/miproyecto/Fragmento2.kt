package com.example.miproyecto

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

class Fragmento2 : Fragment() {

    private val URL_API = "https://api-farmaalert-module.onrender.com/api/pacientes"

    private lateinit var etClave: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etEdad: EditText
    private lateinit var etEnfermedad: EditText
    private lateinit var btnNuevo: MaterialButton
    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnEliminar: MaterialButton
    private lateinit var rvClientes: RecyclerView

    private var existeCliente: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fragmento2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etClave = view.findViewById(R.id.etClave)
        etNombre = view.findViewById(R.id.etNombre)
        etApellido = view.findViewById(R.id.etApellido)
        etEdad = view.findViewById(R.id.etEdad)
        etEnfermedad = view.findViewById(R.id.etEnfermedad)
        btnNuevo = view.findViewById(R.id.btnNuevo)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnEliminar = view.findViewById(R.id.btnEliminar)
        rvClientes = view.findViewById(R.id.rvClientes)

        // Asignar LayoutManager explícito al RecyclerView
        rvClientes.layoutManager = LinearLayoutManager(requireContext())

        // Bloquear clave para que el usuario no pueda escribir manualmente
        etClave.isEnabled = false
        etClave.isFocusable = false

        // Cargar lista desde Render inmediatamente al abrir la pestaña
        cargarGridPacientes()

        btnNuevo.setOnClickListener {
            limpiarPantalla()
        }

        btnGuardar.setOnClickListener {
            ejecutarGuardarOActualizar()
        }

        btnEliminar.setOnClickListener {
            val clave = etClave.text.toString().trim()
            if (clave.isNotEmpty()) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Confirmar eliminación")
                    setMessage("¿Deseas eliminar permanentemente al paciente #$clave?")
                    setPositiveButton("Eliminar") { _, _ ->
                        eliminarPacienteEnLaApi(clave)
                    }
                    setNegativeButton("Cancelar", null)
                    show()
                }
            } else {
                Toast.makeText(requireContext(), "Selecciona un paciente de la lista para eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun limpiarPantalla() {
        etClave.setText("")
        etNombre.setText("")
        etApellido.setText("")
        etEdad.setText("")
        etEnfermedad.setText("")
        existeCliente = false
    }

    private fun cargarGridPacientes() {
        val queue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, URL_API, null,
            { response ->
                val adapter = ClientesAdapter(response) { pacienteSeleccionado ->
                    val id = pacienteSeleccionado.optInt("id")
                    val firstName = pacienteSeleccionado.optString("first_name", "")
                    val lastName = pacienteSeleccionado.optString("last_name", "")
                    val age = pacienteSeleccionado.optInt("age")
                    val diagnosis = pacienteSeleccionado.optString("diagnosis", "")

                    // Llenar formulario arriba al tocar un elemento de la lista
                    etClave.setText(id.toString())
                    etNombre.setText(firstName)
                    etApellido.setText(lastName)
                    etEdad.setText(if (age > 0) age.toString() else "")
                    etEnfermedad.setText(diagnosis)

                    existeCliente = true
                }

                rvClientes.adapter = adapter
            },
            { error ->
                Toast.makeText(requireContext(), "Error al conectar con Render: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(jsonArrayRequest)
    }

    private fun ejecutarGuardarOActualizar() {
        val clave = etClave.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val edadStr = etEdad.text.toString().trim()
        val enfermedad = etEnfermedad.text.toString().trim()

        if (nombre.isEmpty() || apellido.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor llena Nombre, Apellido y Edad", Toast.LENGTH_SHORT).show()
            return
        }

        val jsonBody = JSONObject().apply {
            put("first_name", nombre)
            put("last_name", apellido)
            put("age", edadStr.toIntOrNull() ?: 0)
            put("diagnosis", enfermedad)
            put("room", "101")
        }

        val queue = Volley.newRequestQueue(requireContext())
        val metodo = if (existeCliente) Request.Method.PUT else Request.Method.POST
        val urlFinal = if (existeCliente) "$URL_API/$clave" else URL_API

        val jsonObjectRequest = JsonObjectRequest(metodo, urlFinal, jsonBody,
            { response ->
                val msj = response.optString("mensaje", "Operación exitosa")
                Toast.makeText(requireContext(), msj, Toast.LENGTH_SHORT).show()
                limpiarPantalla()
                cargarGridPacientes()
            },
            {
                Toast.makeText(requireContext(), "Error al guardar en la nube", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(jsonObjectRequest)
    }

    private fun eliminarPacienteEnLaApi(clave: String) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "$URL_API/$clave"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.DELETE, url, null,
            { response ->
                val msj = response.optString("mensaje", "Paciente eliminado")
                Toast.makeText(requireContext(), msj, Toast.LENGTH_SHORT).show()
                limpiarPantalla()
                cargarGridPacientes()
            },
            {
                Toast.makeText(requireContext(), "Error al eliminar el paciente", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(jsonObjectRequest)
    }
}