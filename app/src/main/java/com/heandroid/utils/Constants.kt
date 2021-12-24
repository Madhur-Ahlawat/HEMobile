package com.heandroid.utils

object Constants {

    const val APPLE_PAY = "apple_pay"
    const val GOOGLE_PAY = "google_pay"
    const val PAY_PAL = "pay_pal"
    const val QUICK_PAYMENT = "quick_payment"
    const val BANK_TRANSFER = "bank_transfer"
    const val CARD_PAYMENT = "card_payment"
    const val OPTIONS = "options"
    const val EMAIL_MODE = 1
    const val MESSAGE_MODE = 2
    const val POST_MAIL_MODE = 3
    const val POST_CODE = "post_code"
    const val DATA = "data"
    const val POST_MAIL = "post"
    const val MESSAGE = "phone"
    const val EMAIL = "email"
    const val MODE = "mode"
    val PAYMENT_RESPONSE = "payment_resp"
    val PAYMENT_DATA = "payment_data"
    val VEHICLE_DATA = "vehicle_data"
    val VEHICLE_RESPONSE = "vehicle_api_resp"
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

    const val FORGOT_USERNAME_URL =
        "https://maas-test.services.conduent.com/bosuser/api/account/forgotUserDetails?agencyId=12"
    const val SHOW_SCREEN = "show_screen"
    const val ABOUT_SERVICE="about_service"
    const val CONTACT_DART_CHARGES="contact_dart_charges"
    const val CROSSING_SERVICE_UPDATE="crossing_service_update"
}
