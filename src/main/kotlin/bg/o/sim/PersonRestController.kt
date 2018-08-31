package bg.o.sim

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMapping




@RestController
@RequestMapping("persons")
class PersonRestController(@Autowired val repo: PersonRepo) {

    @RequestMapping(path = ["/all"], method = [RequestMethod.GET])
    fun findAllPeople(): MutableList<Person> = repo.findAll()

    @RequestMapping(path = ["/get/{id}"], method = [RequestMethod.GET])
    fun findPersonById(@PathVariable("id") id: ObjectId) = repo.findById(id)

    @RequestMapping(path = ["/update/{id}"], method = [RequestMethod.PUT])
    fun updatePerson(@RequestParam("id") id: ObjectId, @RequestBody @Valid person: Person) = repo.save(person.apply { this.id = id })

    @RequestMapping(path = ["/save"], method = [RequestMethod.POST])
    fun savePerson(@RequestBody @Valid person: Person) = repo.save(person.apply { this.id = ObjectId.get() })

    @RequestMapping(path = ["/delete/{id}"], method = [RequestMethod.DELETE])
    fun deletePet(@PathVariable id: ObjectId) = repo.findById(id)?.let { repo.delete(it) }

}