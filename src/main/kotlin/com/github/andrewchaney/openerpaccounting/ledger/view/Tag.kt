package com.github.andrewchaney.openerpaccounting.ledger.view

import com.fasterxml.jackson.annotation.JsonBackReference
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

    @JsonBackReference
    @ManyToMany(mappedBy = "tags")
    val ledgerEntries: Set<Ledger>,
)
