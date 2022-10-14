package com.conduent.nationalhighways.utils


object VehicleClassTypeConverter {

    fun toClassCode(className: String?): String {
        return when (className) {
            "A" -> {
                "1"
            }
            "B" -> {
                "2"
            }
            "C" -> {
                "3"
            }
            else -> {
                return "4"
            }
        }
    }

    fun toClassName(classCode: String): String {
        return when (classCode) {
            "1" -> {
                "A"
            }
            "2" -> {
                "B"
            }
            "3" -> {
                "C"
            }
            else -> {
                "D"
            }
        }
    }

    fun toClassPrice(className: String): Double {
        return when (className) {
            "A" -> {
                0.0
            }
            "B" -> {
                2.5
            }
            "C" -> {
                3.0
            }
            else -> {
                6.0
            }
        }
    }


}