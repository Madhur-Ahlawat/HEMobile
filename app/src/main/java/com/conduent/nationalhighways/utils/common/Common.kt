package com.conduent.nationalhighways.utils.common

object Common {


    @Throws(Exception::class)
     fun maskString(strText: String?, start: Int, end: Int, maskChar: Char): String {
        var start = start
        var end = end
        if (strText == null || strText == "") return ""
        if (start < 0) start = 0
        if (end > strText.length) end = strText.length
        if (start > end) throw Exception("End index cannot be greater than start index")
        val maskLength = end - start
        if (maskLength == 0) return strText
        val sbMaskString = StringBuilder(maskLength)
        for (i in 0 until maskLength) {
            sbMaskString.append(maskChar)
        }
        return (strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength))
    }

}