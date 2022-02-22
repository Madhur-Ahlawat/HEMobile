package com.heandroid.utils.common

sealed class Resource <T>(val data:T? =null, val errorMsg: String="")
{
    class Success<T>(data: T?) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    //class DataError<T>(errorCode: Int) : Resource<T>(null, errorCode)
    class DataError<T>(msg: String?) : Resource<T>(null, msg?:"")

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            //is DataError -> "Error[exception=$errorCode]"
            is DataError -> "Error[exception=$errorMsg]"
            is Loading<T> -> "Loading"
        }
    }
}
