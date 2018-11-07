package bg.o.sim.application.model

import bg.o.sim.annotations.ExposedModel
import bg.o.sim.application.web.BaseEntity

@ExposedModel("notes")
data class Note(
        var title: String,
        var content: String
) :  BaseEntity()