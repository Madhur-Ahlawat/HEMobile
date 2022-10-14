package com.conduent.nationalhighways.data.model.account

data class EmailValidationModel(var enable: Boolean = false, var email:String="" , var code:String="")
