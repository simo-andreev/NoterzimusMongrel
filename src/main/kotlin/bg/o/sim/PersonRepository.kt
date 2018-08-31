package bg.o.sim

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

//@RepositoryRestResource(collectionResourceRel = "people", path = "people")
interface PersonRepo : MongoRepository<Person, String> {
    fun findById(id: ObjectId): Person?
    fun findByLastName(lastName: String): List<Person>

}