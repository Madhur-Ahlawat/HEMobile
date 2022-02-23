package com.heandroid.ui.account.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.FragmentViewProfileBinding
import com.heandroid.ui.base.BaseFragment

class ViewProfileFragment : BaseFragment<FragmentViewProfileBinding>(), View.OnClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentViewProfileBinding = FragmentViewProfileBinding.inflate(inflater,container,false)

    override fun init() {}

    override fun initCtrl() {
        binding.editDetailsBtn.setOnClickListener(this)
    }

    override fun observer() {}

    override fun onClick(v: View?) {
     when(v?.id){
        R.id.edit_details_btn ->{ Navigation.findNavController(binding.root).navigate(R.id.action_profileFragment_to_editProfileFragment) }
     }
    }


}