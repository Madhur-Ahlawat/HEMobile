package com.heandroid.ui.startNow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentAboutServiceBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.toolbar

class AboutServiceFragment : BaseFragment<FragmentAboutServiceBinding>(), View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAboutServiceBinding {

        return FragmentAboutServiceBinding.inflate(inflater, container, false)
    }

    override fun init() {
        // not need to set any toolbar
    }

    override fun initCtrl() {
        binding.apply {
            tvLink.setOnClickListener(this@AboutServiceFragment)
        }
    }

    override fun observer() {

        // nothing here to observe
    }

    override fun onResume() {
        super.onResume()
        requireActivity().toolbar(getString(R.string.str_about_this_service))
    }
    override fun onClick(v: View?) {
        v?.let {
            when(v.id)
            {
                R.id.tv_link->{

                }
            }
        }

    }
}