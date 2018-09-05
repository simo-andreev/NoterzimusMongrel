package bg.o.sim.annotations
/**
 * Regrettably does not entail super models walking about au naturel.
 * Still cool tho!
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ExposedModel(val mappingRoot: String)
