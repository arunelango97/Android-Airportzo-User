package com.travel.airportzo.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.ReportadapterBinding
import java.io.File

class ReportAdapter(
    var context: Context,
    val data: MutableList<String> = mutableListOf(),
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {
    init {
        this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
    }

    inner class ReportViewHolder(private val reportAdapterBinding: ReportadapterBinding) :
        RecyclerView.ViewHolder(reportAdapterBinding.root) {
        fun bind(data: String) {
            reportAdapterBinding.imagePath = data
            reportAdapterBinding.viewHolder = this
            reportAdapterBinding.executePendingBindings()
            itemView.setOnClickListener {
//                listener(data)
            }

            when (File(data).extension.lowercase()) {
                "pdf" -> Glide.with(itemView.context).load(R.drawable.pdf_document)
                    .into(reportAdapterBinding.imageUploadView)
                else -> Glide.with(itemView.context).load(data)
                    .into(reportAdapterBinding.imageUploadView)
            }

        }

        fun onItemRemoved(imagePath: String) {
            notifyItemRemoved(this.layoutPosition)
            data.remove(imagePath)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val reportAdapterBinding: ReportadapterBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.reportadapter,
                parent,
                false
            )
        return ReportViewHolder(reportAdapterBinding)
    }
}

