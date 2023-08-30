package com.github.andrewchaney.openerpaccounting.ledger.service

import com.github.andrewchaney.openerpaccounting.configuration.logger
import com.github.andrewchaney.openerpaccounting.ledger.model.LedgerEntryRequest
import com.github.andrewchaney.openerpaccounting.ledger.model.LedgerEntryResponse
import com.github.andrewchaney.openerpaccounting.ledger.view.Ledger
import com.github.andrewchaney.openerpaccounting.ledger.view.LedgerRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class LedgerService(
    private val ledgerRepository: LedgerRepository,
) {

    private val log by logger()

    fun createLedgerEntry(request: LedgerEntryRequest): LedgerEntryResponse {
        log.debug("creating ledger entry: {}", request)

        val entry = ledgerRepository.save(
            Ledger(
                ledgerId = UUID.randomUUID(),
                title = request.title,
                type = request.type,
                associatedCompany = request.associatedCompany,
                amount = request.amount,
                notes = request.notes,
                createdTsEpoch = LocalDateTime.now(),
                updatedTsEpoch = LocalDateTime.now(),
            )
        )

        log.debug("entry successfully persisted: {}", entry)

        return LedgerEntryResponse(entry)
    }
}
