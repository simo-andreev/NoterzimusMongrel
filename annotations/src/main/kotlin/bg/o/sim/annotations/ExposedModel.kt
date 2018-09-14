package bg.o.sim.annotations
/**
 * Regrettably does not entail super models walking about au naturel.
 * Still cool tho!
 *
 * Used by the [ApiGenerator][bg.o.sim.aprocessor.ApiGenerator] to find candidates for
 * `MongoDB` storage and REST API exposure.
 *
 * Intended for use on classes that inherit, directly or not, from [BaseEntity][bg.o.sim.application.web.BaseEntity]
 *
 * @param mappingRoot the first element of all api mappings/URLs for the CRUD operations that will be mapped and exposed
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
annotation class ExposedModel(val mappingRoot: String)
