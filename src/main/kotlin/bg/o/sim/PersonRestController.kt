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
    fun fetchAllPerson(): MutableList<Person> = repo.findAll()

    @RequestMapping(path = ["/get/{id}"], method = [RequestMethod.GET])
    fun fetchPerson(@PathVariable("id") id: String) = repo.findById(id)

    @RequestMapping(path = ["/update/{id}"], method = [RequestMethod.PUT])
    fun updatePerson(@RequestParam("id") id: String, @RequestBody @Valid person: Person) = repo.save(person.apply { this.id = id })

    @RequestMapping(path = ["/save"], method = [RequestMethod.POST])
    fun savePerson(@RequestBody @Valid person: Person) = repo.save(person.apply { this.id = ObjectId.get().toString() })

    @RequestMapping(path = ["/delete/{id}"], method = [RequestMethod.DELETE])
    fun deletePerson(@PathVariable id: String) = repo.delete(repo.findById(id).orElse(null))

}