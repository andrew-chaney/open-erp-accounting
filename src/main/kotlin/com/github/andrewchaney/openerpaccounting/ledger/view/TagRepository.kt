package com.github.andrewchaney.openerpaccounting.ledger.view

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TagRepository : CrudRepository<Tag, UUID> {

    fun getByName(name: String): Tag?
}
