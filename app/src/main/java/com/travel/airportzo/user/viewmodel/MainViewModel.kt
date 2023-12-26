package com.travel.airportzo.user.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.gson.JsonObject
import com.travel.airportzo.user.model.ServiceTicketData
import com.travel.airportzo.user.model.UpdateReportClass
import com.travel.airportzo.user.network.ApiResult
import com.travel.airportzo.user.ui.fragments.PackageServiceFragment
import kotlinx.coroutines.Dispatchers

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _myDataLiveData = MutableLiveData<ServiceTicketData.Station_array>()
    val myDataLiveData: LiveData<ServiceTicketData.Station_array>
        get() = _myDataLiveData


    fun updateMyData(updatedData: ServiceTicketData.Station_array) {
        _myDataLiveData.value = updatedData
    }




    fun loginCall(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.loginCall(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }


    fun otpverifyCall(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.otpverifyCall(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }


    fun terminalList() = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.terminalList()
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }


    fun searchList(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.searchList(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun createOrder(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.createOrder(jsonObject)
            emit(ApiResult.Success(data))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }


    fun bookingHistory(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.bookingHistory(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun availableCoupon() = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.availableCoupon()
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun getOrder(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.getOrder(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }


    fun services(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.services(jsonObject)
            if (data.statusCode == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error", data.message))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun contactDetail(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.contactDetail(jsonObject)
            if (data.statusCode == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error", data.message))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun readContact() = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.readContact()
            if (data.statusCode == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error", data.message))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }


    fun applyCoupon(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            emit(ApiResult.Loading(false))
            val data = mainRepository.applyCoupon(jsonObject)
            if (data.statusCode == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error", data.message))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun servicedetail(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.servicedetail(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun createpassenger(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.createpassenger(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun readdpassenger(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.readdpassenger(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun updatepassenger(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.updatepassenger(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun deletepassenger(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.deletepassenger(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun creategst(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.creategst(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun readdgst(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.readdgst(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun updategst(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.updategst(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun deletegst(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.deletegst(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun update(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.update(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun updateprofile(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.updateprofile(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun reported() = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.reported()
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun updatereport(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.updatereport(jsonObject)
            if (data.status_code == 200){
                val updateReportClass = UpdateReportClass(message = data.message.toString(), title = data.title.toString(), status_code = "${data.status_code}")
                emit(ApiResult.Success(updateReportClass))
            } else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun updatereview(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.updatereview(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }
    fun updateComment(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.updateComment(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }


    fun countries() = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.countries()
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun region(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.region(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun cities(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.cities(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun becomeagentt(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.becomeagentt(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun accountcreate(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.accountcreate(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun accountread(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.accountread(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun accountupdate(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.accountupdate(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun accountdelete(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.accountdelete(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun agentdashboard(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.agentdashboard(jsonObject)
            emit(ApiResult.Loading(false))
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Loading(false))
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
    }

    fun cancelBookingOrder(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.cancelBookingOrder(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

    fun cancelBooking(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.cancelBooking(jsonObject)
            emit(ApiResult.Loading(false))
            if (data.status_code == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Loading(false))
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
    }

    fun faqQuestion() = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.faqQuestion()
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }


    fun manageAccount(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.manageAccount(jsonObject)
            emit(ApiResult.Loading(false))
            if (data.status_code == 200)
                emit(ApiResult.Success(data.data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Loading(false))
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
    }

    fun accountLogout(jsonObject: JsonObject) = liveData(Dispatchers.IO) {
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.accountLogout(jsonObject)
            if (data.status_code == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error", data.message!!))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }

  /*  fun setOnDataChangedListener(date: String){
        onDataChangedListener.value = date
    }
    fun setOnDataChanged(date: String) {
        selectedDate.value = date
    }*/


    /*fun getDetails() = liveData(Dispatchers.IO){
        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.getDetails()
            emit(ApiResult.Loading(false))
            if (data. == 200)
                emit(ApiResult.Success(data))
            else
                emit(ApiResult.Error("Error",data.message!!))
        }catch (exception:Exception){
            emit(ApiResult.Loading(false))
            emit(ApiResult.Error(message = exception.message ?: "Error"))
            Log.d("getUserDetail",exception.message.toString())
        }

        emit(ApiResult.Loading(true))
        try {
            val data = mainRepository.getDetails()
            emit(ApiResult.Success(data))
        } catch (exception: Exception) {
            emit(ApiResult.Error(message = exception.message ?: "Error"))
        }
        emit(ApiResult.Loading(false))
    }*/

}