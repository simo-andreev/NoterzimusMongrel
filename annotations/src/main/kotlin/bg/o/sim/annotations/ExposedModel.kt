package bg.o.sim.annotations
/**
 * Regrettably does not entail super models walking about au naturel.
 * Still cool tho!
 *
 * Used by the [ApiGenerator][bg.o.sim.ApiGenerator] to find candidates for `MongoDB` storage and REST API exposure
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ExposedModel(val mappingRoot: String)
