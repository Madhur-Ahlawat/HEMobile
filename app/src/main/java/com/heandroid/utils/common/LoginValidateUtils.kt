package com.heandroid.utils.common

object LoginValidateUtils {

    fun validLoginInput(
        userName : String,
        password : String,
    ) : Boolean {
        // write conditions along with their return statement
        // if username / password / confirm password are empty return false
        if (userName.isEmpty() || password.isEmpty()){
            return false
        }
        // if digit count of the password is less than 2 return false
        if (password.count { it.isDigit() } < 2){
            return false
        }
        return true
    }
}