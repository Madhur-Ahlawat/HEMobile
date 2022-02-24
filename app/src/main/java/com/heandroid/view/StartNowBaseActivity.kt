package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.heandroid.databinding.ActivityStartNowBaseBinding
import com.heandroid.fragments.AboutServiceFragment
import com.heandroid.fragments.BaseFragment
import com.heandroid.fragments.ContactDartChargeFragment
import com.heandroid.fragments.CrossingServiceUpdateFragment
import com.heandroid.utils.Constants


class StartNowBaseActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStartNowBaseBinding
    private var showWhat: String=Constants.ABOUT_SERVICE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_now_base)

        var destFragment = AboutServiceFragment();
        setDefaultFragment(destFragment);
        setView()

        binding.idToolBarLyt.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setView() {

        showWhat = intent?.getBundleExtra(Constants.DATA)?.getString(Constants.SHOW_SCREEN).toString()
        when(showWhat)
        {
            Constants.ABOUT_SERVICE->
            {
                var frag = AboutServiceFragment()
                binding.idToolBarLyt.tvHeader.text = getString(R.string.str_about_this_service)
                setDefaultFragment(frag);
            }
            Constants.CONTACT_DART_CHARGES->{
                var frag = ContactDartChargeFragment()
                binding.idToolBarLyt.tvHeader.text = getString(R.string.str_contact_dart_charge)
                setDefaultFragment(frag);

            }
            Constants.CROSSING_SERVICE_UPDATE->{

                var frag = CrossingServiceUpdateFragment()
                binding.idToolBarLyt.tvHeader.text = getString(R.string.str_crossing_service_update)
                setDefaultFragment(frag);
            }
        }
    }

    // This method is used to set the default fragment that will be shown.
    private fun setDefaultFragment(defaultFragment: BaseFragment) {
        replaceFragment(defaultFragment)
    }

    // Replace current Fragment with the destination Fragment.
    private fun replaceFragment(destFragment: BaseFragment?) {
        // First get FragmentManager object.
        val fragmentManager: FragmentManager = this.supportFragmentManager
        // Begin Fragment transaction.
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        // Replace the layout holder with the required Fragment object.
        if (destFragment != null) {
            fragmentTransaction.replace(R.id.fragment_container, destFragment)
        }
        // Commit the Fragment replace action.
        fragmentTransaction.commit()
    }
}