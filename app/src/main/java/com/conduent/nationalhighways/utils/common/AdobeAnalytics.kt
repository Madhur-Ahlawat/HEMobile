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
    const val accountOption = "ruc.app.account"
    const val timeOut = "ruc.account.timeout"
    const val login = "ruc.account.login"
    const val optionType = "ruc.account.passwordResetOption"
    const val loginMethod = "ruc.account.loginMethod"
    const val paymentMethod = "ruc.order.paymentMethod"
    const val orderId = "ruc.order.orderId"
    const val orderPlaced = "ruc.order.orderPlaced"
    const val apiError = "ruc.page.error"
    const val userType = "ruc.account.usertype"
    const val userLevel = "ruc.account.userlevel"
    const val financialStatus = "ruc.account.financialstatus"
    const val paymentType = "ruc.account.paymenttype"
    const val accountStatus = "ruc.account.accountstatus"


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
        logIn: Any
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


    fun setDashBoardScreenTrack(
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        stateName: String,
        userT: String,
        userL: String,
        financialS: String,
        paymentT: String,
        accountS: String,
        logIn: Any
    ) {

        val mContextData = HashMap<String, String>()
        mContextData[pageNameKey] = pageName
        mContextData[pageTypeKey] = pageType
        mContextData[languageKey] = language
        mContextData[sectionKey] = section
        mContextData[prevPageNameKey] = prevPageName
        mContextData[userT] = userType
        mContextData[userL] = userLevel
        mContextData[financialS] = financialStatus
        mContextData[paymentT] = paymentType
        mContextData[accountS] = accountStatus
        mContextData[loggedIn] = logIn.toString()
        trackState(stateName, mContextData)

    }

    fun setActionTrack(
        actionKey: String,
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        logIn: Any
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

    fun setActionTrackError(
        actionKey: String,
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        apiErrorMsg: String,
        logIn: Any
    ) {

        val mContextData = HashMap<String, String>()
        mContextData[pageNameKey] = pageName
        mContextData[pageTypeKey] = pageType
        mContextData[languageKey] = language
        mContextData[sectionKey] = section
        mContextData[prevPageNameKey] = prevPageName
        mContextData[apiError] = apiErrorMsg
        mContextData[loggedIn] = logIn.toString()

        trackAction(actionKey, mContextData)


    }

    fun setLoginActionTrackError(
        actionKey: String,
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        apiTimeOut: String,
        loginType: String,
        logIn: Any
    ) {

        val mContextData = HashMap<String, String>()
        mContextData[pageNameKey] = pageName
        mContextData[pageTypeKey] = pageType
        mContextData[languageKey] = language
        mContextData[sectionKey] = section
        mContextData[prevPageNameKey] = prevPageName
        mContextData[timeOut] = apiTimeOut
        mContextData[loginMethod] = loginType
        mContextData[loggedIn] = logIn.toString()

        trackAction(actionKey, mContextData)


    }

    fun setActionTrackPaymentMethodOrderId(
        actionKey: String,
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        apiErr: String,
        paymentM: String,
        ordId: String,
        ordPlcd: String,
        logIn: Any
    ) {

        val mContextData = HashMap<String, String>()
        mContextData[pageNameKey] = pageName
        mContextData[pageTypeKey] = pageType
        mContextData[languageKey] = language
        mContextData[sectionKey] = section
        mContextData[prevPageNameKey] = prevPageName
        mContextData[apiErr] = apiError
        mContextData[paymentMethod] = paymentM
        mContextData[ordId] = orderId
        mContextData[ordPlcd] = orderPlaced
        mContextData[loggedIn] = logIn.toString()

        trackAction(actionKey, mContextData)


    }

    fun setActionTrackPaymentMethod(
        actionKey: String,
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        apiErr: String,
        paymentM: String,
        logIn: Any
    ) {

        val mContextData = HashMap<String, String>()
        mContextData[pageNameKey] = pageName
        mContextData[pageTypeKey] = pageType
        mContextData[languageKey] = language
        mContextData[sectionKey] = section
        mContextData[prevPageNameKey] = prevPageName
        mContextData[apiErr] = apiError
        mContextData[paymentMethod] = paymentM
        mContextData[loggedIn] = logIn.toString()

        trackAction(actionKey, mContextData)


    }

    fun setActionTrack1(
        actionKey: String,
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        accountOpt: String,
        logIn: Any
    ) {

        val mContextData = HashMap<String, String>()
        mContextData[pageNameKey] = pageName
        mContextData[pageTypeKey] = pageType
        mContextData[languageKey] = language
        mContextData[sectionKey] = section
        mContextData[prevPageNameKey] = prevPageName
        mContextData[accountOption] = accountOpt
        mContextData[loggedIn] = logIn.toString()

        trackAction(actionKey, mContextData)


    }

    fun setActionTrack2(
        actionKey: String,
        pageName: String,
        pageType: String,
        language: String,
        section: String,
        prevPageName: String,
        resetOption: String,
        logIn: Any
    ) {

        val mContextData = HashMap<String, String>()
        mContextData[pageNameKey] = pageName
        mContextData[pageTypeKey] = pageType
        mContextData[languageKey] = language
        mContextData[sectionKey] = section
        mContextData[prevPageNameKey] = prevPageName
        mContextData[optionType] = resetOption
        mContextData[loggedIn] = logIn.toString()

        trackAction(actionKey, mContextData)


    }


}