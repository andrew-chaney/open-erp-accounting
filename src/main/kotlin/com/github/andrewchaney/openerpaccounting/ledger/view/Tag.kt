package com.github.andrewchaney.openerpaccounting.ledger.view

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "tag")
class Tag(

    @Id
    @Column(name = "tag_id")
    val tagId: UUID,

    @Column(name = "name")
    val name: String,

    @ManyToMany(mappedBy = "tags")
    val ledgerEntries: Set<Ledger>,
)
