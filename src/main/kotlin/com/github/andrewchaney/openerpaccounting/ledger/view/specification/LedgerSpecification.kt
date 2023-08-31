package com.github.andrewchaney.openerpaccounting.ledger.view.specification

import com.github.andrewchaney.openerpaccounting.ledger.model.EntryType
import com.github.andrewchaney.openerpaccounting.ledger.view.Ledger
import com.github.andrewchaney.openerpaccounting.ledger.view.Tag
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Root
import jakarta.persistence.criteria.Subquery
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class LedgerSpecification {

    fun getByEntryType(type: EntryType): Specification<Ledger> {
        return Specification<Ledger> { root, _, builder ->
            builder.equal(root.get<EntryType>(Ledger::type.name), type)
        }
    }

    fun getByAssociatedCompany(company: String): Specification<Ledger> {
        return Specification<Ledger> { root, _, builder ->
            builder.equal(root.get<String>("associatedCompany"), company)
        }
    }

    fun getByTag(searchTag: String): Specification<Ledger> {
        return Specification<Ledger> { root, query, builder ->
            // Messy, but better than a cartesian product
            query.distinct(true)
            val tagSubQuery: Subquery<Tag> = query.subquery(Tag::class.java)
            val tag: Root<Tag> = tagSubQuery.from(Tag::class.java)
            val tagEntry: Expression<Collection<Tag>> = tag.get("ledgerEntries")
            tagSubQuery.select(tag)
            tagSubQuery.where(builder.equal(tag.get<String>("name"), searchTag), builder.isMember(root, tagEntry))
            builder.exists(tagSubQuery)
        }
    }

    fun getBySpecList(specs: List<Specification<Ledger>>): Specification<Ledger> {
        return Specification.allOf(specs)
    }
}
