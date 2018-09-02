package bg.o.sim.web

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

abstract class CrudApiController<T : BaseEntity>(
        private val repo: MongoRepository<T, String>
) {

    companion object {
        val test1 = Test("Test").apply { this.id = "1" }
        val test2 = Test("Test").apply { this.id = "2" }
    }

    @RequestMapping(path = ["/all"], method = [RequestMethod.GET])
    fun fetchAll(): MutableList<T> = repo.findAll()

    @RequestMapping(path = ["/get/{id}"], method = [RequestMethod.GET])
    fun fetch(@PathVariable("id") id: String): T = repo.findById(id).orElse(null)

    @RequestMapping(path = ["/update/{id}"], method = [RequestMethod.PUT])
    fun update(@RequestParam("id") id: String, @RequestBody @Valid entity: T): T =
            repo.save(entity.apply { this.id = id })

    @RequestMapping(path = ["/save"], method = [RequestMethod.POST])
    fun save(@RequestBody @Valid entity: T): T =
            repo.save(entity.apply { this.id = ObjectId.get().toString() })

    @RequestMapping(path = ["/delete/{id}"], method = [RequestMethod.DELETE])
    fun delete(@PathVariable id: String): Unit = repo.delete(repo.findById(id).orElse(null))

}

abstract class BaseEntity{
        @Id var id : String? = null
}

data class Test(val name: String) : BaseEntity()