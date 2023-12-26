package com.travel.airportzo.user.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.AgentotherAdapterBinding
import com.travel.airportzo.user.model.MyBookingData
import com.travel.airportzo.user.utils.setOnDebounceListener

class AgentOtherAdapter(private var agentOtherData: ArrayList<MyBookingData>, var onClicked: (String) -> Unit) :RecyclerView.Adapter<AgentOtherAdapter.MyViewHolder>() {
    inner class MyViewHolder(var agentOtherAdapterBinding: AgentotherAdapterBinding):RecyclerView.ViewHolder(agentOtherAdapterBinding.root) {
        fun bindItems(task: MyBookingData) {
            agentOtherAdapterBinding.aAirportList.text = task.journey
            agentOtherAdapterBinding.dDatAndTime.text = task.date_time
            agentOtherAdapterBinding.pPassengerListCount.text = task.total_adult
            agentOtherAdapterBinding.childrenListCount.text = task.total_children
            agentOtherAdapterBinding.serviceCount.text = task.total_service
            agentOtherAdapterBinding.aAmount.text = task.total_amount

            agentOtherAdapterBinding.layout.setOnDebounceListener { onClicked(task.token) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val agentOtherAdapterBinding = AgentotherAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(agentOtherAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(agentOtherData[position])
    }

    override fun getItemCount(): Int {
        return agentOtherData.size
    }
}
