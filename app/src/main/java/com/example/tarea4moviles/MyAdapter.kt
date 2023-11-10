package com.example.tarea4moviles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KFunction1

class MyAdapter(private val data: MutableList<Product>, private val deleteCallback: (Int) -> Unit, private val editCallBack: KFunction1<Product, Unit>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val text = data[position]
        holder.textViewName.text = text.name
        holder.textViewDescription.text=text.description

        // Configura un OnClickListener para el botón
        holder.btnEdit.setOnClickListener {
            //Toast.makeText(context, data[position].id, Toast.LENGTH_SHORT).show()
            editCallBack(text)
        }

        // Configura un OnClickListener para el segundo botón (button2)
        holder.btnDelete.setOnClickListener {
            deleteCallback(text.id)
            // Abre la segunda actividad y envía el texto como parámetro
            /*val intent = Intent(context, SecondActivity::class.java)
            intent.putExtra("textParameter", text)
            context.startActivity(intent)*/
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

