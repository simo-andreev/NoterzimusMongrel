package bg.o.sim.model

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


interface TransactRepo : MongoRepository<Transaction, String>

@RestController
@RequestMapping("transact")
class TransactionApi(@Autowired repo: TransactRepo) : CrudApiController<Transaction>(repo)


data class Transaction(
        val origin_id: String,
        val destination_id: String,
        val amount: Long
) : BaseEntity()

