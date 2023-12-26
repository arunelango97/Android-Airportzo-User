package com.travel.airportzo.user.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.ManageAccountAdapterBinding
import com.travel.airportzo.user.model.GetOrderDetailData
import com.travel.airportzo.user.model.ManageAccountData
import com.travel.airportzo.user.model.ReadServiceJson
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.fragments.HomeFragment
import com.travel.airportzo.user.utils.setOnDebounceListener
import java.util.ArrayList

class ManageAccountAdapter(private var manageAccountData: ArrayList<ManageAccountData>, val onClick: (String) -> Unit) : RecyclerView.Adapter<ManageAccountAdapter.MyViewHolder>() {

    inner class MyViewHolder(private var manageAccountAdapterBinding: ManageAccountAdapterBinding):RecyclerView.ViewHolder(manageAccountAdapterBinding.root) {
        @SuppressLint("SetTextI18n", "SuspiciousIndentation")
        fun bind(task: ManageAccountData){
            val context = manageAccountAdapterBinding.root.context

            manageAccountAdapterBinding.bankNo.text=task.deviceInfo
            manageAccountAdapterBinding.bankName.text=task.deviceInfo
                Glide.with(context).load(task.deviceImage).into(manageAccountAdapterBinding.bankImage)

           manageAccountAdapterBinding.loggedIn.text = task.lastLoggedIn
            manageAccountAdapterBinding.logout.setOnDebounceListener {
            onClick(task.deviceToken)
            }

            manageAccountAdapterBinding.logout.setTextColor(Color.parseColor(context?.let {
                SavedSharedPreference.getCustomColor(
                    it
                ).secondary_colour
            }))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val manageAccountAdapterBinding = ManageAccountAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(manageAccountAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(manageAccountData[position])
    }

    override fun getItemCount(): Int {
        return manageAccountData.size
    }
}