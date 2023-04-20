package com.example.brewbuddyproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MyRecycleAdapter(val breweryLocations: ArrayList<Brewery>): RecyclerView.Adapter<MyRecycleAdapter.MyViewHolder>() {

    var counter = 1
    private val TAG = "MyRecycleAdapter"

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val brewName = itemView.findViewById<TextView>(R.id.brew_name)
        val brewStreet = itemView.findViewById<TextView>(R.id.brew_street)
        val brewCity = itemView.findViewById<TextView>(R.id.brew_city_state)
        val brewPhone = itemView.findViewById<TextView>(R.id.brew_phone)
        val brewWebsite = itemView.findViewById<TextView>(R.id.brew_website)

        init {
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "You clicked on ${breweryLocations[adapterPosition].name}", Toast.LENGTH_SHORT).show()
            }


            itemView.setOnLongClickListener(OnLongClickListener {
                Toast.makeText(itemView.context, "You LONG clicked on ${breweryLocations[adapterPosition].name}", Toast.LENGTH_SHORT).show()
                true // returning true instead of false, works for me
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item, parent, false)
        Log.d(TAG, "onCreateViewHolder: ${counter++}")
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return breweryLocations.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.brewName.text = breweryLocations[position].name
        holder.brewStreet.text = breweryLocations[position].street
        //why am I getting city and state but not zip from this??
        holder.brewCity.text = "${breweryLocations[position].city}, ${breweryLocations[position].state} ${breweryLocations[position].zip}"
        holder.brewPhone.text = breweryLocations[position].phone
        holder.brewWebsite.text = breweryLocations[position].website_url
    }


}