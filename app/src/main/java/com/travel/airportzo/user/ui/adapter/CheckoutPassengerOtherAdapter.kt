package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.CheckoutPassengerotherAdapterBinding
import com.travel.airportzo.user.model.PassengerCreateData
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.fragments.CheckoutFragment.Companion.checkoutDataOther


class CheckoutPassengerOtherAdapter(
    var context: Context,
    private var checkoutDataOther: ArrayList<PassengerCreateData>,
) : RecyclerView.Adapter<CheckoutPassengerOtherAdapter.MyViewHolder>() {
    inner class MyViewHolder( var checkoutPassengerOtherAdapterBinding: CheckoutPassengerotherAdapterBinding): RecyclerView.ViewHolder(checkoutPassengerOtherAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bindItems(task: PassengerCreateData) {
            //bind the item here
            checkoutPassengerOtherAdapterBinding.checkoutPassengerName.text = task.name_view
            checkoutPassengerOtherAdapterBinding.checkoutPassengerNo.text = task.country_code+ " " +task.mobile_number
            checkoutPassengerOtherAdapterBinding.checkoutPassengerEmail.text = task.email_id
            checkoutPassengerOtherAdapterBinding.checkoutPassengerAge.text = task.age
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val checkoutPassengerOtherAdapterBinding = CheckoutPassengerotherAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(checkoutPassengerOtherAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(checkoutDataOther[position])
        holder.checkoutPassengerOtherAdapterBinding.checkoutCancel.setOnDebounceListener {
            checkoutDataOther.removeAt(position)
//            clickListener(holder)
            notifyDataSetChanged()
            Toast.makeText(
                holder.itemView.context,
                "Other Passenger detail cleared",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return checkoutDataOther.size
    }
}