package com.heandroid.utils.common

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import com.heandroid.data.model.payment.CardTypeModel
import java.util.*


open class CardNumberFormatterTextWatcher  : TextWatcher{

    private val VISA = "^4[0-9]{6,}$"
    private val MASTER_CARD = "^5[1-5][0-9]{5,}$"
    private val AMERICAN_EXPRESS = "^3[47][0-9]{5,}$";
    private val DINERS_CLAB = "^3(?:0[0-5]|[68][0-9])[0-9]{4,}$"
    private val JCB = "^(?:2131|1800|35[0-9]{3})[0-9]{3,}$";

    private val listOfPattern = listOf(CardTypeModel("VISA",VISA),
                                       CardTypeModel("MASTER_CARD",MASTER_CARD),
                                       CardTypeModel("AMERICAN_EXPRESS",AMERICAN_EXPRESS),
                                       CardTypeModel("DINERS_CLAB",DINERS_CLAB),
                                       CardTypeModel("JCB",JCB))


    private var current = ""

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun afterTextChanged(s: Editable) {
        if (s.toString() != current) {
            val userInput = s.toString().replace(nonDigits,"")
            if (userInput.length <= 16) {
                current = userInput.chunked(4).joinToString(" ")
                s.filters = arrayOfNulls<InputFilter>(0)
            }
            s.replace(0, s.length, current, 0, current.length)

            for (p  in listOfPattern) {
                if (s.toString().matches(Regex(p.pattern!!)) ) {
                    Log.e("cardype",p.type?:"")
                    break
                }
            }
        }
    }

    companion object {
        private val nonDigits = Regex("[^\\d]")
    }
}