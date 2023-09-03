package com.github.andrewchaney.openerpaccounting.ledger.service

import com.github.andrewchaney.openerpaccounting.configuration.logger
import com.github.andrewchaney.openerpaccounting.exception.EntryNotFoundException
import com.github.andrewchaney.openerpaccounting.ledger.LedgerModelAssembler
import com.github.andrewchaney.openerpaccounting.ledger.model.EntryType
import com.github.andrewchaney.openerpaccounting.ledger.view.Ledger
import com.github.andrewchaney.openerpaccounting.ledger.view.LedgerRepository
import com.github.andrewchaney.openerpaccounting.ledger.view.Tag
import com.github.andrewchaney.openerpaccounting.ledger.view.TagRepository
import com.github.andrewchaney.openerpaccounting.ledger.view.specification.LedgerSpecification
import com.github.andrewchaney.openerpaccounting.ledger.wire.LedgerEntryRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class LedgerService(
    private val ledgerModelAssembler: LedgerModelAssembler,
    private val ledgerSpecification: LedgerSpecification,
    private val ledgerRepository: LedgerRepository,
    private val pagedResourcesAssembler: PagedResourcesAssembler<Ledger>,
    private val tagRepository: TagRepository,
) {

    private val log by logger()

    fun createLedgerEntry(request: LedgerEntryRequest): EntityModel<Ledger> {
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

        return ledgerModelAssembler.toModel(entry)
    }

    fun getLedgerEntryById(id: UUID): EntityModel<Ledger> {
        return ledgerRepository.findById(id).getOrNull()
            ?.let(ledgerModelAssembler::toModel)
            ?: throw EntryNotFoundException()
    }

    fun getAllLedgerEntries(
        type: EntryType?,
        associatedCompany: String?,
        tags: Set<String>?,
        offset: Int,
        limit: Int,
    ): PagedModel<EntityModel<Ledger>> {
        val specifications = mutableListOf<Specification<Ledger>>()

        type?.let { specifications.add(ledgerSpecification.getByEntryType(it)) }
        associatedCompany?.let { specifications.add(ledgerSpecification.getByAssociatedCompany(it)) }
        tags?.map { specifications.add(ledgerSpecification.getByTag(it)) }

        val page: Pageable = PageRequest.of(
            offset,
            limit,
            Sort.by(Sort.Direction.DESC, "createdTsEpoch")
        )

        val entries = ledgerRepository.findAll(ledgerSpecification.getBySpecList(specifications), page)

        return pagedResourcesAssembler.toModel(entries, ledgerModelAssembler)
    }

    fun deleteLedgerEntryById(id: UUID) {
        if (ledgerRepository.existsById(id)) {
            ledgerRepository.deleteById(id)
            log.debug("successfully deleted ledger entry {}", id)
        } else {
            log.debug("ledger entry {} does not exist", id)
        }
    }
}
