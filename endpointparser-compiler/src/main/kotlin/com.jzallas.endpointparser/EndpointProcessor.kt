package com.jzallas.endpointparser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.auto.service.AutoService
import com.jzallas.endpointparser.extensions.arguments
import com.jzallas.endpointparser.extensions.className
import com.jzallas.endpointparser.extensions.methodName
import com.jzallas.endpointparser.extensions.returns
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.StandardLocation
import kotlin.reflect.KClass

@AutoService(Processor::class)
class EndpointProcessor : AbstractProcessor() {

  companion object {
    private val SUPPORTED_TYPES = setOf(
        GET::class,
        PUT::class,
        POST::class,
        DELETE::class
    )

    private const val OUTPUT_FILE_NAME = "results.json"

    private const val ENABLED_FLAG = "endpointParser"

    private val objectMapper = ObjectMapper().registerKotlinModule()
  }

  private var messager: Messager? = null
  private var filer: Filer? = null
  private var enabled = false

  private val endpoints = mutableSetOf<EndpointInfo>()

  private val fileResource
    get() = filer?.createResource(
        StandardLocation.CLASS_OUTPUT,
        javaClass.canonicalName,
        OUTPUT_FILE_NAME
    )

  override fun init(environment: ProcessingEnvironment) {
    super.init(environment)
    messager = environment.messager
    filer = environment.filer
    enabled = environment.options
        .getOrDefault(ENABLED_FLAG, false)
        .toString()
        .toBoolean()
  }

  override fun getSupportedAnnotationTypes(): MutableSet<String> {
    return SUPPORTED_TYPES.map { it.java.name }.toMutableSet()
  }

  override fun process(set: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
    if (!enabled) return false
    try {
      if (roundEnvironment.processingOver()) {
        write(endpoints)
        messager?.printMessage(Diagnostic.Kind.NOTE, "Finished writing endpoints")
      } else {
        SUPPORTED_TYPES
            .associate { it to roundEnvironment.getElementsAnnotatedWith(it.java) }
            .flatMap { (annotation, elements) -> elements.map { annotation to it } }
            .mapNotNull { (annotation, element) -> generateEndpointInfo(annotation, element) }
            .let { endpoints.addAll(it) }

        messager?.printMessage(Diagnostic.Kind.NOTE, "Finished parsing parsing endpoints.")
      }
    } catch (exception: ProcessingException) {
      // at least leave a note in the log stating that the processor failed
      messager?.printMessage(Diagnostic.Kind.WARNING, exception.message)

    }
    return true
  }

  private fun write(endpoints: Collection<EndpointInfo>) {
    try {
      fileResource!!.openWriter()
          .use { objectMapper.writeValue(it, endpoints) }
    } catch (e: Exception) {
      throw ProcessingException(e)
    }
  }

  private fun <T : Annotation> generateEndpointInfo(annotation: KClass<T>, element: Element): EndpointInfo? {

    return element.let { it as? ExecutableElement }
        ?.let {
          EndpointInfo(
              annotation.java.simpleName,
              it.endpoint(annotation),
              it.methodName,
              it.className,
              it.arguments.map { (type, name) -> EndpointInfo.Parameters(type, name) },
              it.returns
          )
        }
  }

  private fun <T : Annotation> Element.endpoint(annotation: KClass<T>): String {
    return when (annotation) {
      GET::class -> getAnnotation(GET::class.java).value
      POST::class -> getAnnotation(POST::class.java).value
      PUT::class -> getAnnotation(PUT::class.java).value
      DELETE::class -> getAnnotation(DELETE::class.java).value
      else -> throw ProcessingException(IllegalArgumentException("Parsed invalid annotation $annotation"))
    }.takeIf { it.isNotEmpty() } ?: "/"
  }
}