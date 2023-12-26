package com.travel.airportzo.user.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.travel.airportzo.user.databinding.PackageReviewFragmentBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.ui.adapter.ReviewAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.Utility

class PackageReviewFragment : BaseFragment() {

    private val packageReviewFragment by lazy { PackageReviewFragmentBinding.inflate(layoutInflater) }
    private val aboutReviewData: ArrayList<ServiceTicketData.Reviews> by lazy { arrayListOf() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        for (a in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
            for (b in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!!.size) {
                for (c in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array.size) {
                    if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].service_token == Utility.bottom &&
                        ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].business_names==Utility.business ) {
                        if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].reviews.isNotEmpty()) {
                            aboutReviewData.clear()
                            ChooseServiceFragment.servicePosition?.let {
                                ChooseServiceFragment.ticketServiceListData[it].service_collection!![a].service_group!![b].reviews }?.let { aboutReviewData.addAll(it) }
                            packageReviewFragment.reviewList.adapter = context?.let {
                                ReviewAdapter(it ,aboutReviewData)
                            }
                            if (aboutReviewData.isEmpty()){
                                packageReviewFragment.reviewList.visibility = View.GONE
                            }else{
                                packageReviewFragment.noReview.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return packageReviewFragment.root
    }

}