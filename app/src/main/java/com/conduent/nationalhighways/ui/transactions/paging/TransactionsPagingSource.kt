package com.conduent.nationalhighways.ui.transactions.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryRequest
import com.conduent.nationalhighways.data.model.accountpayment.TransactionData
import com.conduent.nationalhighways.data.repository.dashboard.DashBoardRepo
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.google.gson.Gson
import retrofit2.HttpException

class TransactionsPagingSource(
    private val repository: DashBoardRepo,
) : PagingSource<Int, TransactionData>() {

    private var responseData = mutableListOf<TransactionData>()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionData> {
        return try {
            val currentPage = params.key ?: 1
            val request = AccountPaymentHistoryRequest(
                currentPage,
                Constants.ALL_TRANSACTION,
                5
            )
            val response = repository.getAccountPayment(
                request
            )
            val data = response?.body()!!.transactionList?.transaction
            data!!.forEach{
                Log.e("XJ220",it.amount.toString())
            }

            responseData?.addAll(data!!)

            LoadResult.Page(
                data = responseData,
                prevKey = null,
                nextKey = currentPage.plus(5)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }

    }


    override fun getRefreshKey(state: PagingState<Int, TransactionData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


}