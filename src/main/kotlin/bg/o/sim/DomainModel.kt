package bg.o.sim

import org.springframework.data.annotation.Id

data class Person(
        @Id var id : String?,
        val firstName: String,
        val lastName: String
)