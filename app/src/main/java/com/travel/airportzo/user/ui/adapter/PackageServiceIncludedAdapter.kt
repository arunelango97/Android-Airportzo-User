package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.PackageServiceIncludedAdapterBinding
import com.travel.airportzo.user.savedpreference.SavedSharedPreference


class PackageServiceIncludedAdapter(var context: Context, var data: ArrayList<String>) :
    RecyclerView.Adapter<PackageServiceIncludedAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val packageServiceIncludedAdapter = PackageServiceIncludedAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(packageServiceIncludedAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return data.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(data[position])
    }

    //the class is holding the list view
    inner class MyViewHolder(private val packageServiceIncludedAdapterBinding: PackageServiceIncludedAdapterBinding) : RecyclerView.ViewHolder(packageServiceIncludedAdapterBinding.root) {

        fun bindItems(task: String) {
            //bind the item here
           /* val drawable = packageServiceIncludedAdapterBinding.image.drawable
            val colorValue = SavedSharedPreference.getCustomColor(itemView.context).brand_colour
            val drawableCopy = drawable?.mutate()
            if (drawableCopy != null) {
                DrawableCompat.setTint(drawableCopy, Color.parseColor(colorValue))
                packageServiceIncludedAdapterBinding.image.setImageDrawable(drawableCopy)
            }*/
//            packageServiceIncludedAdapterBinding.image.setColorFilter(Color.parseColor(colorValue))

            packageServiceIncludedAdapterBinding.serviceText.text = task.trim()



        }

    }

}