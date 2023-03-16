package com.conduent.nationalhighways.utils.widgets

import android.content.Context
import android.util.AttributeSet
import com.conduent.nationalhighways.data.model.address.DataAddress

class NHAddressCellm @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    NHTextCell(context, attrs, defStyleAttr){
    var address:DataAddress?=null

        init {
          if (address!=null){
              text="${address?.country} \n${address?.town}"
          }
        }

}