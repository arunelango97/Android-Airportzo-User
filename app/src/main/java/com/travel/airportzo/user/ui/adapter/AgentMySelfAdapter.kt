package com.travel.airportzo.user.ui.adapter



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.AgentmyselfAdapterBinding
import com.travel.airportzo.user.model.MyBookingData
import com.travel.airportzo.user.utils.setOnDebounceListener

class AgentMySelfAdapter(private var agentMyselfData: ArrayList<MyBookingData>, var onClicked:(String)->Unit) :RecyclerView.Adapter<AgentMySelfAdapter.MyViewHolder>(){
    inner class MyViewHolder(var agentMyselfAdapterBinding: AgentmyselfAdapterBinding):RecyclerView.ViewHolder(agentMyselfAdapterBinding.root) {
        fun bindItems(task: MyBookingData) {
            agentMyselfAdapterBinding.aAirportList.text = task.journey
            agentMyselfAdapterBinding.dDateAndTime.text = task.date_time
            agentMyselfAdapterBinding.pPassengerListCount.text = task.total_adult
            agentMyselfAdapterBinding.childrenListCount.text = task.total_children
            agentMyselfAdapterBinding.serviceCount.text = task.total_service
            agentMyselfAdapterBinding.aAmount.text = task.total_amount

            agentMyselfAdapterBinding.layout.setOnDebounceListener { onClicked(task.token) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val agentMyselfAdapterBinding = AgentmyselfAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(agentMyselfAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(agentMyselfData[position])
    }

    override fun getItemCount(): Int {
        return agentMyselfData.size
    }
}
