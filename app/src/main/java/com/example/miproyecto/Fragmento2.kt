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

    // CORRECCIÓN CLAVE DE IDs
    etClave = view.findViewById(R.id.etClave)
    etNombre = view.findViewById(R.id.etNombre)
    // Apellidos en el XML apuntaba a etClave/etApellido, aquí enlazamos el EditText interno del Apellido:
    etApellido = view.findViewById(R.id.etClave) // Si tu EditText de apellido tiene id etApellido cámbialo aquí
    etEdad = view.findViewById(R.id.etEdad)
    etEnfermedad = view.findViewById(R.id.etEnfermedad)

    btnNuevo = view.findViewById(R.id.btnNuevo)
    btnGuardar = view.findViewById(R.id.btnGuardar)
    btnEliminar = view.findViewById(R.id.btnEliminar)
    rvClientes = view.findViewById(R.id.rvClientes)

    rvClientes.layoutManager = LinearLayoutManager(requireContext())

    etClave.isEnabled = false
    etClave.isFocusable = false

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
    val safeContext = context?.applicationContext ?: return
    val queue = Volley.newRequestQueue(safeContext)

    val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, URL_API, null,
      { response ->
        if (!isAdded) return@JsonArrayRequest

        val adapter = ClientesAdapter(response) { pacienteSeleccionado ->
          val id = pacienteSeleccionado.optInt("id")
          val firstName = pacienteSeleccionado.optString("first_name", "")
          val lastName = pacienteSeleccionado.optString("last_name", "")
          val age = pacienteSeleccionado.optInt("age")
          val diagnosis = pacienteSeleccionado.optString("diagnosis", "")

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
        if (isAdded) {
          Toast.makeText(context, "Error al conectar con Render: ${error.message}", Toast.LENGTH_SHORT).show()
        }
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

    val safeContext = context?.applicationContext ?: return
    val queue = Volley.newRequestQueue(safeContext)
    val metodo = if (existeCliente) Request.Method.PUT else Request.Method.POST
    val urlFinal = if (existeCliente) "$URL_API/$clave" else URL_API

    val jsonObjectRequest = JsonObjectRequest(metodo, urlFinal, jsonBody,
      { response ->
        if (!isAdded) return@JsonObjectRequest
        val msj = response.optString("mensaje", "Operación exitosa")
        Toast.makeText(context, msj, Toast.LENGTH_SHORT).show()
        limpiarPantalla()
        cargarGridPacientes()
      },
      {
        if (isAdded) {
          Toast.makeText(context, "Error al guardar en la nube", Toast.LENGTH_SHORT).show()
        }
      }
    )
    queue.add(jsonObjectRequest)
  }

  private fun eliminarPacienteEnLaApi(clave: String) {
    val safeContext = context?.applicationContext ?: return
    val queue = Volley.newRequestQueue(safeContext)
    val url = "$URL_API/$clave"

    val jsonObjectRequest = JsonObjectRequest(Request.Method.DELETE, url, null,
      { response ->
        if (!isAdded) return@JsonObjectRequest
        val msj = response.optString("mensaje", "Paciente eliminado")
        Toast.makeText(context, msj, Toast.LENGTH_SHORT).show()
        limpiarPantalla()
        cargarGridPacientes()
      },
      {
        if (isAdded) {
          Toast.makeText(context, "Error al eliminar el paciente", Toast.LENGTH_SHORT).show()
        }
      }
    )
    queue.add(jsonObjectRequest)
  }
}