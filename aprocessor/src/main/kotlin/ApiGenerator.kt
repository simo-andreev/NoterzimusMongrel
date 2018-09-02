package annotation

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
        return setOf(ExposedModel::class.qualifiedName!!)
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
            val pckge = processingEnv.elementUtils.getPackageOf(element).toString()

            generateClass(mappingRoot, pckge, element as TypeElement)
        }


        return true
    }

    private fun generateClass(mappingRoot: String, pckge: String, element: TypeElement) {
        val fileName = "${mappingRoot.capitalize()}Repo"
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
//        val file = FileSpec.builder(pckge, fileName)
//                .addImport(Autowired::class)
//                .addImport(MongoRepository::class)
//                .addImport(RequestMapping::class)
//                .addImport(RestController::class)
//                .addType(parameterizedRepoInterface)
//                .addType(apiClass)
//                .build()


        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        File(kaptKotlinGeneratedDir, "$fileName.kt").createNewFile()
    }

//    private fun elementIsBaseEntity(element: Element): Boolean {
//        return processingEnv
//                .typeUtils
//                .isSubtype(
//                        element.asType(),
//                        processingEnv.elementUtils.getTypeElement(BaseEntity::class.java.name).asType()
//                )
//    }
}



@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class ExposedModel(val mappingRoot: String)

