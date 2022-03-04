package com.heandroid.ui.nominatedcontacts

import com.heandroid.data.model.nominatedcontacts.SecondaryAccountData

interface NominatedContactListener {

    fun onItemClick(type: String, data: SecondaryAccountData,pos:Int,isExpanded:Boolean)

}