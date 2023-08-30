package com.github.andrewchaney.openerpaccounting.ledger.view

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LedgerRepository : CrudRepository<Ledger, UUID>
