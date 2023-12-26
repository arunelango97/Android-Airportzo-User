package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.ChooseticketAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.fragments.ChooseServiceFragment.Companion.ticketServiceListData
import com.travel.airportzo.user.utils.Utility

class ChooseTicketAdapter (var dataList: ArrayList<ServiceTicketData.Station_array>, private var clickListener: (ChooseTicketAdapter.MyViewHolder,Int)-> Unit) :
    RecyclerView.Adapter<ChooseTicketAdapter.MyViewHolder>() {
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val chooseTicketAdapter = ChooseticketAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(chooseTicketAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(dataList[position])
        clickListener(holder,position)
        Log.d("Adapter", "onBindViewHolder called for position $position, journey_date = ${ticketServiceListData[position].journey_date}")
    }

    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()

    //the class is holding the list view
    inner class MyViewHolder(var chooseTicketAdapterBinding: ChooseticketAdapterBinding) : RecyclerView.ViewHolder(
        chooseTicketAdapterBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(task: ServiceTicketData.Station_array) {
            Log.d("ticketAdapter", task.toString())
            /** brand color*/
            chooseTicketAdapterBinding.sServiceView.setBackgroundColor(Color.parseColor(
                SavedSharedPreference.getCustomColor(itemView.context).brand_colour))


            //bind the item here
            chooseTicketAdapterBinding.cServicePortName.text = task.airport_code
            chooseTicketAdapterBinding.cServicePortTerminal.text = "${task.airport_name} ${task.terminal_name}"
            chooseTicketAdapterBinding.dateTime.text = "${task.journey_date}\n${task.gmt_view}"
            Log.d("bindItems","${task.journey_date}\n${task.gmt_view}".toString() )

            Utility.dateTime = chooseTicketAdapterBinding.dateTime.text.toString()

        }

    }


}