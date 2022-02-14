package com.heandroid.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.databinding.FragmentAccountBinding
import com.heandroid.dialog.IOSTypeDialog
import com.heandroid.model.LogOutResp
import com.heandroid.model.LoginResponse
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.view.ActivityHome
import com.heandroid.view.CommunicationActivity
import com.heandroid.view.ProfileActivity
import com.heandroid.viewmodel.AccountViewModel
import com.heandroid.viewmodel.ViewModelFactory

class AccountFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentAccountBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setClickEvents()
    }

    private fun initialize() {

        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        viewModel = ViewModelProvider(this, factory)[AccountViewModel::class.java]

    }

    private fun setClickEvents() {
        dataBinding.profile.setOnClickListener {

            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            startActivity(intent)
        }

        dataBinding.rlAccount.setOnClickListener {

            val intent = Intent(requireActivity(), CommunicationActivity::class.java)
            startActivity(intent)
        }

        dataBinding.logOutLyt.setOnClickListener {
            showDialog()
        }

    }

    private fun setupObservers() {
        //todo move to constants file
        Log.d("AccountFragment", "Before api call")
        dataBinding.progressLayout.visibility = View.VISIBLE

        viewModel.logOutUser()
        viewModel.loginOutVal.observe(requireActivity(),
            {
                when (it.status) {
                    Status.SUCCESS -> {
                        Log.d("AccountFragment", " on Succes called")

                        val logoutResponse = it.data!!.body() as LogOutResp

                        if (logoutResponse.success.equals("true", true)) {
                            navigateToHome()
                        }
                        Log.d("AccountFragment", " on Succes called logoutResponse $logoutResponse")
                        dataBinding.progressLayout.visibility = View.GONE

                    }

                    Status.ERROR -> {
                        Log.d("AccountFragment", " on Error called")

                        dataBinding.progressLayout.visibility = View.GONE
                    }

                    Status.LOADING -> {
                        Log.d("AccountFragment", " on Loading called")

                        // show/hide loader
                        dataBinding.progressLayout.visibility = View.VISIBLE
                    }
                }
            })
    }

    private fun showDialog(){

        IOSTypeDialog.Builder(requireActivity()).setTitle("Logout").setMessage("Are You sure \n Do you want to logout?").setPositiveButton("Ok"
        ) { _, _ ->
            setupObservers()

        }.setNegativeButton("Cancel"
        ) { _, _ ->

        }.show()
    }

    private fun navigateToHome() {
        requireActivity().finish()

        Intent(requireActivity(), ActivityHome::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(this)
        }

    }

}