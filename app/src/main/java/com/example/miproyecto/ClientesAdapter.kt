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
            .inflate(R.layout.item_cliente_row, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val paciente: JSONObject = jsonArray.getJSONObject(position)

        val id = paciente.optInt("id", 0)
        val firstName = paciente.optString("first_name", "")
        val lastName = paciente.optString("last_name", "")

        holder.tvClaveRow.text = id.toString()
        holder.tvNombreRow.text = "$firstName $lastName".trim()

        holder.itemView.setOnClickListener {
            onClienteClickListener(paciente)
        }
    }

    override fun getItemCount(): Int = jsonArray.length()

    class ClienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvClaveRow: TextView = view.findViewById(R.id.tvClaveRow)
        val tvNombreRow: TextView = view.findViewById(R.id.tvNombreRow)
    }
}