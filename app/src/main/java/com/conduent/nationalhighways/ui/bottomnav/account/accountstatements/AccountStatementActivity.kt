package com.conduent.nationalhighways.ui.bottomnav.account.accountstatements

import android.util.ArraySet
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.StatementListModel
import com.conduent.nationalhighways.data.model.account.ViewStatementsReqModel
import com.conduent.nationalhighways.data.model.contactdartcharge.CreateNewCaseReq
import com.conduent.nationalhighways.data.model.contactdartcharge.CreateNewCaseResp
import com.conduent.nationalhighways.data.remote.ApiService
import com.conduent.nationalhighways.databinding.ActivityAccountStatementBinding
import com.conduent.nationalhighways.ui.base.BaseActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.startNow.contactdartcharge.ContactDartChargeViewModel
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.setSpinnerAdapterData
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.logout.LogoutListener
import com.conduent.nationalhighways.utils.logout.LogoutUtil
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class AccountStatementActivity : BaseActivity<ActivityAccountStatementBinding>(), LogoutListener {

    private lateinit var binding: ActivityAccountStatementBinding
    private val viewModel: AccountStatementViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var periodList = mutableListOf<String>()
    private var yearListCSV = mutableListOf<String>()
    private val viewModelCase: ContactDartChargeViewModel by viewModels()
    private var mCsv = -1


    @Inject
    lateinit var api: ApiService
    @Inject
    lateinit var sessionManager: SessionManager

    private var mPeriodsCsv = " "
    private var mPeriodsPdf = " "
    private var mAnnualYear = " "
    private var mYearPdf = " "
    private var mYearCsv = " "
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

        binding.submitAccount.setOnClickListener {
            val mPeriodYear = "year: $mYearCsv"

            val newCaseReq = CreateNewCaseReq(
                "",
                "",
                "",
                "",
                "",
                mPeriodYear,
                "REQUEST AN ANNUAL STATEMENT",
                "ACCOUNT MANAGEMENT",
                null,
                "ENU"
            )
            if (statementList.isNotEmpty()) {
                loader?.show(supportFragmentManager, "Loader")
                viewModelCase.createNewCase(newCaseReq)
                binding.submitAccount.isEnabled = false
                mCsv = 1
            }

        }
        binding.downloadCsv.setOnClickListener {

            val mPeriodYear = "year: $mYearCsv, Month: $mPeriodsCsv"
            val newCaseReq = CreateNewCaseReq(
                "",
                "",
                "",
                "",
                "",
                mPeriodYear,
                "REQUEST A MONTHLY STATEMENT",
                "ACCOUNT MANAGEMENT",
                null,
                "ENU"
            )

            if (statementList.isNotEmpty()) {
                loader?.show(supportFragmentManager, "Loader")
                viewModelCase.createNewCase(newCaseReq)
                binding.downloadCsv.isEnabled = false
                mCsv = 0
            }

        }

        binding.downloadPdf.setOnClickListener {
            if (statementList.isNotEmpty()) {
                loader?.show(supportFragmentManager, "Loader")
                viewModel.viewStatements(ViewStatementsReqModel(mPdfFile))
                binding.downloadPdf.isEnabled = false
            }

        }
        binding.spPeriodCsv.onItemSelectedListener = spinnerCsvListener
        binding.spYearCsv.onItemSelectedListener = yearCSVListener
        binding.spYearPdf.onItemSelectedListener = yearPdfListener
        binding.spPeriodPdf.onItemSelectedListener = spinnersPdfListener
        binding.spYearAccount.onItemSelectedListener = yearListener


    }


    override fun observeViewModel() {
        observe(viewModel.accountLiveData, ::handleAccountStatementResponse)
        observe(viewModelCase.createNewCaseVal, ::createNewCase)
        observe(viewModel.viewStatementsLiveData, ::viewStatements)

    }

    private fun viewStatements(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()

        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    ErrorUtil.showError(binding.root, "downloaded successfully")

                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }

    }

    private fun createNewCase(resource: Resource<CreateNewCaseResp?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (it.statusCode == "0") {
                        Logg.logging(
                            "testing",
                            "createNewCase $mCsv "
                        )

                        if (mCsv == 0) {
                            mCsv = -1
                            binding.caseCsvResRl.visible()
                            binding.respCsvCaseTv.text =
                                getString(R.string.str_request_subimmited_txt, it.srNumber)
                            Logg.logging(
                                "testing",
                                "createNewCase $mCsv  it.srNumber ${it.srNumber}"
                            )

                        } else if (mCsv == 1) {
                            mCsv = -1
                            binding.annualResRl.visible()
                            binding.respAnnualCaseTv.text =
                                getString(R.string.str_annual_request_subimmited_txt, it.srNumber)

                            Logg.logging(
                                "testing",
                                "createNewCase else $mCsv  it.srNumber ${it.srNumber}"
                            )

                        }

                    } else {
                        ErrorUtil.showError(binding.root, "Something went wrong. Try again later")
                    }

                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }


    private var statementList = ArrayList<StatementListModel>()
    private val mSet = ArraySet<String>()
    private val mPeriodsSet = ArraySet<String>()
    private var mPdfFile = " "
    private fun handleAccountStatementResponse(resource: Resource<List<StatementListModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        yearListCSV.clear()
        periodList.clear()
        when (resource) {
            is Resource.Success -> {
//                setSpinnerData()
                statementList.clear()

                // val statementList = resource.data
                val mTempList = ArrayList(resource.data)
                statementList.addAll(mTempList as ArrayList<StatementListModel>)
                Logg.logging("Testing", " statementList $statementList ")
                if (statementList.size > 0) {
                    for (element in statementList.indices) {
                        val statementDate = statementList[element]?.statementDate
                        mPdfFile = statementList[element].pdfName!!
                        if (statementDate == null) {
                            val mPeriodsDate = statementList[element]?.period?.split("-")
                            Logg.logging(
                                "testing",
                                "Date Utils mPeriodsDate size ${mPeriodsDate!!.size} "
                            )
                            Logg.logging("testing", "Date Utils mPeriodsDate $mPeriodsDate ")
                            val sdf = SimpleDateFormat("MM/dd/yyyy")
                            val mFromDate = sdf.parse(mPeriodsDate!![0])
                            val mToDate = sdf.parse(mPeriodsDate!![1])
                            val outputFormat = SimpleDateFormat("yyyy")
                            val formattedDate = outputFormat.format(mFromDate)
                            mSet.add(formattedDate.toString())


                        } else {
                            val sdf = SimpleDateFormat("dd/MM/yyyy")
                            val date = sdf.parse(statementDate)
                            val outputFormat = SimpleDateFormat("yyyy")
                            val formattedDate = outputFormat.format(date)
                            mSet.add(formattedDate.toString())
                        }

                    }
                    yearListCSV.addAll(mSet)

                    binding.spYearCsv.setSpinnerAdapterData(yearListCSV)
                    binding.spYearPdf.setSpinnerAdapterData(yearListCSV)

                    binding.spYearAccount.setSpinnerAdapterData(yearListCSV)


                }else{
                    ErrorUtil.showError(binding.root, "Statements not found")

                }

            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }

        }
    }

    private val spinnerCsvListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            Toast.makeText(this@AccountStatementActivity, periodList[position], Toast.LENGTH_SHORT)
                .show()
//            mPeriodsCsv = yearListCSV[position]
            mPeriodsCsv = parent.getItemAtPosition(position).toString()//yearListCSV[position]

            binding.selectCsvPeriodsTxt.gone()

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }
    private val spinnersPdfListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//            Toast.makeText(this@AccountStatementActivity, periodList[position], Toast.LENGTH_SHORT)
//                .show()
            mPeriodsPdf = parent.getItemAtPosition(position).toString()//yearListCSV[position]

            binding.selectPdfPeriodsTxt.gone()

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    private val yearCSVListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

//            val mYear =  yearListCSV[position]
            val mYear = parent.getItemAtPosition(position).toString()//yearListCSV[position]
            binding.downloadCsv.isEnabled = true

            mYearCsv = mYear
            mPeriodsSet.clear()
            binding.selectCsvYearTxt.gone()

            statementList.forEach {
                val mPeriodsDate = it.period?.split("-")

                val sdf = SimpleDateFormat("MM/dd/yyyy")
                val mFromDate = sdf.parse(mPeriodsDate!![0])
                val mToDate = sdf.parse(mPeriodsDate!![1])
                val outputFormat = SimpleDateFormat("yyyy")
                val formattedYear = outputFormat.format(mFromDate)
                if (mYear == formattedYear) {
                    val mFromDateConvert =
                        DateUtils.convertMonthNameAndDateFormat(mPeriodsDate!![0])
                    val mToDateConvert =
                        DateUtils.convertMonthNameAndDateFormat(mPeriodsDate[1])

                    Logg.logging(
                        "testing",
                        "Date Utils mFromDateConvert $mFromDateConvert "
                    )
                    Logg.logging(
                        "testing",
                        "Date Utils mToDateConvert $mToDateConvert "
                    )

                    mPeriodsSet.add("$mFromDateConvert - $mToDateConvert")

                    Logg.logging(
                        "testing",
                        "Date Utils mPeriodsSet $mPeriodsSet "
                    )

                }


            }
            periodList.clear()
            periodList.addAll(mPeriodsSet)
            Logg.logging(
                "testing",
                "Date Utils periodList $periodList "
            )
            Logg.logging(
                "testing",
                "Date Utils view?.id ${view?.id} "
            )
            Logg.logging(
                "testing",
                "Date Utils id $id "
            )
            binding.spPeriodCsv.setSpinnerAdapterData(periodList)


            Toast.makeText(this@AccountStatementActivity, yearListCSV[position], Toast.LENGTH_SHORT)
                .show()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }
    private val yearPdfListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

//            val mYear = yearListCSV[position]
            val mYear = parent.getItemAtPosition(position).toString()//yearListCSV[position]
            binding.downloadPdf.isEnabled = true

            mYearPdf = mYear

            mPeriodsSet.clear()
            binding.selectPdfYearTxt.gone()
            statementList.forEach {
                val mPeriodsDate = it.period?.split("-")

                val sdf = SimpleDateFormat("MM/dd/yyyy")
                val mFromDate = sdf.parse(mPeriodsDate!![0])
                val mToDate = sdf.parse(mPeriodsDate!![1])
                val outputFormat = SimpleDateFormat("yyyy")
                val formattedYear = outputFormat.format(mFromDate)
                if (mYear == formattedYear) {
                    val mFromDateConvert =
                        DateUtils.convertMonthNameAndDateFormat(mPeriodsDate!![0])
                    val mToDateConvert =
                        DateUtils.convertMonthNameAndDateFormat(mPeriodsDate[1])

                    Logg.logging(
                        "testing",
                        "Date Utils mFromDateConvert $mFromDateConvert "
                    )
                    Logg.logging(
                        "testing",
                        "Date Utils mToDateConvert $mToDateConvert "
                    )

                    mPeriodsSet.add("$mFromDateConvert - $mToDateConvert")

                    Logg.logging(
                        "testing",
                        "Date Utils mPeriodsSet $mPeriodsSet "
                    )

                }
            }
            periodList.clear()
            periodList.addAll(mPeriodsSet)
            Logg.logging(
                "testing",
                "Date Utils periodList $periodList "
            )
            Logg.logging(
                "testing",
                "Date Utils view?.id ${view?.id} "
            )
            Logg.logging(
                "testing",
                "Date Utils id $id "
            )

            binding.spPeriodPdf.setSpinnerAdapterData(periodList)

            Toast.makeText(this@AccountStatementActivity, yearListCSV[position], Toast.LENGTH_SHORT)
                .show()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }
    private val yearListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            mAnnualYear = parent.getItemAtPosition(position).toString()//yearListCSV[position]
            binding.submitAccount.isEnabled = true

            binding.selectAnnualTxt.gone()
            Toast.makeText(this@AccountStatementActivity, yearListCSV[position], Toast.LENGTH_SHORT)
                .show()
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
        LogoutUtil.stopLogoutTimer()
        sessionManager.clearAll()
        Utils.sessionExpired(this, this, sessionManager,api)
    }

    override fun onDestroy() {
        LogoutUtil.stopLogoutTimer()
        super.onDestroy()
    }

}
