package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.PackageListAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener


class PackageListAdapter(
    var context: Context,
    var dataList: ArrayList<ServiceTicketData.Service_array>,
    val onClick: (ServiceTicketData.Service_array) -> Unit,
    val onRemove: (String,ServiceTicketData.Service_array) -> Unit,
) :
    RecyclerView.Adapter<PackageListAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val packageListAdapterBinding =
            PackageListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(packageListAdapterBinding)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(dataList[position])
        Log.d("dataList", "onBindViewHolder:${dataList.toString()} ")
    }

    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()

    //the class is holding the list view
    inner class MyViewHolder(private val packageListAdapterBinding: PackageListAdapterBinding) :
        RecyclerView.ViewHolder(packageListAdapterBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(task: ServiceTicketData.Service_array) {



            //bind the item here
            packageListAdapterBinding.cCardPackageName.text = task.service_name
            packageListAdapterBinding.cCardAdultPrice.text = "${context.getString(R.string.indianRupee)} ${task.price_adult} for 1st adult and ${task.additional_price_adult} for each additional adult(s) | ${context.getString(R.string.indianRupee)} ${task.price_children} for 1st child and ${task.additional_price_children} for each additional child(ren)"
            packageListAdapterBinding.cCardChildPrice.text = task.price_children
            packageListAdapterBinding.aCardPackageTotalPrice.text="${context.getString(R.string.indianRupee)} ${task.totalAmount.toString()}"
            Log.d("PackageAmount", "total amount: ${task.totalAmount.toString()} ")

            if (task.isClicked) {
                /** brand color*/
                val colorValue = SavedSharedPreference.getCustomColor(itemView.context).secondary_colour
                val shapeDrawable = GradientDrawable()
                shapeDrawable.shape = GradientDrawable.RECTANGLE
                shapeDrawable.setStroke(1, Color.parseColor(colorValue))
                packageListAdapterBinding.cContactBtn.background = shapeDrawable

                packageListAdapterBinding.bBookService.visibility = View.GONE
                packageListAdapterBinding.cContactBtn.visibility = View.VISIBLE
            } else {
                /** brand color*/
                val colorValue = SavedSharedPreference.getCustomColor(itemView.context).brand_colour
                packageListAdapterBinding.bBookService.setBackgroundColor(Color.parseColor(colorValue))

                packageListAdapterBinding.bBookService.visibility = View.VISIBLE
                packageListAdapterBinding.cContactBtn.visibility = View.GONE
            }

            packageListAdapterBinding.bBookService.setOnDebounceListener {
                task.isClicked = true
                onClick(task)
                packageListAdapterBinding.bBookService.visibility = View.GONE
                packageListAdapterBinding.cContactBtn.visibility = View.VISIBLE

            }

            packageListAdapterBinding.cContactBtn.setOnDebounceListener {
                task.isClicked = false
                onRemove(task.service_token,task)
                packageListAdapterBinding.bBookService.visibility = View.VISIBLE
                /** brand color*/
                val colorValue = SavedSharedPreference.getCustomColor(itemView.context).brand_colour
                packageListAdapterBinding.bBookService.setBackgroundColor(Color.parseColor(colorValue))

                packageListAdapterBinding.cContactBtn.visibility = View.GONE
            }

            packageListAdapterBinding.cCardServiceNameRecycler.adapter =
                PackageServiceIncludedAdapter(
                    context, dataList[bindingAdapterPosition].business_names
                )
            packageListAdapterBinding.cCardServiceFeatureRecycler.adapter = PackageFeatureAdapter(
                context, dataList[bindingAdapterPosition].service_features
            )



        }
    }
}