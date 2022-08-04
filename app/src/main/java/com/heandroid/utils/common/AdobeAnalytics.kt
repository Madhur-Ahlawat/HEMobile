package com.heandroid.utils.common

import com.adobe.marketing.mobile.MobileCore

object AdobeAnalytics {


    fun trackState(state: String, contextData: MutableMap<String,String>) {
        MobileCore.trackState(state, contextData)

    }

    fun trackAction(action:String,contextData: MutableMap<String,String>) {
        MobileCore.trackAction(action, contextData)

    }

}