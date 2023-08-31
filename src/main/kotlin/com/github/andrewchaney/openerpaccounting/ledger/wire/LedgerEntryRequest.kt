package com.github.andrewchaney.openerpaccounting.ledger.wire

import com.github.andrewchaney.openerpaccounting.ledger.model.EntryType
import java.math.BigDecimal

data class LedgerEntryRequest(

    val title: String,

    val type: EntryType,

    val associatedCompany: String,

    val amount: BigDecimal,

    val notes: String?,

    val tags: Set<String>?,
)
