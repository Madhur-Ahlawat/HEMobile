package com.conduent.nationalhighways.ui.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.ErrorResponseModel
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.CustomDialogBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject


abstract class BaseActivity<T> : AppCompatActivity() {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()

    @Inject
    lateinit var smBa: SessionManager

    @Inject
    lateinit var apiServiceBa: ApiService

    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var loaderDialog: LoaderDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        observeViewModel()

        BaseApplication.CurrentContext = this

    }

    /* override fun onWindowFocusChanged(hasFocus: Boolean) {
         super.onWindowFocusChanged(hasFocus)
         if (hasFocus) {
             // allow screenshots when activity is focused
             window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
         } else {
             // hide information (blank view) on app switcher
             window.setFlags(
                 WindowManager.LayoutParams.FLAG_SECURE,
                 WindowManager.LayoutParams.FLAG_SECURE
             )
         }
     }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun displayMessage(
        fTitle: String?,
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
        pListener: DialogPositiveBtnListener?,
        nListener: DialogNegativeBtnListener?
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        val alertDialog = builder.create()

        val customTitleView = LayoutInflater.from(this).inflate(
            R.layout.alert_dialog_custom_title_layout, LinearLayout(this)
        )
        val titleText = customTitleView.findViewById<TextView>(R.id.alert_dialog_title_text)
        titleText.text = fTitle
        alertDialog.setCustomTitle(customTitleView)
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            positiveBtnTxt
        ) { dialog, _ -> pListener?.positiveBtnClick(dialog) }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            negativeBtnTxt
        ) { dialog, _ -> nListener?.negativeBtnClick(dialog) }
        alertDialog.setOnShowListener {
            val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.blue
                )
            )
            val nbutton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            nbutton.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        }

        alertDialog.show()
    }

    fun displayCustomMessage(
        fTitle: String?,
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
        pListener: DialogPositiveBtnListener?,
        nListener: DialogNegativeBtnListener?
    ) {

        val dialog = Dialog(this)
        dialog.setCancelable(false)


        val binding: CustomDialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(this))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)



        dialog.setContentView(binding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        ) //Controlling width and height.


        binding.title.text = fTitle
        binding.message.text = message
        binding.cancelBtn.text = negativeBtnTxt
        binding.okBtn.text = positiveBtnTxt
        binding.cancelBtn.setOnClickListener {
            nListener?.negativeBtnClick(dialog)
            dialog.dismiss()
        }

        binding.okBtn.setOnClickListener {
            pListener?.positiveBtnClick(dialog)
            dialog.dismiss()
        }
        dialog.show()
        dialog.setOnDismissListener {
            // Cleanup any resources here if needed
        }

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        activityScope.launch {
            withContext(Dispatchers.IO) {
                val lastTokenTime = Utils.convertStringToDate(
                    smBa.fetchStringData(SessionManager.LAST_TOKEN_TIME),
                    Constants.dd_mm_yyyy_hh_mm_ss
                )
                if (lastTokenTime != null) {
                    val diff = Utils.getTimeDifference(lastTokenTime, Date())
                    if (diff.first >= 1 || (diff.first == 0L && diff.second > 13)) {
                        BaseApplication.getNewToken(apiServiceBa, smBa)
                        BaseApplication.saveDateInSession(smBa)
                    }
                }
            }
        }
    }


    protected open fun showDialog(title: String?, message: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "OK"
            ) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun checkSessionExpiredOrServerError(errorResponsModel: ErrorResponseModel?): Boolean {
        return (errorResponsModel?.errorCode == Constants.TOKEN_FAIL && (errorResponsModel.error != null && errorResponsModel.error.equals(
            Constants.INVALID_TOKEN
        ))) || (errorResponsModel?.errorCode == Constants.INTERNAL_SERVER_ERROR &&
                (errorResponsModel.error != null && errorResponsModel.error.equals(
                    Constants.SERVER_ERROR
                )))
    }

    fun displaySessionExpireDialog(errorResponsModel: ErrorResponseModel?) {
        if (errorResponsModel?.errorCode == Constants.TOKEN_FAIL && errorResponsModel.error.equals(
                Constants.INVALID_TOKEN
            )
        ) {
            Utils.displaySesionExpiryDialog(this)
        } else if (errorResponsModel?.errorCode == Constants.INTERNAL_SERVER_ERROR && errorResponsModel.error.equals(
                Constants.SERVER_ERROR
            )
        ) {
            startNewActivityByClearingStack(LandingActivity::class.java) {
                putString(Constants.SHOW_SCREEN, Constants.SERVER_ERROR)
            }

        }
    }

    fun showLoaderDialog() {
        Log.e(
            "BaseActivity",
            "LoaderDialog showLoaderDialog: $loaderDialog ${loaderDialog?.isVisible} ${loaderDialog?.isVisible == false}"
        )
        val fragmentManager = supportFragmentManager
        val existingFragment = fragmentManager.findFragmentByTag(Constants.LOADER_DIALOG)
        if (existingFragment != null) {
            (existingFragment as LoaderDialog).dismiss()
        }
        if (loaderDialog == null) {
            loaderDialog = LoaderDialog()
            loaderDialog?.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomLoaderDialog)
        }
        if (loaderDialog?.isVisible == null || loaderDialog?.isVisible == false) {
            loaderDialog?.show(fragmentManager, Constants.LOADER_DIALOG)
        }
    }

    fun dismissLoaderDialog() {
        Log.e(
            "BaseActivity",
            "LoaderDialog dismissLoaderDialog: $loaderDialog ${loaderDialog?.showsDialog}"
        )
        if (loaderDialog != null) {
            loaderDialog?.dismiss()
            loaderDialog = null
        }
    }

}

fun AppCompatActivity.onBackPressed(callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callback()
            }
        })
}


fun FragmentActivity.onBackPressed(callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callback()
            }
        })

}

