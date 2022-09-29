package com.heandroid.ui.bottomnav.account.accountstatements

import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.data.model.account.StatementListModel
import com.heandroid.databinding.ActivityAccountStatementBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.setSpinnerAdapterData
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountStatementActivity : BaseActivity<ActivityAccountStatementBinding>(), LogoutListener {

    private lateinit var binding: ActivityAccountStatementBinding
    private val viewModel: AccountStatementViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var periodList = mutableListOf<String>()
    private var yearListCSV = mutableListOf<String>()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun initViewBinding() {
        binding = ActivityAccountStatementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_account_statements)

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(this.supportFragmentManager, Constants.LOADER_DIALOG)

        viewModel.getAccountStatement()

        binding.toolBarLyt.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun observeViewModel() {
        observe(viewModel.accountLiveData, ::handleAccountStatementResponse)
    }

    private fun handleAccountStatementResponse(resource: Resource<List<StatementListModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        yearListCSV.clear()
        periodList.clear()
        yearListCSV.add("Select")
        periodList.add("Select")
        when (resource) {
            is Resource.Success -> {
                setSpinnerData()
                /*val statementList = resource.data
                if (statementList?.size!! > 0) {
                    for (element in statementList.indices) {
                        periodList.add(statementList[element]?.period.toString())

                        val statementDate = statementList[element]?.statementDate

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

                }*/

            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }

        }
    }

    private fun setSpinnerData() {
        val years = resources.getStringArray(R.array.years)
        years.forEach {
            yearListCSV.add(it)
        }
        val periods = resources.getStringArray(R.array.period)
        periods.forEach {
            periodList.add(it)
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


    private val spinnerListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//            Toast.makeText(this@AccountStatementActivity, periodList[position], Toast.LENGTH_SHORT)
//                .show()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    private val yearCSVListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//            Toast.makeText(this@AccountStatementActivity, yearListCSV[position], Toast.LENGTH_SHORT)
//                .show()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    override fun onStart() {
        super.onStart()
        loadSession()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        loadSession()
    }

    private fun loadSession() {
        LogoutUtil.stopLogoutTimer()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onLogout() {
        sessionManager.clearAll()
        Utils.sessionExpired(this)
    }

}
