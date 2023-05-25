package com.conduent.nationalhighways.ui.account.creation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.databinding.FragmentManualAddressBinding
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.setSpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ManualAddressFragment :  BaseFragment<FragmentManualAddressBinding>(),
View.OnClickListener {

    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesList: MutableList<String> = ArrayList()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentManualAddressBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        viewModel.getCountries()
    }

    override fun observer() {
        observe(viewModel.countriesList, ::getCountriesList)
    }

    override fun onClick(v: View?) {
    }

    private fun getCountriesList(response: Resource<List<CountriesModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                countriesList.clear()
                countriesList.add(0, getString(R.string.select_country))
                response.data?.forEach {
                    it?.countryName?.let { it1 -> countriesList.add(it1) }
                }
                binding.apply {
                    country.dataSet.addAll(countriesList)
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }
            else -> {
            }
        }
    }

}