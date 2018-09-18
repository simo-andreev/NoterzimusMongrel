package bg.o.sim.aprocessor

import bg.o.sim.annotations.ExposedModel
import com.google.auto.service.AutoService
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Creates files parsable by Spring Boot Starter's -data-rest, -data-mongodb-reactive and -web packages.
 * Spring then takes said generated classes and props-up a MongoDb CRUD controller and corresponding REST APIs.
 *
 *
 * To be used on [BaseEntity][bg.o.sim.application.web.BaseEntity]. If annotated element is _not_ a subclass
 * of BaseEntity, compilation will fail.
 *
 *
 * *Important!* _If running in Intellij Idea, you might notice this Processor isn't called.
 * To fix, make sure to activate `Build Tools->Gradle->Runner->Delegate IDE build/run actions to gradle'`*_
 *
 * @since v0.1.1
 *
 * @author Simo Andreev <github.com/simo-andreev | simeon.zlatanov.andreev@gmail.com>
 */
@Suppress("unused") // used during build, just not referenced in user code.
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("bg.o.sim.annotations.ExposedModel")
internal class ApiGenerator : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {

        // Supported Annotation:
        val annotationClass = ExposedModel::class.java

        // go through all the elements that the system passes to the ApiGenerator
        for (element in roundEnv.getElementsAnnotatedWith(annotationClass)) {

            // @ExposedModel is only applicable to classes.
            if (element.kind != ElementKind.CLASS) {
                val error = "${this.javaClass.canonicalName} : " +
                        "${annotationClass.simpleName} can only be applied to classes!" +
                        " Failed for ${element.kind} [${element.simpleName}]"

                // Report error, so it gives informative compilation error.
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, error)
                return true
            }

            // @ExposedModel's mappingRoot parameter. It will be passed as a
            // org.springframework.web.bind.annotation.RequestMapping's value in generated code.
            val mappingRoot = element.getAnnotation(annotationClass).mappingRoot
            // @ExposedModel's exposeWebApi parameter. It will determine
            // whether to add @RestController and @RequestMapping, thus exposing CRUD operations to web requests
            val exposeWebApi = element.getAnnotation(annotationClass).exposeWebApi

            val pkg = processingEnv.elementUtils.getPackageOf(element).toString()

            generateTemplatedClass(mappingRoot, pkg, element as TypeElement, exposeWebApi)
        }

        return true
    }

    /**
     * Populate a Kotlin String template, with content similar to
     * the longform example in [the example][bg.o.sim.application.model.TransactionApi].
     * Afterwards the templated String is written to a file in the `build` directory of
     * the element's module, under the same package directive as the `element` itself.
     */
    private fun generateTemplatedClass(mappingRoot: String, pkg: String, element: TypeElement, exposeWebApi: Boolean) {
        val controllerTemplate = if (exposeWebApi) "@RestController @RequestMapping(\"$mappingRoot\")" else ""

        val mongoRepoName = "${element.simpleName}_MongoRepo"
        val crudApiName = "${element.simpleName}_CrudApi"

        val template = """
            package $pkg

            import bg.o.sim.annotations.ExposedModel
            import bg.o.sim.application.web.BaseEntity
            import bg.o.sim.application.web.CrudApiController
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.data.mongodb.repository.MongoRepository
            import org.springframework.stereotype.Service
            import org.springframework.web.bind.annotation.RequestMapping
            import org.springframework.web.bind.annotation.RestController
            import ${element.qualifiedName}

            internal interface $mongoRepoName : MongoRepository<${element.simpleName}, String>

            @Service
            $controllerTemplate
            class $crudApiName internal constructor(@Autowired repo: $mongoRepoName) : CrudApiController<${element.simpleName}>(repo)
        """.trimIndent()


        // build file name and get an appropriate directory to place it in.
        val fileName = "${mappingRoot.capitalize()}Repo"
        val kaptKotlinGeneratedDir = "${processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]}/${pkg.replace("\\.", File.separator)}"

        // Create any missing directories and write the new, generated, Kotlin file
        File(kaptKotlinGeneratedDir).mkdirs()
        File(kaptKotlinGeneratedDir, "$fileName.kt").apply { createNewFile() }.printWriter().use { out ->
            out.print(template)
        }
    }
}


