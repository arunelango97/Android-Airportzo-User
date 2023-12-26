package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.CheckoutAdapterBinding
import com.travel.airportzo.user.model.CheckoutData
import java.util.ArrayList

class CheckoutSavedPassengerAdapter(
    var context: Context,
    private var checkoutSavedPassenger: ArrayList<CheckoutData>
) :RecyclerView.Adapter<CheckoutSavedPassengerAdapter.MyViewHolder>() {
   inner class MyViewHolder(var checkoutAdapterBinding: CheckoutAdapterBinding):RecyclerView.ViewHolder(checkoutAdapterBinding.root) {
       fun bindItems(task: CheckoutData) {

           //bind the item here
           checkoutAdapterBinding.checkoutPassengerName.text = task.name
           checkoutAdapterBinding.checkoutPassengerNo.text = task.mobile_number
           checkoutAdapterBinding.checkoutPassengerEmail.text = task.email_id
           checkoutAdapterBinding.checkoutPassengerAge.text = task.date_of_birth

    }
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val checkoutAdapterBinding = CheckoutAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(checkoutAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(checkoutSavedPassenger[position])
    }

    override fun getItemCount(): Int {
      return checkoutSavedPassenger.size
    }
}