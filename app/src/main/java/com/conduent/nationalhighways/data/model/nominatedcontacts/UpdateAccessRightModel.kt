package com.conduent.nationalhighways.data.model.nominatedcontacts

data class UpdateAccessRightModel(
    val action: String?,
    val accountId: String?,
    val updData: MutableList<UpdatePermissionModel?>?
)