package com.gordeevbr.revolut.web.dtos

import com.gordeevbr.revolut.exceptions.InvalidCentsException
import com.gordeevbr.revolut.exceptions.NegativeValueException
import com.gordeevbr.revolut.kotlin.NoArg
import java.math.BigDecimal

@NoArg
data class BalanceRepresentationDto(
        val decimal: Long,
        val cents: Int
) {
    fun toBigDecimal(): BigDecimal {
        if (cents < 0 || cents > 99) {
            throw InvalidCentsException(cents)
        }
        if (decimal < 0) {
            throw NegativeValueException(decimal)
        }
        val centsBigDecimal = BigDecimal(cents).movePointLeft(2)
        val decimalBigDecimal = BigDecimal(decimal)
        return centsBigDecimal.plus(decimalBigDecimal)
    }
}

fun BigDecimal.toDto(): BalanceRepresentationDto {
    val centsDecimal = this.remainder(BigDecimal.ONE).stripTrailingZeros()
    val cents = centsDecimal.movePointRight(this.scale()).toInt()
    val decimal = this.minus(centsDecimal).setScale(0).toLong()
    return BalanceRepresentationDto(decimal, cents)
}