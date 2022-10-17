package com.conduent.nationalhighways.data.model.auth.forgot.password

data class VerifyRequestOtpResp(var success: String?)

data class VerifyRequestOtpReq(var code: String?, var referenceId: String?)
