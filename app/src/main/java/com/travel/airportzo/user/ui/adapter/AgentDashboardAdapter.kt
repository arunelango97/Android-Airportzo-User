package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.AgentDashboardAdapterBinding
import com.travel.airportzo.user.model.AgentDashboardData

class AgentDashboardAdapter(var context: Context,var agentBookingDetail: ArrayList<AgentDashboardData>) :RecyclerView.Adapter<AgentDashboardAdapter.MyViewHolder>() {
   inner class MyViewHolder(private val agentDashboardAdapterBinding: AgentDashboardAdapterBinding):RecyclerView.ViewHolder(agentDashboardAdapterBinding.root) {
  fun bindItems(task:AgentDashboardData){
    agentDashboardAdapterBinding.date.text=task.date_time
    agentDashboardAdapterBinding.bookingNo.text=task.id
    agentDashboardAdapterBinding.total.text=" ₹ ".plus(task.total_amount)
    agentDashboardAdapterBinding.earnedMile.text=" ₹ ".plus(task.total_amount)
}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val agentDashboardAdapterBinding = AgentDashboardAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(agentDashboardAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(agentBookingDetail[position])
    }

    override fun getItemCount(): Int {
       return agentBookingDetail.size
    }
}