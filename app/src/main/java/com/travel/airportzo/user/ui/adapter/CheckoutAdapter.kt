package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.CheckoutAdapterBinding
import com.travel.airportzo.user.model.PassengerCreateData
import com.travel.airportzo.user.utils.setOnDebounceListener

class CheckoutAdapter(var context: Context, private var checkoutData: ArrayList<PassengerCreateData>, var clickListener:(CheckoutAdapter.MyViewHolder)-> Unit) :
    RecyclerView.Adapter<CheckoutAdapter.MyViewHolder>() {
    inner class MyViewHolder(val checkoutAdapterBinding: CheckoutAdapterBinding) :
        RecyclerView.ViewHolder(checkoutAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bindItems(task: PassengerCreateData) {
            //bind the item here
            checkoutAdapterBinding.checkoutPassengerName.text = task.name_view
            checkoutAdapterBinding.checkoutPassengerNo.text = task.country_code+ " " +task.mobile_number
            checkoutAdapterBinding.checkoutPassengerEmail.text = task.email_id
            checkoutAdapterBinding.checkoutPassengerAge.text = task.age


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val checkoutAdapterBinding = CheckoutAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(checkoutAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(checkoutData[position])

        holder.checkoutAdapterBinding.checkoutCancel.setOnDebounceListener {
            clickListener(holder)
            if (checkoutData.size == 0) {
                Toast.makeText(context, "Required Contact Passenger", Toast.LENGTH_SHORT).show()
            } else {
                checkoutData.removeAt(position)
                notifyDataSetChanged()
                Toast.makeText(
                    holder.itemView.context,
                    "Contact Passenger detail cleared",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int {
       return checkoutData.size



    }
}