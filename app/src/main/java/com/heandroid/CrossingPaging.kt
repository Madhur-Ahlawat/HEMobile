package com.heandroid

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import com.heandroid.model.crossingHistory.response.CrossingHistoryItem
import com.heandroid.network.ApiHelper
import java.lang.Exception

class CrossingPaging(private val apiHelper: ApiHelper,private var body : CrossingHistoryRequest?) : PagingSource<Int, CrossingHistoryItem>() {
    private val numberOfIndex=5
    override fun getRefreshKey(state: PagingState<Int, CrossingHistoryItem>): Int? {
       return state.anchorPosition?.let {
            val anchorPage=state?.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(numberOfIndex)?:anchorPage?.nextKey?.minus(numberOfIndex)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CrossingHistoryItem> {
        val position=params.key?:1
        body?.startIndex=position.toLong()
        body?.count=numberOfIndex?.toLong()

//        return try {
////             val crossingHistoryApi=apiHelper.crossingHistoryApiCall(body)
////             val response=crossingHistoryApi.body() as CrossingHistoryApiResponse
////             LoadResult.Page(data=response.transactionList?.transaction?.toList(),
////                            prevKey = if(position==1) null else position-numberOfIndex,
////                            nextKey = if(response.transactionList.transaction.isEmpty()) null else position+numberOfIndex)
////
//
//
//        }catch (e: Exception){
//            LoadResult.Error(e)
//        }
//

       return  LoadResult.Error(RuntimeException())
    }
}