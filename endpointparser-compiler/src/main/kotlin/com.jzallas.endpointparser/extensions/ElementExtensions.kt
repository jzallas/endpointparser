package com.jzallas.endpointparser.extensions

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement

private const val DEFAULT_SEPARATOR = '.'

val ExecutableElement.arguments
  get() = this.parameters
      .map { it.asType().asString() to it.asString() }

val ExecutableElement.returns
  get () = this.returnType?.asString()

val ExecutableElement.methodName
  get() = this.simpleName.toString()

val ExecutableElement.className: String
  get() = enclosingElement.let { it as TypeElement }
      .qualifiedName
      .toString()

val Element.packageElement: PackageElement
  get() = when (this.kind) {
    ElementKind.PACKAGE -> this as PackageElement
    else -> this.enclosingElement.packageElement
  }

fun Element.asString(): String {
  this.let { it as? TypeElement }
      ?.let { return it.asString() }

  return when (kind) {
    ElementKind.FIELD,
    ElementKind.CONSTRUCTOR,
    ElementKind.METHOD -> "$enclosingElement.$this"
    else -> toString()
  }
}

private fun TypeElement.asString(): String {
  return packageElement.qualifiedName
      .toString()
      .let { packageName ->
        val qualifiedName = qualifiedName.toString()

        if (packageName.isEmpty()) {
          qualifiedName.replace('.', DEFAULT_SEPARATOR)
        } else {
          qualifiedName.substring(packageName.length + 1)
              .replace('.', DEFAULT_SEPARATOR)
              .let { "$packageName.$it" }
        }
      }
}