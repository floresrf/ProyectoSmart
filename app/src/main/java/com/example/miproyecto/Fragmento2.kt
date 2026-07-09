package com.example.miproyecto

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.Calendar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Fragmento2 : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private val URL_API = "https://web-api-movil-rene.onrender.com/api/clientes"

    private lateinit var etClave: EditText
    private lateinit var etNombre: EditText
    private lateinit var etEdad: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var btnNuevo: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private lateinit var rvClientes: RecyclerView

    private var existeCliente: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragmento2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etClave = view.findViewById(R.id.etClave)
        etNombre = view.findViewById(R.id.etNombre)
        etEdad = view.findViewById(R.id.etEdad)
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento)
        btnNuevo = view.findViewById(R.id.btnNuevo)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnEliminar = view.findViewById(R.id.btnEliminar)
        rvClientes = view.findViewById(R.id.rvClientes)

        rvClientes.layoutManager = LinearLayoutManager(requireContext())

        // Cargar el Grid desde Render inmediatamente al abrir la pantalla
        cargarGridClientes()

        // Buscar clave automáticamente (Al perder el foco)
        etClave.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val clave = etClave.text.toString().trim()
                if (clave.isNotEmpty()) {
                    buscarClientePorClave(clave)
                }
            }
        }

        // ==========================================
        // EVENTO: Mostrar Calendario al dar clic
        // ==========================================
        etFechaNacimiento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val añoActual = calendar.get(Calendar.YEAR)
            val mesActual = calendar.get(Calendar.MONTH)
            val díaActual = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, añoSeleccionado, mesSeleccionado, díaSeleccionado ->
                    // Formateamos mes y día con ceros a la izquierda si son menores a 10
                    val mesFormateado = String.format("%02d", mesSeleccionado + 1)
                    val díaFormateado = String.format("%02d", díaSeleccionado)

                    // Setea el texto en el formato visual AAAA/MM/DD
                    etFechaNacimiento.setText("$añoSeleccionado/$mesFormateado/$díaFormateado")
                },
                añoActual,
                mesActual,
                díaActual
            )
            datePicker.show()
        }

        // Nuevo (Limpiar pantalla)
        btnNuevo.setOnClickListener {
            limpiarPantalla()
        }

        // Guardar (Inserta con POST o actualiza con PUT en la nube)
        btnGuardar.setOnClickListener {
            ejecutarGuardarOActualizar()
        }

        // Eliminar (Con cuadro de diálogo de confirmación)
        btnEliminar.setOnClickListener {
            val clave = etClave.text.toString().trim()
            if (clave.isNotEmpty()) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Confirmar eliminación")
                    setMessage("¿Estás seguro de que deseas eliminar permanentemente al cliente con clave $clave?")
                    setPositiveButton("Sí, eliminar") { _, _ ->
                        eliminarClienteEnLaApi(clave)
                    }
                    setNegativeButton("Cancelar", null)
                    show()
                }
            } else {
                Toast.makeText(requireContext(), "Escribe una clave para eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun limpiarPantalla() {
        etClave.setText("")
        etNombre.setText("")
        etEdad.setText("")
        etFechaNacimiento.setText("")
        etClave.isEnabled = true
        existeCliente = false
    }

    // =======================================================
    // CONEXIONES DE RED USANDO VOLLEY HACIA LA NUBE (RENDER)
    // =======================================================

    private fun buscarClientePorClave(clave: String) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "$URL_API/$clave"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val existe = response.getBoolean("existe")
                if (existe) {
                    existeCliente = true
                    val cliente = response.getJSONObject("cliente")
                    etNombre.setText(cliente.getString("nombre"))
                    etEdad.setText(cliente.getInt("edad").toString())

                    // Convierte AAAA-MM-DD de la API a AAAA/MM/DD para tu formulario
                    val fechaApi = cliente.getString("fecha_nacimiento").split("T")[0]
                    etFechaNacimiento.setText(fechaApi.replace("-", "/"))

                    etClave.isEnabled = false // Congela el ID durante la edición
                    Toast.makeText(requireContext(), "Cliente cargado", Toast.LENGTH_SHORT).show()
                } else {
                    existeCliente = false
                }
            },
            { Toast.makeText(requireContext(), "Buscando...", Toast.LENGTH_SHORT).show() }
        )
        queue.add(jsonObjectRequest)
    }

    private fun ejecutarGuardarOActualizar() {
        val clave = etClave.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val edadStr = etEdad.text.toString().trim()
        val fechaFormulario = etFechaNacimiento.text.toString().trim()

        if (clave.isEmpty() || nombre.isEmpty() || edadStr.isEmpty() || fechaFormulario.length < 10) {
            Toast.makeText(requireContext(), "Llena todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        // TRANSFORMACIÓN: De "AAAA/MM/DD" a "AAAA-MM-DD" para enviarlo en formato estándar JSON a Render
        val fechaParaJson = fechaFormulario.replace("/", "-")

        val jsonBody = JSONObject().apply {
            put("clave", clave)
            put("nombre", nombre)
            put("edad", edadStr.toInt())
            put("fecha_nacimiento", fechaParaJson)
        }

        val queue = Volley.newRequestQueue(requireContext())
        val metodo = if (existeCliente) Request.Method.PUT else Request.Method.POST
        val urlFinal = if (existeCliente) "$URL_API/$clave" else URL_API

        val jsonObjectRequest = JsonObjectRequest(metodo, urlFinal, jsonBody,
            { response ->
                Toast.makeText(requireContext(), response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                limpiarPantalla()
                cargarGridClientes() // Refresca automáticamente el Grid inferior
            },
            { Toast.makeText(requireContext(), "Error al guardar registro", Toast.LENGTH_SHORT).show() }
        )
        queue.add(jsonObjectRequest)
    }

    private fun eliminarClienteEnLaApi(clave: String) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "$URL_API/$clave"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.DELETE, url, null,
            { response ->
                Toast.makeText(requireContext(), response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                limpiarPantalla()
                cargarGridClientes()
            },
            { Toast.makeText(requireContext(), "Error al eliminar de la BD", Toast.LENGTH_SHORT).show() }
        )
        queue.add(jsonObjectRequest)
    }

    private fun cargarGridClientes() {
        val queue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, URL_API, null,
            { response ->

                val adapter = ClientesAdapter(response) { clienteSeleccionado ->

                    etClave.setText(clienteSeleccionado.getString("clave"))
                    etNombre.setText(clienteSeleccionado.getString("nombre"))

                    if (clienteSeleccionado.has("edad")) {
                        etEdad.setText(clienteSeleccionado.getInt("edad").toString())
                    }

                    if (clienteSeleccionado.has("fecha_nacimiento")) {
                        val fechaApi = clienteSeleccionado.getString("fecha_nacimiento").split("T")[0]
                        etFechaNacimiento.setText(fechaApi.replace("-", "/"))
                    }
                    existeCliente = true
                    etClave.isEnabled = false
                }

                rvClientes.adapter = adapter
            },
            { error ->
                Toast.makeText(requireContext(), "Error al cargar catálogo en red", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(jsonArrayRequest)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragmento2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}