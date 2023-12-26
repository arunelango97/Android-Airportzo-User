package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.AboutcancelpolicyAdapterBinding
import com.travel.airportzo.user.model.ServiceTicketData

class AboutCancelPolicyAdapter(
    var context: Context,
    private var packageCancelData: ArrayList<ServiceTicketData.Cancellation_policy_detail>
) :RecyclerView.Adapter<AboutCancelPolicyAdapter.MyViewHolder>() {
   inner class MyViewHolder(private val aboutCancelPolicyAdapterBinding: AboutcancelpolicyAdapterBinding):RecyclerView.ViewHolder(aboutCancelPolicyAdapterBinding.root) {
       fun bind(task:ServiceTicketData.Cancellation_policy_detail){

           if (task.hours == "0"){
               aboutCancelPolicyAdapterBinding.amount.text = "After 24 hours"
               aboutCancelPolicyAdapterBinding.percentage.text = "- No Refund"
           }else{
               aboutCancelPolicyAdapterBinding.amount.text = task.hours.plus(" ").plus("Hours Before")
               val refundPercentage = 100 - task.percentage.toInt()
               aboutCancelPolicyAdapterBinding.percentage.text = "-".plus(" ").plus(refundPercentage).plus(" ").plus("% Refund")
           }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val aboutCancelPolicyAdapterBinding = AboutcancelpolicyAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return MyViewHolder(aboutCancelPolicyAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(packageCancelData[position])
    }

    override fun getItemCount(): Int {
      return  packageCancelData.size
    }
}