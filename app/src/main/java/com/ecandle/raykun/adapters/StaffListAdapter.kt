package com.ecandle.raykun.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ecandle.raykun.R
import com.ecandle.raykun.models.Staff
import java.util.*

class StaffListAdapter(private val context: Context, val data: ArrayList<Staff>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.staff_list_item, parent, false)
        return MyHolder(view)
    }

    // Bind data
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        // Get current position of item in recyclerview to bind data and assign values from list
        val myHolder = holder as MyHolder
        val current = data[position]
        myHolder.staff_date_assigned.text  = current.date_assigned
        myHolder.staff_phonenumber.text = current.phonenumber
        myHolder.staff_name.text = current.name
        myHolder.staff_email.text = current.email

    }
    // return total item from List
    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(position: Int): Staff {
        return data[position]
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val staff_date_assigned: TextView = itemView.findViewById(R.id.staff_date_assigned )
        internal val staff_phonenumber: TextView = itemView.findViewById(R.id.staff_phonenumber)
        internal val staff_name: TextView = itemView.findViewById(R.id.staff_name)
        internal val staff_email: TextView = itemView.findViewById(R.id.staff_email )
    }
}
