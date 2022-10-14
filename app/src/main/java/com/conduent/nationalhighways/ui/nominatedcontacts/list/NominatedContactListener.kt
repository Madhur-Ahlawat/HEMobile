package com.conduent.nationalhighways.ui.nominatedcontacts.list

import com.conduent.nationalhighways.data.model.nominatedcontacts.SecondaryAccountData

interface NominatedContactListener {

    fun onItemClick(type: String, data: SecondaryAccountData,pos:Int,isExpanded:Boolean)

}