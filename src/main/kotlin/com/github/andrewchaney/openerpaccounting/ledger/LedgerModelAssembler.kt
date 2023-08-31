package com.github.andrewchaney.openerpaccounting.ledger

import com.github.andrewchaney.openerpaccounting.ledger.view.Ledger
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class LedgerModelAssembler : RepresentationModelAssembler<Ledger, EntityModel<Ledger>> {

    override fun toModel(entity: Ledger): EntityModel<Ledger> {
        return EntityModel.of(
            entity,
            linkTo<LedgerController> { getLedgerEntryById(entity.ledgerId) }.withSelfRel()
        )
    }
}
