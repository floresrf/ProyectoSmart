package com.example.miproyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class ClientesAdapter(
    private val jsonArray: JSONArray,
    private val onClienteClickListener: (JSONObject) -> Unit
) : RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paciente_turno, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente: JSONObject = jsonArray.optJSONObject(position) ?: return

        // Lectura ultra segura de la API
        val id = cliente.optInt("id", 0)
        val firstName = cliente.optString("first_name", "")
        val lastName = cliente.optString("last_name", "")
        val age = cliente.optInt("age", 0)
        val diagnosis = cliente.optString("diagnosis", "Sin diagnóstico")
        val room = cliente.optString("room", "N/A")

        // Formato para los TextViews según la estructura del XML
        val nombreCompleto = "$firstName $lastName".trim()
        val infoSecundaria = "ID: $id | Edad: ${if (age > 0) age else "-"} | Hab: $room"

        holder.tvNombre.text = if (nombreCompleto.isNotEmpty()) nombreCompleto else "Paciente sin nombre"
        holder.tvClaveEdad.text = infoSecundaria
        holder.tvDiagnostico.text = diagnosis

        holder.itemView.setOnClickListener {
            onClienteClickListener(cliente)
        }
    }

    override fun getItemCount(): Int = jsonArray.length()

    class ClienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombrePacienteTurno)
        val tvClaveEdad: TextView = view.findViewById(R.id.tvClaveEdadTurno)
        val tvDiagnostico: TextView = view.findViewById(R.id.tvDiagnosticoTurno)
    }
}