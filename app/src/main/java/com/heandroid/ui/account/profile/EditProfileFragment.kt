package com.heandroid.ui.account.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.heandroid.databinding.FragmentEditProfileBinding
import com.heandroid.ui.base.BaseFragment

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEditProfileBinding = FragmentEditProfileBinding.inflate(inflater,container,false)
    override fun init() {}
    override fun initCtrl() {}
    override fun observer() {}
}