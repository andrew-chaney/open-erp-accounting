package com.github.andrewchaney.openerpaccounting.ledger.model

import java.math.BigDecimal

data class LedgerEntryRequest(

    val title: String,

    val type: EntryType,

    val associatedCompany: String,

    val amount: BigDecimal,

    val notes: String?,
)
