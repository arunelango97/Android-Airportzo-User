package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.JourneyAdapterBinding
import com.travel.airportzo.user.model.GetOrderDetailData

class JourneyAdapter(
    var context: Context,
    private var bookingJourneyData: ArrayList<GetOrderDetailData.Journey_detail>
) : RecyclerView.Adapter<JourneyAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val journeyAdapterBinding: JourneyAdapterBinding) :
        RecyclerView.ViewHolder(journeyAdapterBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(task: GetOrderDetailData.Journey_detail) {
            journeyAdapterBinding.flightFrom.text = "${task.depart_airport}-${task.depart_terminal}"
            journeyAdapterBinding.flightTo.text = "${task.arrival_airport}-${task.arrival_terminal}"
            journeyAdapterBinding.flightDate.text = task.depart_date
            journeyAdapterBinding.flightNumber.text = task.flight_number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val journeyAdapterBinding =
            JourneyAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(journeyAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(bookingJourneyData[position])
    }

    override fun getItemCount(): Int {
        return bookingJourneyData.size
    }
}