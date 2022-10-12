package com.nextgentrainer.java.utils

class QualityFeature(var name: String, var isValid: Boolean, var decisionBase: List<*>?) {

    override fun toString(): String {
        return "{" +
            "\"name\":" + "\"" + name + "\"" +
            ", \"isValid\":" + isValid +
            // ", \"decisionBase\": " + decisionBase.toString()
            // + narazie wykomentowane, ponieważ eksplozja rozmiaru, powinno lecieć do bazki
            "}"
    }
}
