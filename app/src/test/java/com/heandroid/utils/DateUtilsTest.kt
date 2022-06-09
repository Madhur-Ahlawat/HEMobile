package com.heandroid.utils

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DateUtilsTest {

    private lateinit var dateUtils: DateUtils

    @Before
    fun init() {
        dateUtils = DateUtils
    }

    @Test
    fun `test date range`() {
        dateUtils.calculateDays("01/30/2022", "01/20/2020")
        assertEquals("","")
    }

}