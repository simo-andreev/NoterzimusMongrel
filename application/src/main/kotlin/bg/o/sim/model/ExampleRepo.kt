package bg.o.sim.model

import bg.o.sim.annotations.ExposedModel
import bg.o.sim.web.BaseEntity
import bg.o.sim.web.CrudApiController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/*
 * This is an example for adding a persistence Data Model and exposing it in a web API.
 * This file contains everything that has to be added, so that:
 *      - a 'transaction-%d' collection is created in the connected instance of MongoDb
 *      - relevant CRUD endpoints are exposed on the configured port structured as host:port/transact-%d/{crud-operation}
 */


// Extend MongoRepository<ENT, ID> because Spring can't @autowire with generics.
interface TransactRepo : MongoRepository<TransactionLongform, String>

// Extend CrudApiController<ENT> to get the CRUD REST request mappings.
// Annotate with @RestController to let Spring do its magic with the aforementioned REST mappings.
// Annotate with @RequestMapping to group ENT specific requests under ENT specific root, avoiding mapping collisions.
@RestController
@RequestMapping("transact_long")
class TransactionApi(@Autowired repo: TransactRepo) : CrudApiController<TransactionLongform>(repo)

// The ENT itself. This is the Kotlin object that will be JSON-ed in both web requests and MongoDB persistence.
data class TransactionLongform(
        val origin_id: String,
        val destination_id: String,
        val amount: Long
) : BaseEntity()


/*
 * The following is equivalent to the above declarations, but uses the ApiGenerator annotation processor to do the
 * code-writing _for_ you.
 */

// Annotate with @ExposedModel, to generate the above demonstrated Api interface and Repo class.
// the annotation parameter is forwarded to a @RequestMapping annotation, where it acts as explained previously.
// The annotated class is the Kotlin object that will be JSON-ed in both web requests and MongoDB persistence.
@ExposedModel("transact_short")
data class TransactionShortform(
        val origin_id: String,
        val destination_id: String,
        val amount: Long
) : BaseEntity()
