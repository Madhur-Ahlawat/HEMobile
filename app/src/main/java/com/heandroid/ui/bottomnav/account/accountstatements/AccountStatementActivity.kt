package com.heandroid.ui.bottomnav.account.accountstatements

import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.data.model.account.StatementListModel
import com.heandroid.databinding.ActivityAccountStatementBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.setSpinnerAdapterData
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class AccountStatementActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityAccountStatementBinding
    private val viewModel: AccountStatementViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var periodList = mutableListOf<String>()
    private var yearListCSV = mutableListOf<String>()

    override fun initViewBinding() {
        binding = ActivityAccountStatementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_account_statements)

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(this.supportFragmentManager, Constants.LOADER_DIALOG)

        viewModel.getAccountStatement()

    }

    override fun observeViewModel() {
        observe(viewModel.accountLiveData, ::handleAccountStatementResponse)
    }

    private fun handleAccountStatementResponse(resource: Resource<List<StatementListModel?>?>?) {
        try {
            loader?.dismiss()
            when (resource) {
                is Resource.Success -> {

                    val statementList = resource.data
                    if (statementList?.size!! > 0) {
                        for (element in statementList.indices) {
                            periodList.add(statementList[element]?.period.toString())

                            var statementDate = statementList[element]?.statementDate

                            val sdf = SimpleDateFormat("dd/MM/yyyy")
                            val date = sdf.parse(statementDate)

                            val outputFormat = SimpleDateFormat("yyyy")
                            val formattedDate = outputFormat.format(date)

                            yearListCSV.add(formattedDate.toString())
                        }
                        binding.spPeriodCsv.setSpinnerAdapterData(periodList)
                        binding.spPeriodCsv.onItemSelectedListener = spinnerListener
                        binding.spYearCsv.setSpinnerAdapterData(yearListCSV)
                        binding.spYearCsv.onItemSelectedListener = yearCSVListener
                        binding.spYearPdf.setSpinnerAdapterData(yearListCSV)
                        binding.spYearPdf.onItemSelectedListener = yearCSVListener
                        binding.spPeriodPdf.setSpinnerAdapterData(periodList)
                        binding.spPeriodPdf.onItemSelectedListener = spinnerListener

                        binding.spYearAccount.setSpinnerAdapterData(yearListCSV)
                        binding.spYearAccount.onItemSelectedListener = yearCSVListener

                    }
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }
        } catch (e: Exception) {
        }
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            Toast.makeText(this@AccountStatementActivity, periodList[position], Toast.LENGTH_SHORT)
                .show()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    private val yearCSVListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            Toast.makeText(this@AccountStatementActivity, yearListCSV[position], Toast.LENGTH_SHORT)
                .show()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

}
