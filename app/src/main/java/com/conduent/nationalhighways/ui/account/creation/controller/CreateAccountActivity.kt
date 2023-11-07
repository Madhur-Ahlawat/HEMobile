package com.conduent.nationalhighways.ui.account.creation.controller

import android.content.DialogInterface.OnShowListener
import android.util.TypedValue
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.ActivityCreateAccountBinding
import com.conduent.nationalhighways.ui.account.creation.newAccountCreation.AccountSuccessfullyCreationFragment
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CreateAccountActivity : BaseActivity<Any>(),LogoutListener {
    lateinit var binding: ActivityCreateAccountBinding
    @Inject
    lateinit var sessionManager: SessionManager

    override fun initViewBinding() {
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_create_an_account)
        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }


        AdobeAnalytics.setScreenTrack(
            "create account",
            "create account",
            "english",
            "create account",
            "home",
            "create account",
            sessionManager.getLoggedInUser()
        )

        ratingDialog()

    }

    private fun ratingDialog() {
//        val builder: RatingDialog.Builder = RatingDialog.Builder(this)
//        builder.title(this.getString(R.string.app_name))
//        builder.positiveButtonText(this.getString(R.string.not_now))
//        builder.ratingBarColor(R.color.blue_color)
//        builder.playstoreUrl("https://play.google.com/store/apps/details?id=com.conduent.nationalhighways")
//        builder.icon(getDrawable(R.drawable.transactions))
//        builder.session(12)
//
//// create the dialog and show it
//
//// create the dialog and show it
//        val dialog: RatingDialog = builder.build()
//        dialog.setOnShowListener(OnShowListener {
//            dialog.getIconImageView().setScaleX(0.8f)
//            dialog.getIconImageView().setScaleY(0.8f)
//            dialog.getTitleTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
//            dialog.getTitleTextView().setTextColor(this.getColor(R.color.grayColorIcon))
//        })
//        dialog.show()
    }

    override fun observeViewModel() {}



    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->
                if (fragment is AccountSuccessfullyCreationFragment){

                }else{
                    onBackPressedDispatcher.onBackPressed()
                }

            }
        }

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        LogoutUtil.stopLogoutTimer()
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }
}

