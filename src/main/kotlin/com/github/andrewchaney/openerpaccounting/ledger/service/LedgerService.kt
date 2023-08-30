package com.github.andrewchaney.openerpaccounting.ledger.service

import com.github.andrewchaney.openerpaccounting.configuration.logger
import com.github.andrewchaney.openerpaccounting.ledger.model.LedgerEntryRequest
import com.github.andrewchaney.openerpaccounting.ledger.model.LedgerEntryResponse
import com.github.andrewchaney.openerpaccounting.ledger.view.Ledger
import com.github.andrewchaney.openerpaccounting.ledger.view.LedgerRepository
import com.github.andrewchaney.openerpaccounting.ledger.view.Tag
import com.github.andrewchaney.openerpaccounting.ledger.view.TagRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class LedgerService(
    private val ledgerRepository: LedgerRepository,
    private val tagRepository: TagRepository,
) {

    private val log by logger()

    fun createLedgerEntry(request: LedgerEntryRequest): LedgerEntryResponse {
        log.debug("creating ledger entry: {}", request)

        // Get/Save the tags first
        val tags: Set<Tag> = request.tags
            ?.map { tag ->
                // Not a huge fan of this implementation. A lot of time is wasted with repeated queries.
                // Should definitely be refactored to conduct a bulk query and bulk persist of the new tags.
                tagRepository.getByName(tag)
                    ?: tagRepository.save(
                        Tag(
                            tagId = UUID.randomUUID(),
                            name = tag,
                            ledgerEntries = emptySet(),
                        )
                    )
            }
            ?.toSet()
            ?: emptySet()


        val entry = ledgerRepository.save(
            Ledger(
                ledgerId = UUID.randomUUID(),
                title = request.title,
                type = request.type,
                associatedCompany = request.associatedCompany,
                amount = request.amount,
                notes = request.notes,
                tags = tags,
                createdTsEpoch = LocalDateTime.now(),
                updatedTsEpoch = LocalDateTime.now(),
            )
        )

        log.debug("entry successfully persisted: {}", entry)

        return LedgerEntryResponse(entry)
    }
}
