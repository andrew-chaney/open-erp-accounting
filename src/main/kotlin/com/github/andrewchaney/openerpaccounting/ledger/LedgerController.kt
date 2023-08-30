package com.github.andrewchaney.openerpaccounting.ledger

import com.github.andrewchaney.openerpaccounting.configuration.logger
import com.github.andrewchaney.openerpaccounting.ledger.model.LedgerEntryRequest
import com.github.andrewchaney.openerpaccounting.ledger.model.LedgerEntryResponse
import com.github.andrewchaney.openerpaccounting.ledger.service.LedgerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ledger")
@Validated
class LedgerController(
    private val ledgerService: LedgerService,
) {

    private val log by logger()

    @PostMapping
    fun createLedgerEntryRequest(@RequestBody request: LedgerEntryRequest): ResponseEntity<LedgerEntryResponse> {
        log.debug("processing create ledger entry request: {}", request)

        val entry = ledgerService.createLedgerEntry(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(entry)
    }
}
