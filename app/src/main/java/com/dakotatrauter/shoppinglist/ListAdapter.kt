package com.dakotatrauter.shoppinglist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ListAdapter(private val context: Context, val items: ArrayList<Item>):
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    // Sets the number of items in the recycler view
    override fun getItemCount(): Int {
        return items.size
    }

    // Creating the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false)
        return ViewHolder(view)
    }

    // Binding data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemName.text = item.name
        holder.itemStyle.text = item.styleNumber
        holder.itemPrice.text = "$" + item.price

        val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)
        Picasso.with(context).load(item.imageUrl).into(imageView)

        holder.deleteButton.setOnClickListener() {
            removeItem(holder.adapterPosition)
        }
    }

    private fun removeItem(position: Int){
        ItemList.totalPrice -= items[position].price.toDouble()
        items.removeAt(position)
        notifyItemRemoved(position)
        ItemList.reloadNeeded = true
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var itemName = view.findViewById<TextView>(R.id.textView_itemName)!!
        var itemStyle = view.findViewById<TextView>(R.id.textView_itemStyle)!!
        var itemPrice = view.findViewById<TextView>(R.id.textView_itemPrice)!!
        val deleteButton = view.findViewById<Button>(R.id.button_deleteItem)!!
    }
}