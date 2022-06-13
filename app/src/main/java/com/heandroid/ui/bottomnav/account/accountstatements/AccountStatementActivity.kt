package com.heandroid.ui.bottomnav.account.accountstatements

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.data.model.account.AccountStatementResponse
import com.heandroid.databinding.ActivityAccountStatementBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountStatementActivity : BaseActivity<Any?>() {

    private lateinit var binding: ActivityAccountStatementBinding
    private val viewModel: AccountStatementViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var periodList = mutableListOf<String>()

    override fun initViewBinding() {
        binding = ActivityAccountStatementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolBarLyt.titleTxt.text = getString(R.string.str_account_statements)

        binding.spPeriodCsv.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Toast.makeText(this@AccountStatementActivity, periodList[position], Toast.LENGTH_SHORT).show()
            }
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun observeViewModel() {
        observe(viewModel.accountLiveData, ::handleAccountStatementResponse)
    }

    private fun handleAccountStatementResponse(resource: Resource<AccountStatementResponse?>?) {
        try {
            loader?.dismiss()
            when (resource) {
                is Resource.Success -> {

                   val statementList = resource.data?.statementList
                   if(statementList?.size!! > 0){
                      for(element in statementList.indices){
                          periodList.add(statementList[element]?.period.toString())
                      }
                      val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periodList)
                      binding.spPeriodCsv.adapter = adapter
                   }
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
            }
        } catch (e: Exception) {
        }
    }
}
