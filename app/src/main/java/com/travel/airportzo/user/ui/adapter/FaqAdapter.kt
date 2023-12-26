package com.travel.airportzo.user.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.travel.airportzo.user.databinding.FaqAdapterBinding
import com.travel.airportzo.user.model.FaqData
import com.travel.airportzo.user.utils.setOnDebounceListener

class FaqAdapter(val faqData: ArrayList<FaqData>) : RecyclerView.Adapter<FaqAdapter.MyViewHolder>() {
   inner class MyViewHolder(private val faqAdapterBinding: FaqAdapterBinding): RecyclerView.ViewHolder(faqAdapterBinding.root) {
fun bindItems(faqData: FaqData) {
    faqAdapterBinding.faqQuestion.text=faqData.question


    faqAdapterBinding.policyadd.setOnDebounceListener {
        faqAdapterBinding.policyadd.visibility=View.GONE
        faqAdapterBinding.policyminus.visibility=View.VISIBLE
        faqAdapterBinding.faqAnswer.visibility=View.VISIBLE
        faqAdapterBinding.faqAnswer.text=faqData.answer
    }
    faqAdapterBinding.policyminus.setOnDebounceListener {
        faqAdapterBinding.policyadd.visibility=View.VISIBLE
        faqAdapterBinding.policyminus.visibility=View.GONE
        faqAdapterBinding.faqAnswer.visibility=View.GONE
    }
}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val faqAdapterBinding = FaqAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(faqAdapterBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(faqData[position])
    }

    override fun getItemCount(): Int {
        return faqData.size
    }
}