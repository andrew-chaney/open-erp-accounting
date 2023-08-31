package com.github.andrewchaney.openerpaccounting.ledger.view

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LedgerRepository : CrudRepository<Ledger, UUID>, PagingAndSortingRepository<Ledger, UUID> {

    fun findAll(specifications: Specification<Ledger>, pageable: Pageable): Page<Ledger>
}
