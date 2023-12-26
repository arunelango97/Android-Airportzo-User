package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.CheckoutServiceAdapterBinding
import com.travel.airportzo.user.model.PassengerCreateData
import com.travel.airportzo.user.utils.setOnDebounceListener

class CheckoutServiceAdapter(
    var context: Context, private var checkoutDataService: ArrayList<PassengerCreateData>, private var clickListener: (MyViewHolder)->Unit) :
    RecyclerView.Adapter<CheckoutServiceAdapter.MyViewHolder>() {
    inner class MyViewHolder(var checkoutServiceAdapterBinding: CheckoutServiceAdapterBinding): RecyclerView.ViewHolder(checkoutServiceAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bindItems(task: PassengerCreateData) {

            //bind the item here
            checkoutServiceAdapterBinding.checkoutPassengerName.text = task.name_view
            checkoutServiceAdapterBinding.checkoutPassengerNo.text = task.country_code+ " " +task.mobile_number
            checkoutServiceAdapterBinding.checkoutPassengerEmail.text = task.email_id


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val checkoutServiceAdapterBinding = CheckoutServiceAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(checkoutServiceAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(checkoutDataService[position])
        holder.checkoutServiceAdapterBinding.checkoutCancel.setOnDebounceListener {
            checkoutDataService.removeAt(position)
            clickListener(holder)
            notifyDataSetChanged()
            Toast.makeText(
                holder.itemView.context,
                "service Contact detail cleared",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return checkoutDataService.size
    }
}