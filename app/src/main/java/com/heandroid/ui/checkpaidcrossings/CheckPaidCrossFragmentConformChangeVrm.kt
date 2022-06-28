package com.heandroid.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.heandroid.R
import com.heandroid.databinding.FragmentEnterVrmCheckChangeConformBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckPaidCrossFragmentConformChangeVrm : BaseFragment<FragmentEnterVrmCheckChangeConformBinding>(), View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by viewModels()

    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnterVrmCheckChangeConformBinding =
        FragmentEnterVrmCheckChangeConformBinding.inflate(inflater, container, false)

    override fun init() {

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {


    }

    override fun observer() {

    }


    override fun onClick(v: View?) {


    }
}