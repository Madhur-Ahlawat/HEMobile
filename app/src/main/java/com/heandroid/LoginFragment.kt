package com.heandroid

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.textfield.TextInputEditText

class LoginFragment: Fragment() {
    lateinit var tvForgotPassword: TextView
    lateinit var tfUserName: TextInputEditText
    lateinit var tfPassword: TextInputEditText
    lateinit var btnSignIn: TextView
    lateinit var tvCreateAccount: TextView
    var userName:String=""
    var password:String=""
    companion object {

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tfUserName = view.findViewById(R.id.edt_username)
        tfPassword = view.findViewById(R.id.edt_password)
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password)
        btnSignIn = view.findViewById(R.id.btn_signin)
        tvCreateAccount = view.findViewById(R.id.tv_create_account)

        btnSignIn.setOnClickListener {

            userName = tfUserName.text.toString().trim()
            password = tfPassword.text.toString().trim()
            Log.d("password", password)
            if(validate())
            {
                // do login
                requireActivity().supportFragmentManager.commit {
                        val bundle = bundleOf("some_int" to 0)
                        setReorderingAllowed(true)

                        //replace((R.id.fragment_container_view, DashboardFragment.newInstance(),args = bundle)

                }
            }
        }

        tvForgotPassword.setOnClickListener {

            // forgot password
        }

        tvCreateAccount.setOnClickListener {

            // create new user account
        }

    }

    private fun validate(): Boolean {
        if(TextUtils.isEmpty(userName))
        {
            Toast.makeText(activity , requireActivity().getString(R.string.txt_error_username) , Toast.LENGTH_SHORT).show()
            return false
        }

//        else if(TextUtils.isEmpty(password))
//        {
//            Toast.makeText(activity , requireActivity().getString(R.string.txt_error_password) , Toast.LENGTH_SHORT).show()
//            return false
//        }

        return true
    }
}
