package com.travel.airportzo.user.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.travel.airportzo.user.databinding.PackageAboutFragmentBinding
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.ui.adapter.AboutAmenitiesAdapter
import com.travel.airportzo.user.ui.adapter.AboutCancelPolicyAdapter
import com.travel.airportzo.user.ui.adapter.AboutPhotoAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import com.travel.airportzo.user.utils.Utility


class PackageAboutFragment : BaseFragment() {

    private val packageAboutFragment by lazy { PackageAboutFragmentBinding.inflate(layoutInflater) }

    private val aboutPhotoPackageData: ArrayList<String> by lazy { arrayListOf() }
    private val aboutAmenitiesPackageData: ArrayList<ServiceTicketData.Amenities> by lazy { arrayListOf() }
    private val packageCancelData: ArrayList<ServiceTicketData.Cancellation_policy_detail> by lazy { arrayListOf() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        for (a in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
//            for (b in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!!.size) {
//                for (c in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array.size) {
//                    if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].sp_company_token == Utility.bottom && ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].business_names == Utility.business) {
//                        if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].photos.isNotEmpty()) {
//                            aboutPhotoPackageData.clear()
//                            ChooseServiceFragment.servicePosition?.let { ChooseServiceFragment.ticketServiceListData[it].service_collection!![a].service_group!![b].photos }
//                                ?.let { aboutPhotoPackageData.addAll(it) }
//                            packageAboutFragment.sAboutImagesRecyclerview.adapter =
//                                context?.let { AboutPhotoAdapter(it, aboutPhotoPackageData) }
//                        }
//                    }
//                }
//            }
//        }

//        for (a in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
//            for (b in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!!.size) {
//                for (c in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array.size) {
//                    if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].sp_company_token == Utility.bottom && ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].service_array[c].business_names == Utility.business) {
//                        if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![a].service_group!![b].amenities.isNotEmpty()) {
//                            aboutAmenitiesPackageData.clear()
//                            ChooseServiceFragment.servicePosition?.let {
//                                ChooseServiceFragment.ticketServiceListData[it].service_collection!![a].service_group!![b].amenities
//                            }?.let { aboutAmenitiesPackageData.addAll(it) }
//                            packageAboutFragment.sAboutFacilityRecyclerview.adapter = context?.let {
//                                AboutAmenitiesAdapter(it, aboutAmenitiesPackageData)
//                            }
//                        }
//                    }
//                }
//            }
//        }

//        for (i in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
//            for (j in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!!.size) {
//                for (k in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].cancellation_policy_detail.size) {
//                    if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].cancellation_policy_detail.isNotEmpty()) {
//                        if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].sp_company_token == Utility.bottom && ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].business_names == Utility.business) {
//
////                            packageCancelData.clear()
//
//                            ChooseServiceFragment.servicePosition?.let {
//                                ChooseServiceFragment.ticketServiceListData[it].service_collection!![i].service_group!![j].cancellation_policy_detail[k]
//                            }?.let { packageCancelData.add(it) }
//                            packageAboutFragment.cancelRecycler.adapter =
//                                context?.let { AboutCancelPolicyAdapter(it, packageCancelData) }
//                        }
//                    }
//                }
//            }
//        }


        for (serviceCollectionData in ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!) {
            for (serviceGroupData in serviceCollectionData.service_group!!) {
                for (serviceArrayData in serviceGroupData.service_array){
                if (serviceArrayData.service_token == Utility.bottom && serviceArrayData.business_names == Utility.business) {

                    if (serviceGroupData.photos.isNotEmpty()) {
                        aboutPhotoPackageData.clear()
                        ChooseServiceFragment.servicePosition?.let { serviceGroupData.photos }
                            ?.let { aboutPhotoPackageData.addAll(it) }
                        packageAboutFragment.sAboutImagesRecyclerview.adapter =
                            context?.let { AboutPhotoAdapter(it, aboutPhotoPackageData) }
                    }

                    if (serviceGroupData.amenities.isNotEmpty()) {
                        aboutAmenitiesPackageData.clear()
                        ChooseServiceFragment.servicePosition?.let {
                            serviceGroupData.amenities
                        }?.let { aboutAmenitiesPackageData.addAll(it) }
                        packageAboutFragment.sAboutFacilityRecyclerview.adapter =
                            context?.let {
                                AboutAmenitiesAdapter(it, aboutAmenitiesPackageData)
                            }
                    }

                    ChooseServiceFragment.servicePosition?.let {
                        serviceGroupData.cancellation_policy_detail
                    }?.let { packageCancelData.addAll(it) }

                    packageCancelData.add(ServiceTicketData.Cancellation_policy_detail("0", "0"))

                    packageAboutFragment.cancelRecycler.adapter =
                        context?.let { AboutCancelPolicyAdapter(it, packageCancelData) }

                    packageAboutFragment.sAboutDescription.text = serviceGroupData.description

                    packageAboutFragment.rescheduleText.text =
                        serviceGroupData.reschedule_policy
                }
            }
        }
        }


//        for (m in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
//            for (n in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![m].service_group!!.size) {
//                if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![m].service_group!![n].about_description.isNotEmpty()) {
//                    if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![m].service_group!![n].sp_company_token == Utility.bottom &&
//                        ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![m].service_group!![n].business_names==Utility.business ) {
//                        packageAboutFragment.sAboutDescription.text =
//                            ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![m].service_group!![n].about_description
//                    }
//                }
//            }
//        }

//        for (i in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!!.size) {
//            for (j in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!!.size) {
//                for (k in 0 until ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].cancellation_policy_detail.size) {
//                    if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].reschedule_policy.isNotEmpty()) {
//                        if (ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].sp_company_token == Utility.bottom && ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].business_names == Utility.business) {
//                            packageAboutFragment.rescheduleText.text =
//                                ChooseServiceFragment.ticketServiceListData[ChooseServiceFragment.servicePosition!!].service_collection!![i].service_group!![j].reschedule_policy
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return packageAboutFragment.root
    }

}
