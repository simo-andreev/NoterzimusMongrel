package bg.o.sim.application.web

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Contains an ID member that will be used by the underlining MongoDB instance for indexing, fetching and identifying records.
 *
 * All items that are to be persisted *MUST* extend (directly or not) from BaseEntity!
 *
 * That also applies to *ALL* classes annotated with [ExposedModel][bg.o.sim.annotations.ExposedModel]!
 *
 * @since v0.1.0
 *
 * @author Simo Andreev <github.com/simo-andreev | simeon.zlatanov.andreev@gmail.com>
 */
abstract class BaseEntity{
    @Id var id : String? = null
}

/**
 * Provides default mappings for CRUD methods for a generic type [T].
 *
 * The REST API is propped-up by Spring-Starter-web's dark magics. The CRUD operations on the underling DB are done using
 * the [MongoDB repo][repo].
 *
 * @param T  [BaseEntity][bg.o.sim.application.web.BaseEntity] subclass that is to be persisted and exposed in a REST API
 * @param repo  an instance of [MongoRepository][org.springframework.data.mongodb.repository.MongoRepository] that handles
 * operations on the database. Can be [autowired][org.springframework.beans.factory.annotation.Autowired]
 * if extended to eliminate generics from declaration.
 *
 * For instance in:
 * ```kotlin
 * class SampleRepo extends MongoRepository<SampleEnt, String>
 * ```
 * `SampleRepo` would then be autowire-able.
 *
 * @since v0.1.0
 *
 * @author Simo Andreev <github.com/simo-andreev | simeon.zlatanov.andreev@gmail.com>
 */
abstract class CrudApiController<T : BaseEntity>(
        private val repo: MongoRepository<T, String>
) {

    /**
     * Retrieves all records of type [T]
     * @see org.springframework.data.mongodb.repository.MongoRepository.findAll
     */
    @RequestMapping(path = ["/all"], method = [RequestMethod.GET])
    fun fetchAll(): MutableList<T> = repo.findAll()

    /**
     * Retrieves up-to one record of type [T] that matches the passed [id]
     *
     * @return JSON representation of record of type [T] or `null` if no record matches [id]
     * @see org.springframework.data.repository.CrudRepository.findById
     */
    @RequestMapping(path = ["/get/{id}"], method = [RequestMethod.GET])
    fun fetch(@PathVariable("id") id: String): T = repo.findById(id).orElse(null)

    /**
     * Adds or changes data associated with the record matching passed [id].
     *
     * @return JSON representation of record of type [T] that was updated.
     * @see org.springframework.data.repository.CrudRepository.save
     */
    @RequestMapping(path = ["/update/{id}"], method = [RequestMethod.PUT])
    fun update(@RequestParam("id") id: String, @RequestBody @Valid entity: T): T =
            repo.save(entity.apply { this.id = id })

    /**
     * Inserts a new record from passed [data][entity] and generates an ID for it.
     *
     * @return JSON representation of record of type [T] that was created.
     * @see org.springframework.data.repository.CrudRepository.save
     * @see org.bson.types.ObjectId.get
     */
    @RequestMapping(path = ["/save"], method = [RequestMethod.POST])
    fun save(@RequestBody @Valid entity: T): T =
            repo.save(entity.apply { this.id = ObjectId.get().toString() })

    /**
     * Deletes any records that match the passed [id]
     * @see org.springframework.data.repository.CrudRepository.delete
     */
    @RequestMapping(path = ["/delete/{id}"], method = [RequestMethod.DELETE])
    fun delete(@PathVariable id: String): Unit = repo.delete(repo.findById(id).orElse(null))

}