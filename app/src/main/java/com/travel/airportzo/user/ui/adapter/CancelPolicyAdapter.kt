package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.CancelpolicyAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData

class CancelPolicyAdapter(
    var context: Context,
    private var packageServiceData: ArrayList<ServiceTicketData.Cancellation_policy_detail>,

    ) : RecyclerView.Adapter<CancelPolicyAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val cancelPolicyAdapterBinding: CancelpolicyAdapterBinding) :
        RecyclerView.ViewHolder(cancelPolicyAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(task: ServiceTicketData.Cancellation_policy_detail, position: Int) {

            if (task.hours == "0"){
                cancelPolicyAdapterBinding.amount.text = "After 24 hours"
                cancelPolicyAdapterBinding.percentage.text = "- No Refund"
            }else{
                cancelPolicyAdapterBinding.amount.text = task.hours.plus(" ").plus("Hours Before")
                val refundPercentage = 100 - task.percentage.toInt()
                cancelPolicyAdapterBinding.percentage.text = "-".plus(" ").plus(refundPercentage).plus(" ").plus("% Refund")
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val cancelPolicyAdapterBinding = CancelpolicyAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyViewHolder(cancelPolicyAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(packageServiceData[position], position)
    }

    override fun getItemCount(): Int {
        return packageServiceData.size
    }
}