package com.travel.airportzo.user.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonObject
import com.travel.airportzo.user.R
import com.travel.airportzo.user.databinding.SavedBankFragmentBinding
import com.travel.airportzo.user.model.BankDetailData
import com.travel.airportzo.user.model.IFSCData
import com.travel.airportzo.user.network.ApiIFSC
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.network.NoInternetActivity
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.utils.setOnDebounceListener
import com.travel.airportzo.user.ui.adapter.SavedBankAdapter
import com.travel.airportzo.user.ui.base.BaseFragment
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class SavedBankFragment : BaseFragment() {

    private val savedBank by lazy { SavedBankFragmentBinding.inflate(layoutInflater) }

    private val newBankData: ArrayList<BankDetailData> by lazy { arrayListOf() }
    private val bankCodeData: ArrayList<IFSCData> by lazy { arrayListOf() }

    private val savedBankAdapter by lazy { context?.let { SavedBankAdapter(it,newBankData,::deleted) }}

    private var account:String=""
    private var reAccount:String=""
    private var ifsc:String=""
    private var primary:Boolean=false
    private var bankToken:String=""
    var ifscCode: String = ""
    var branch: String = ""


    private val bank by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_addbank, null))
                /** Brand Color*/
                val saveBankBtn = findViewById<MaterialButton>(R.id.addSaveBank)
                saveBankBtn?.setBackgroundColor(Color.parseColor(activity?.let { it1 ->
                    SavedSharedPreference.getCustomColor(
                        it1
                    ).brand_colour
                }))
                setCancelable(true)}
        }
    }

    private val deleteBank by lazy {
        context?.let {
            BottomSheetDialog(it, R.style.MyBottomSheetDialogTheme).apply {
                setContentView(layoutInflater.inflate(R.layout.bottomsheet_bankinfo, null))
                setCancelable(true)}
        }
    }

    private fun deleted(token: String) {
        bankToken=token
        for (i in 0 until newBankData.size){
             if (token==newBankData[i].token){
                 deleteBank?.findViewById<TextView>(R.id.accountNo)?.text=newBankData[i].account_number
                 deleteBank?.findViewById<TextView>(R.id.accountReNo)?.text=newBankData[i].account_number
                 deleteBank?.findViewById<TextView>(R.id.accountIfscCode)?.text=newBankData[i].ifsc_code
                 deleteBank?.findViewById<TextView>(R.id.ifscText)?.text=newBankData[i].branch_name
             }
         }
        deleteBank?.show()
        bankDelete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountRead()

        savedBank.bankBack.setOnDebounceListener {
            onBackPressed()
        }

        savedBank.bankButton.setOnDebounceListener {
            bank?.show()
        }

        bank?.findViewById<EditText>(R.id.ifscEdittext)?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ifscCode = s.toString()
                if (ifscCode.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill IFSC code and Press search",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    branchName()
                }
            }
        })

        bank?.findViewById<Button>(R.id.addSaveBank)?.setOnDebounceListener {
            if ( bank?.findViewById<EditText>(R.id.accountNo)?.text?.isEmpty()==true){
                Toast.makeText(requireActivity(), "Account no is empty", Toast.LENGTH_SHORT).show()
            }else if (bank?.findViewById<EditText>(R.id.accountReNo)?.text?.isEmpty()==true){
                Toast.makeText(requireActivity(), "Re account no is empty", Toast.LENGTH_SHORT).show()
            }else if (bank?.findViewById<EditText>(R.id.accountNo)?.text?.toString() != bank?.findViewById<EditText>(R.id.accountReNo)?.text?.toString()){
                Toast.makeText(context, "Account number not matched", Toast.LENGTH_SHORT).show()
            }else if (bank?.findViewById<EditText>(R.id.accountIfscCode)?.text?.isEmpty()==true) {
                Toast.makeText(requireActivity(), "Ifsc is empty", Toast.LENGTH_SHORT).show()
            }else{
                account= bank?.findViewById<EditText>(R.id.accountNo)?.text?.toString()!!
                reAccount= bank?.findViewById<EditText>(R.id.accountReNo)?.text?.toString()!!
                ifsc=bank?.findViewById<EditText>(R.id.ifscEdittext)?.text?.toString()!!
                branch=bank?.findViewById<TextView>(R.id.ifscText)?.text?.toString()!!
                primary = bank?.findViewById<CheckBox>(R.id.aServiceCheckbox)?.isChecked == true

                addBank()
                bank?.dismiss()

                bank?.findViewById<EditText>(R.id.accountNo)?.text?.clear()
                bank?.findViewById<EditText>(R.id.accountReNo)?.text?.clear()
                bank?.findViewById<EditText>(R.id.ifscEdittext)?.text?.clear()
                bank?.findViewById<TextView>(R.id.ifscText)?.text=""
            }
        }
    }

    private fun branchName() {
            ApiIFSC.ApiInterfaceifsc().product(ifscCode)
                .enqueue(object : Callback<IFSCData> {
                    override fun onFailure(call: Call<IFSCData>, t: Throwable) {
                    }

                    override fun onResponse(
                        call: Call<IFSCData>,
                        response: Response<IFSCData>
                    ) {
                        if (response.isSuccessful) {
                            bankCodeData.clear()
                            bankCodeData.add(response.body()!!)
                           branch = bank?.findViewById<EditText>(R.id.ifscText)?.setText(response.body()!!.bRANCH).toString()
                        }
                    }
                })
    }

    private fun onBackPressed() {
        findNavController().popBackStack()
    }



    private fun addBank() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token",token)
            addProperty("account_number",account)
            addProperty("ifsc_code",ifsc)
            addProperty("branch_name",branch)
            addProperty("is_primary",primary)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.accountcreate(jsonObject = jsonObject )?.observe(requireActivity(),createAccount)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val createAccount=Observer<ApiResult<BankDetailData>>{
        when(it) {
//            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                accountRead()
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }



    private fun accountRead() {
        val token =   SavedSharedPreference.getUserData(requireContext()).token
        val jsonObject = JsonObject().apply {
            addProperty("user_token", token)
        }
        if (isNetworkConnected(requireContext())) {
            lifecycleScope.launch {
            viewModel?.accountread(jsonObject = jsonObject)?.observe(requireActivity(),accountRead)
            }
        } else {
            startActivity(Intent(requireContext(), NoInternetActivity::class.java))
        }
    }

    private val accountRead=Observer<ApiResult<List<BankDetailData>>>{
        when(it) {
            is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                newBankData.clear()
                newBankData.addAll(it.data)
                savedBank.saveNewBank.adapter=savedBankAdapter
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
        }
    }



    private fun bankDelete() {
        deleteBank?.findViewById<Button>(R.id.delete)?.setOnDebounceListener {
            val token =   SavedSharedPreference.getUserData(requireContext()).token
            val jsonObject = JsonObject().apply {
                addProperty("user_token", token)
                addProperty("token", bankToken)
            }
            if (isNetworkConnected(requireContext())) {
                lifecycleScope.launch{
                viewModel?.accountdelete(jsonObject = jsonObject)?.observe(requireActivity(),accountDelete)
                deleteBank?.dismiss()
                }
            } else {
                startActivity(Intent(requireContext(), NoInternetActivity::class.java))
            }

        }
    }

    private val accountDelete=Observer<ApiResult<Any>>{
        when(it) {
//          is ApiResult.Loading -> loader.onChanged(it.loading)
            is ApiResult.Success -> {
                accountRead()
                savedBank.saveNewBank.adapter=savedBankAdapter
            }
            is ApiResult.Error -> {
//                errors("Error", it.message)
            }
            else -> {}
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return savedBank.root
    }
}