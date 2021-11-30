package com.heandroid.utils

object Constants {

    const val POST_CODE = "post_code"
    const val DATA = "data"
    const val POST_MAIL = "post"
    const val MESSAGE = "message"
    const val EMAIL = "email"
    const val MODE = "mode"
    val PAYMENT_RESPONSE = "payment_resp"
    val PAYMENT_DATA="payment_data"
    val VEHICLE_DATA= "vehicle_data"
    val VEHICLE_RESPONSE="vehicle_api_resp"
    const val BASE_URL = "https://maas-test.services.conduent.com"
    //const val LOGIN_URL = "/oauth/token/"
    const val LOGIN_URL = "https://maas-test.services.conduent.com/oauth/token/"
    const val VEHICLE_URL = "https://maas-test.services.conduent.com/oauth/token/"
   // const val LOGIN_URL = "https://maas-test.services.conduent.com/oauth/token/"
   // const val POSTS_URL = ""

    /*accountOverview: `${BOSUSER}account/overview`,
    paymentList: `${PAYMENTS}account/retrievepaymentslist`,
    vehicleInfo: `${BOSUSER}account/vehicle`,
    TransationHistory: `${TRIPS}transactions`,*/

    const val FORGOT_USERNAME_URL = "https://maas-test.services.conduent.com/bosuser/api/account/forgotUserDetails?agencyId=12"
}
