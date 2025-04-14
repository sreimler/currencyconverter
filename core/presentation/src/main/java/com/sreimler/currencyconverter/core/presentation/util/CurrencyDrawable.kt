package com.sreimler.currencyconverter.core.presentation.util

import com.sreimler.currencyconverter.core.presentation.R

/**
 * Maps currency codes to drawable resource IDs.
 *
 * @param code The currency code (e.g., "USD", "EUR").
 * @return The corresponding drawable resource ID for the currency flag.
 */
fun getDrawableIdForCurrency(code: String): Int {
    return when (code) {
        "AUD" -> R.drawable.au
        "BGN" -> R.drawable.bg
        "BRL" -> R.drawable.br
        "CAD" -> R.drawable.ca
        "CHF" -> R.drawable.ch
        "CNY" -> R.drawable.cn
        "CZK" -> R.drawable.cz
        "DKK" -> R.drawable.dk
        "EUR" -> R.drawable.eu
        "GBP" -> R.drawable.gb
        "HKD" -> R.drawable.hk
        "HRK" -> R.drawable.hr
        "HUF" -> R.drawable.hu
        "IDR" -> R.drawable.id
        "ILS" -> R.drawable.il
        "INR" -> R.drawable.`in`
        "ISK" -> R.drawable.`is`
        "JPY" -> R.drawable.jp
        "KRW" -> R.drawable.kr
        "MXN" -> R.drawable.mx
        "MYR" -> R.drawable.my
        "NOK" -> R.drawable.no
        "NZD" -> R.drawable.nz
        "PHP" -> R.drawable.ph
        "PLN" -> R.drawable.pl
        "RON" -> R.drawable.ro
        "RUB" -> R.drawable.ru
        "SEK" -> R.drawable.se
        "SGD" -> R.drawable.sg
        "THB" -> R.drawable.th
        "TRY" -> R.drawable.tr
        "USD" -> R.drawable.us
        "ZAR" -> R.drawable.za
        else -> R.drawable.question_sign
    }
}