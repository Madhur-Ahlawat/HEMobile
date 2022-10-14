package com.conduent.nationalhighways.data.model.contactdartcharge

data class UploadFileResponseModel(
    val uploaded: Boolean?,
    val fileName: String?,
    val originalFileName: String?,
    val pathForVector: String?
)
