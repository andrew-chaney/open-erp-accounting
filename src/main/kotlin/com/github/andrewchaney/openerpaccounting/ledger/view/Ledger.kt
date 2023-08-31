package com.github.andrewchaney.openerpaccounting.ledger.view

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.github.andrewchaney.openerpaccounting.ledger.model.EntryType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "ledger")
class Ledger(

    @Id
    @Column(name = "ledger_id")
    val ledgerId: UUID,

    @Column(name = "title")
    val title: String,

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    val type: EntryType,

    @Column(name = "associated_company")
    val associatedCompany: String,

    @Column(name = "amount")
    val amount: BigDecimal,

    @Column(name = "notes")
    val notes: String?,

    @Column(name = "created_ts_epoch")
    val createdTsEpoch: LocalDateTime,

    @Column(name = "updated_ts_epoch")
    val updatedTsEpoch: LocalDateTime,

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
        name = "ledger_tag",
        joinColumns = [JoinColumn(name = "ledger_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")],
    )
    val tags: Set<Tag>,
)
