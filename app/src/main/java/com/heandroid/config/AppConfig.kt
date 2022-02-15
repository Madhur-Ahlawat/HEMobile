package com.heandroid.config

import android.net.Uri
import java.net.URL

class AppConfig {
    val baseURL: Uri? = Uri.parse("https://maas-test.services.conduent.com")
    val oAuthURL: Uri? = Uri.parse("https://maas-test.services.conduent.com/oauth/token/")
    val agencyID: String = "12"
    val clientID: String = "NY_EZ_Pass_iOS_QA"
    val clientSecret:String ="N4pBHuCUgw8D2BdZtSMX2jexxw3tp7"
}