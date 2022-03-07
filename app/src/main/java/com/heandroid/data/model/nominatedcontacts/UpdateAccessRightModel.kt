package com.heandroid.data.model.nominatedcontacts

import com.google.gson.annotations.SerializedName

data class UpdateAccessRightModel(
    val action: String?,
    val accountId: String?,
    val updateList: MutableList<UpdatePermissionModel?>?
)