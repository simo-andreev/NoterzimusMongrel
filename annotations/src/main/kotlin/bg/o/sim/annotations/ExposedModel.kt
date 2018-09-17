package bg.o.sim.annotations
/**
 * Regrettably does not entail super models walking about au naturel.
 * Still cool tho!
 *
 * Used by the [ApiGenerator][bg.o.sim.aprocessor.ApiGenerator] to find candidates for
 * `MongoDB` storage and REST API exposure. To decline exposure of the CRUD calls as a web REST API,
 * set the annotation's value parameter [exposeWebApi] to `false`.
 *
 * Intended for use on classes that inherit, directly or not, from [BaseEntity][bg.o.sim.application.web.BaseEntity]
 *
 * @param exposeWebApi whether to expose the CRUD operations via web REST API. If set to `true` => the operations will
 * be made available as network REST calls. If set to `false` the CRUD API operations will be accessible only within
 * code, in the scope of the generated CRUD API. *Defaults to `true`!* [added in v0.2.0]
 *
 * @param mappingRoot the first element of all api mappings/URLs for the CRUD operations that will be mapped and exposed.
 * *Only relevant if [exposeWebApi] is set to `true`. Ignored otherwise!*
 *
 * @sample bg.o.sim.application.model.TransactionShortform
 *
 * @see bg.o.sim.aprocessor.ApiGenerator
 * @since v0.1.1
 *
 * @author Simo Andreev <github.com/simo-andreev | simeon.zlatanov.andreev@gmail.com>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ExposedModel(val mappingRoot: String, val exposeWebApi: Boolean = true)
