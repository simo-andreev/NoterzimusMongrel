@file:Suppress("ClassName") // used as example for generated code, so naming conventions might differ

package bg.o.sim.application.model

import bg.o.sim.annotations.ExposedModel
import bg.o.sim.application.web.BaseEntity
import bg.o.sim.application.web.CrudApiController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.PostConstruct

/*
 * This is an example for adding a persistence Data Model and exposing it in a web API.
 * This file contains everything that has to be added, so that:
 *      - a 'transaction-%d' collection is created in the connected instance of MongoDb
 *      - relevant CRUD endpoints are exposed on the configured port structured as host:port/transact-%d/{crud-operation}
 */

// Extend MongoRepository<ENT, ID> because Spring can't @autowire with generics.
internal interface TransactionLongform_MongoRepo : MongoRepository<TransactionLongform, String>

// Extend CrudApiController<ENT> to get the CRUD REST request mappings.
// Annotate with @Service to allow autowiring in rest of code once.
// Annotate with @RestController to let Spring do its magic with the aforementioned REST mappings.
// Annotate with @RequestMapping to group ENT specific requests under ENT specific root, avoiding mapping collisions.
@Service
@RestController
@RequestMapping("transact_long")
class TransactionLongform_CrudApi internal constructor(@Autowired repo: TransactionLongform_MongoRepo) : CrudApiController<TransactionLongform>(repo)

// The ENT itself. This is the Kotlin object that will be JSON-ed in both web requests and MongoDB persistence.
data class TransactionLongform(
        val origin: String,
        val destination: String,
        val amount: Long
) : BaseEntity()


/*
 * The following is equivalent to the above declarations, but uses the ApiGenerator annotation processor to do the
 * code-writing _for_ you.
 */

// Annotate with @ExposedModel, to generate the above demonstrated Api interface and Repo class.
// the `mappingRoot` annotation parameter is forwarded to a @RequestMapping annotation, where it acts as explained previously.
// The annotated class is the Kotlin object that will be JSON-ed in both web requests and MongoDB persistence.
@ExposedModel("transact_short")
data class TransactionShortform(
        val origin: String,
        val destination: String,
        val amount: Long
) : BaseEntity()


// TODO [18.09.2018] - add long-form local repo example!~

/*
 * The following is equivalent to the above declarations, but exposes the CRUD operations only locally: in-code
 */

// Annotate with @ExposedModel, to generate the above demonstrated Api interface and Repo class.
// the `exposeWebApi` annotation parameter denotes this as a non-web exposed data model.
// `mappingRoot` is ignored, because when not `exposed` to web, the @RequestMapping annotation is removed.
@ExposedModel(exposeWebApi = false, mappingRoot = "transact_local_short")
data class TransactionLocalShortform(
        val origin: String,
        val destination: String,
        val amount: Long
) : BaseEntity()
