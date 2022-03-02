package com.heandroid.ui.auth.session

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.heandroid.R
import com.heandroid.databinding.FragmentSignoutBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignoutFragment : BaseFragment<FragmentSignoutBinding>(), View.OnClickListener {

    override fun onResume() {
        super.onResume()
        val toolbar=requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.tvContactUs).gone()
    }

    override fun init() {
        binding.btnSignin.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
    }

    override  fun initCtrl(){ }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnSignin ->{ requireActivity().startActivity(Intent(requireActivity(),AuthActivity::class.java)) }
       //     R.id.btnStart ->{ requireActivity().startActivity(Intent(requireActivity(),ActivityHome::class.java)) }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSignoutBinding = FragmentSignoutBinding.inflate(inflater,container,false)

    override fun observer() {
    }
}