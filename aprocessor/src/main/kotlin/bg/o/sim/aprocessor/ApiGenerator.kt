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
 * Creates files parsable by Spring Boot Starter -data-rest, -data-mongodb-reactive and -web.
 * Spring then takes said generated classes and props-up a MongoDb CRUD controller and corresponding REST APIs.
 *
 * To be used on [BaseEntity][bg.o.sim.application.web.BaseEntity]. If annotated element is _not_ a subclass
 * of BaseEntity, compilation will fail.
 *
 * *Important!* _If running in Intellij Idea,
 * make sure to check `Build Tools->Gradle->Runner->Delegate IDE build/run actions to gradle'`*_
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

        val annotationClass = ExposedModel::class.java
        for (element in roundEnv.getElementsAnnotatedWith(annotationClass)) {

            // @ExposedModel is only applicable to classes.
            if (element.kind != ElementKind.CLASS) {
                val error = "$annotationClass can only be applied to classes! Failed for [${element.simpleName}]"
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, error)
                return true
            }

            // @ExposedModel's mappingRoot parameter. It will be passed as a
            // org.springframework.web.bind.annotation.RequestMapping's value, further on.
            val mappingRoot = element.getAnnotation(annotationClass).mappingRoot
            val pkg = processingEnv.elementUtils.getPackageOf(element).toString()

            generateTemplatedClass(mappingRoot, pkg, element as TypeElement)
        }


        return true
    }

    /**
     * Populate a Kotlin String template, with content similar to
     * the longform example in [the example][bg.o.sim.application.model.TransactionApi].
     * Afterwards the templated String is written to a file in the `build` directory of
     * the element's module, under the same pakcgae directive as the `element` itself.
     */
    private fun generateTemplatedClass(mappingRoot: String, pkg: String, element: TypeElement){
        val template = """
            package $pkg

            import bg.o.sim.annotations.ExposedModel
            import bg.o.sim.application.web.BaseEntity
            import bg.o.sim.application.web.CrudApiController
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.data.mongodb.repository.MongoRepository
            import org.springframework.web.bind.annotation.RequestMapping
            import org.springframework.web.bind.annotation.RestController
            import ${element.qualifiedName}

            interface ${mappingRoot.capitalize()}Repo : MongoRepository<${element.simpleName}, String>
            @RestController
            @RequestMapping("$mappingRoot")
            class ${mappingRoot.capitalize()}Api(@Autowired repo: ${mappingRoot.capitalize()}Repo) : CrudApiController<${element.simpleName}>(repo)
        """.trimIndent()


        // get f.name and an appropriate directory to place it in.
        val fileName = "${mappingRoot.capitalize()}Repo"
        val kaptKotlinGeneratedDir = "${processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]}/${pkg.replace("\\.", File.separator )}"

        // Create any missing directories and write the new, generated, Kotlin file
        File(kaptKotlinGeneratedDir).mkdirs()
        File(kaptKotlinGeneratedDir, "$fileName.kt").apply { createNewFile() }.printWriter().use { out ->
            out.print(template)
        }
    }
}


