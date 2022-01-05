package com.heandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.FragmentProfileBinding

class ProfileFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = Bundle()
        dataBinding.editDetailsBtn.setOnClickListener {
            Navigation.findNavController(dataBinding.root)
                .navigate(R.id.action_profileFragment_to_editProfileFragment)

        }
    }


}