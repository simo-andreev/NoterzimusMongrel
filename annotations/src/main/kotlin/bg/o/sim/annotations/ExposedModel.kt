package bg.o.sim.annotations
/**
 * Regrettably does not entail super models walking about au naturel.
 * Still cool tho! Will share the detail if and when I manage to coax the thing into working.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ExposedModel(val mappingRoot: String)
