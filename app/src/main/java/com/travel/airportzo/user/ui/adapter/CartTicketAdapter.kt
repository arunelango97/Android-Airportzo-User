package com.travel.airportzo.user.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.CartticketAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.utils.setOnDebounceListener

class CartTicketAdapter (var dataList: ArrayList<ServiceTicketData.Station_array>, var onClicked: (ServiceTicketData.Station_array) -> Unit) :
RecyclerView.Adapter<CartTicketAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val cartTicketAdapter = CartticketAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(cartTicketAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(dataList[position])
    }

    //the class is holding the list view
    inner class MyViewHolder(private val cartTicketAdapterBinding: CartticketAdapterBinding ) : RecyclerView.ViewHolder(cartTicketAdapterBinding.root) {

        fun bindItems(task: ServiceTicketData.Station_array) {
            //bind the item here
            cartTicketAdapterBinding.aAirportList.text = task.airport_code
//            cartTicketAdapterBinding.dDateAndTime.text = task.dateandtime
//            cartTicketAdapterBinding.pPassengerList.text = task.adult
//            cartTicketAdapterBinding.childrenList.text = task.children
//            cartTicketAdapterBinding.services.text = task.services
//            cartTicketAdapterBinding.rReward.text = task.reward
//            cartTicketAdapterBinding.aAmount.text = task.amount

            cartTicketAdapterBinding.layout.setOnDebounceListener { onClicked(task) }

        }

    }

}