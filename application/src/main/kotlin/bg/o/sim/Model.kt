package bg.o.sim

import bg.o.sim.annotations.ExposedModel
import bg.o.sim.web.BaseEntity

@ExposedModel("mdl")
data class Model(val counter: Int, val post : String):BaseEntity()