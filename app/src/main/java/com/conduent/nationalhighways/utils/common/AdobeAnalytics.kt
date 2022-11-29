package com.conduent.nationalhighways.utils.common

import com.adobe.marketing.mobile.MobileCore
import com.conduent.nationalhighways.ui.base.BaseApplication
import java.util.HashMap

object AdobeAnalytics {

    const val pageNameKey = "ruc.page.pageName"
    const val pageTypeKey = "ruc.page.pageType"
    const val languageKey = "ruc.app.language"
    const val sectionKey = "ruc.app.section"
    const val prevPageNameKey = "ruc.page.prevPageName"
    const val loggedIn = "ruc.account.loggedIn"
    const val timeOut = "ruc.account.timeout"
    const val login = "ruc.account.login"
    const val loginMethod = "ruc.account.loginMethod"
    const val paymentMethod = "ruc.order.paymentMethod"
    const val orderId = "ruc.order.orderId"
    const val orderPlaced = "ruc.order.orderPlaced"
    const val paymentError  = "ruc.page.error"

    fun trackState(state: String, contextData: MutableMap<String, String>) {
        MobileCore.trackState(state, contextData)
    }

    fun trackAction(action: String, contextData: MutableMap<String, String>) {
        MobileCore.trackAction(action, contextData)
    }

    fun setLifeCycleCallAdobe(flag: Boolean) {
        if (flag) {
            MobileCore.setApplication(BaseApplication.INSTANCE)
            MobileCore.lifecycleStart(null)
        } else {
            MobileCore.lifecyclePause()

        }
    }

    fun setScreenTrack(
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        stateName: String,
        logIn:Any
    ) {

        val mContextData = HashMap<String, String>()
        mContextData[pageNameKey] = pageName
        mContextData[pageTypeKey] = pageType
        mContextData[languageKey] = language
        mContextData[sectionKey] = section
        mContextData[prevPageNameKey] = prevPageName
        mContextData[loggedIn] = logIn.toString()
        trackState(stateName, mContextData)

    }

    fun setActionTrack(
        actionKey:String,
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        logIn:Any
    ) {

        val mContextData = HashMap<String, String>()
        mContextData[pageNameKey] = pageName
        mContextData[pageTypeKey] = pageType
        mContextData[languageKey] = language
        mContextData[sectionKey] = section
        mContextData[prevPageNameKey] = prevPageName
        mContextData[loggedIn] = logIn.toString()

        trackAction(actionKey, mContextData)


    }


}