package com.github.andrewchaney.openerpaccounting.ledger

import com.github.andrewchaney.openerpaccounting.configuration.logger
import com.github.andrewchaney.openerpaccounting.ledger.model.EntryType
import com.github.andrewchaney.openerpaccounting.ledger.service.LedgerService
import com.github.andrewchaney.openerpaccounting.ledger.view.Ledger
import com.github.andrewchaney.openerpaccounting.ledger.wire.LedgerEntryRequest
import jakarta.validation.constraints.Min
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
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
    fun createLedgerEntryRequest(@RequestBody request: LedgerEntryRequest): ResponseEntity<EntityModel<Ledger>> {
        log.debug("processing create ledger entry request: {}", request)

        val entry = ledgerService.createLedgerEntry(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(entry)
    }

    @GetMapping("/{id}")
    fun getLedgerEntryById(@PathVariable id: UUID): ResponseEntity<EntityModel<Ledger>> {
        log.debug("processing get ledger entry request for entry: {}", id)

        val entry = ledgerService.getLedgerEntryById(id)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(entry)
    }

    @GetMapping
    fun getAllLedgerEntries(
        @RequestParam(name = "type", required = false) type: EntryType?,
        @RequestParam(name = "associatedCompany", required = false) associatedCompany: String?,
        @RequestParam(name = "tags", required = false) tags: Set<String>?,
        @RequestParam(name = "offset", defaultValue = "0", required = false) @Min(0) offset: Int,
        @RequestParam(name = "limit", defaultValue = "100", required = false) @Min(1) limit: Int,
    ): ResponseEntity<PagedModel<EntityModel<Ledger>>> {
        log.debug(
            "processing get all request with params: [type: {}, associatedCompany: {}, tags: {}, offset: {}, limit: {}]",
            type,
            associatedCompany,
            tags,
            offset,
            limit,
        )

        val entries = ledgerService.getAllLedgerEntries(
            type,
            associatedCompany,
            tags,
            offset,
            limit,
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(entries)
    }

    @DeleteMapping("/{id}")
    fun deleteLedgerEntryById(@PathVariable id: UUID): ResponseEntity<Any> {
        log.debug("processing delete entry request for entry {}", id)

        ledgerService.deleteLedgerEntryById(id)

        return ResponseEntity
            .noContent()
            .build()
    }

    @PutMapping("/{id}")
    fun updateLedgerEntryById(
        @PathVariable id: UUID,
        @RequestBody request: LedgerEntryRequest,
    ): ResponseEntity<EntityModel<Ledger>> {
        log.debug("processing put request {} for entry {}", request, id)

        val entry = ledgerService.updateLedgerEntryById(id, request)

        return ResponseEntity
            .ok()
            .body(entry)
    }
}
