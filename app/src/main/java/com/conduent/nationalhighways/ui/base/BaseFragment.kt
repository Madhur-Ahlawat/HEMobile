package com.conduent.nationalhighways.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.utils.common.AdobeAnalytics

abstract class BaseFragment<B: ViewBinding> : Fragment() {

    protected lateinit var binding: B


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=getFragmentBinding(inflater, container)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
        observer()
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B
    abstract fun init()
    abstract fun initCtrl()
    abstract fun observer()

    override fun onResume() {
        super.onResume()
        AdobeAnalytics.setLifeCycleCallAdobe(true)

    }

    override fun onPause() {
        super.onPause()
        AdobeAnalytics.setLifeCycleCallAdobe(false)
    }

    fun displayMessage(
        fTitle: String?,
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
        pListener: DialogPositiveBtnListener?,
        nListener: DialogNegativeBtnListener?
    ) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setCancelable(false)

        val alertDialog = builder.create()

        val customTitleView = LayoutInflater.from(requireActivity()).inflate(
            R.layout.alert_dialog_custom_title_layout, LinearLayout(requireActivity())
        )

        val titleText = customTitleView.findViewById<TextView>(R.id.alert_dialog_title_text)

        titleText.text = fTitle?.let { it }


        alertDialog.setCustomTitle(customTitleView)
        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            positiveBtnTxt
        ) { dialog, which -> pListener?.positiveBtnClick(dialog) }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            negativeBtnTxt
        ) { dialog, which -> nListener?.negativeBtnClick(dialog) }
        alertDialog.setOnShowListener {
            val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.black
                )
            )
            val nbutton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            nbutton.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.black
                )
            )
        }

        alertDialog.show()
    }



}