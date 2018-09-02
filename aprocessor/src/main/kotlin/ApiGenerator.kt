package annotation

import bg.o.sim.annotations.ExposedModel
import com.google.auto.service.AutoService
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


@AutoService(Processor::class)
class ApiGenerator : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return mutableSetOf(ExposedModel::class.java.name)
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {

        val annotClass = ExposedModel::class.java
        for (element in roundEnv.getElementsAnnotatedWith(annotClass)) {
            if (element.kind != ElementKind.CLASS) {
                val error = annotClass.toString() + " can only be applied to classes! " +
                        "Failed for [" + element.simpleName + "]"
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, error)
                return true
            }

//            if (!elementIsBaseEntity(element)) {
//                val error = annotClass.toString() + " can only be applied to web.BaseEntity subclasses! " +
//                        "Failed for [" + element.simpleName + "]"
//                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, error)
//                return true
//            }

            val mappingRoot = element.getAnnotation(annotClass).mappingRoot
            val pkg = processingEnv.elementUtils.getPackageOf(element).toString()

            generateTemplatedClass(mappingRoot, pkg, element as TypeElement)
        }


        return true
    }

    private fun generateTemplatedClass(mappingRoot: String, pkg: String, element: TypeElement){
        val template = """
            package $pkg

            import bg.o.sim.annotations.ExposedModel
            import bg.o.sim.web.BaseEntity
            import bg.o.sim.web.CrudApiController
            import org.springframework.beans.factory.annotation.Autowired
            import org.springframework.data.mongodb.repository.MongoRepository
            import org.springframework.web.bind.annotation.RequestMapping
            import org.springframework.web.bind.annotation.RestController
            import ${element.qualifiedName}

            interface ${mappingRoot.capitalize()}Repo : MongoRepository<${element.simpleName}, String>

            @RestController
            @RequestMapping("${mappingRoot}")
            class ${mappingRoot.capitalize()}Api(@Autowired repo: ${mappingRoot.capitalize()}Repo) : CrudApiController<${element.simpleName}>(repo)
        """.trimIndent()

        
        val fileName = "${mappingRoot.capitalize()}Repo"
        val kaptKotlinGeneratedDir = "${processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]}/${pkg.replace("\\.", File.separator )}"
        File(kaptKotlinGeneratedDir).mkdirs()
        File(kaptKotlinGeneratedDir, "$fileName.kt").apply { createNewFile() }.printWriter().use { out ->
            out.print(template)
        }
    }

    // TODO - decide how to handle BaseEntity as super-class
//    private fun elementIsBaseEntity(element: Element): Boolean {
//        return processingEnv
//                .typeUtils
//                .isSubtype(
//                        element.asType(),
//                        processingEnv.elementUtils.getTypeElement(BaseEntity::class.java.name).asType()
//                )
//    }

    private fun generateClass(mappingRoot: String, pkg: String, element: TypeElement) {
//        val fileName = "${mappingRoot.capitalize()}Repo"
//
//        val entityType = element.asClassName()
//        val stringType = ClassName("kotlin", "String")
//
//
//        val mongoRepoType = ClassName(MongoRepository::class.java.packageName, MongoRepository::class.java.simpleName)
//        val fullMongoRepoType = mongoRepoType.parameterizedBy(entityType, stringType)
//        val parameterizedRepoInterface = TypeSpec.interfaceBuilder(fileName)
//                .addSuperinterface(fullMongoRepoType)
//                .build()
//
//
//        val requestMappingType = AnnotationSpec.builder(RequestMapping::class.java)
//                .addMember("value = [%S]", mappingRoot)
//                .build()
//        val repoParameterType = ParameterSpec.builder("repo", fullMongoRepoType)
//                .addAnnotation(Autowired::class)
//                .build()
//        val primConstructorType = FunSpec.constructorBuilder()
//                .addParameter(repoParameterType)
//                .build()
//
//        val apiControllerType = ClassName(CrudApiController::class.java.packageName, CrudApiController::class.java.simpleName)
//        val parameterizedApiType = apiControllerType.parameterizedBy(entityType)
//
//        val apiClass = TypeSpec.classBuilder("${mappingRoot.capitalize()}Api")
//                .addAnnotation(RestController::class)
//                .addAnnotation(requestMappingType)
//                .primaryConstructor(primConstructorType)
//                .superclass(parameterizedApiType)
//                .addSuperclassConstructorParameter("%N", primConstructorType)
//                .build()
//        val file = FileSpec.builder(pkg, fileName)
//                .addType(parameterizedRepoInterface)
//                .build()
//
//
//        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
//        file.writeTo(File(kaptKotlinGeneratedDir, "$fileName.kt"))
//        File(kaptKotlinGeneratedDir).mkdir()
//        File(kaptKotlinGeneratedDir, "$fileName.kt").createNewFile()
    }
}


