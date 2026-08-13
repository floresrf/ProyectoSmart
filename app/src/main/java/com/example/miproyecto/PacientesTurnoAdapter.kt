package com.example.miproyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class PacientesTurnoAdapter(
    private val jsonArray: JSONArray
) : RecyclerView.Adapter<PacientesTurnoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paciente_turno, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asignacion: JSONObject = jsonArray.getJSONObject(position)

        val idPatient = asignacion.optInt("id_patient", 0)
        val patientFullName = asignacion.optString("patient_full_name", "Sin nombre")

        val claveFormateada = String.format("PAC-%04d", idPatient)

        holder.tvNombre.text = patientFullName
        holder.tvClaveEdad.text = "Clave: $claveFormateada"
        holder.tvDiagnostico.text = "Paciente Asignado"
    }

    override fun getItemCount(): Int = jsonArray.length()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombrePacienteTurno)
        val tvClaveEdad: TextView = view.findViewById(R.id.tvClaveEdadTurno)
        val tvDiagnostico: TextView = view.findViewById(R.id.tvDiagnosticoTurno)
    }
}