package com.github.andrewchaney.openerpaccounting.ledger.model

import com.github.andrewchaney.openerpaccounting.ledger.view.Ledger
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class LedgerEntryResponse(ledger: Ledger) {

    val ledgerId: UUID
    val title: String
    val type: EntryType
    val associatedCompany: String
    val amount: BigDecimal
    val notes: String?
    val createdTsEpoch: LocalDateTime
    val updatedTsEpoch: LocalDateTime

    init {
        ledgerId = ledger.ledgerId
        title = ledger.title
        type = ledger.type
        associatedCompany = ledger.associatedCompany
        amount = ledger.amount
        notes = ledger.notes
        createdTsEpoch = ledger.createdTsEpoch
        updatedTsEpoch = ledger.updatedTsEpoch
    }
}
