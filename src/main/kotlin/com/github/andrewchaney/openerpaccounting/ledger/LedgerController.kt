package com.github.andrewchaney.openerpaccounting.ledger

import com.github.andrewchaney.openerpaccounting.configuration.logger
import com.github.andrewchaney.openerpaccounting.ledger.service.LedgerService
import com.github.andrewchaney.openerpaccounting.ledger.wire.LedgerEntryRequest
import com.github.andrewchaney.openerpaccounting.ledger.wire.LedgerEntryResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

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

    @GetMapping
    fun getLedgerEntryById(@RequestParam id: UUID): ResponseEntity<LedgerEntryResponse> {
        log.debug("processing get ledger entry request for entry: {}", id)

        val entry = ledgerService.getLedgerEntryById(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(entry)
    }
}
