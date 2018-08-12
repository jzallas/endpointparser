package com.jzallas.endpointparser.extensions

import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror

fun TypeMirror.asString(): String {
  return StringBuilder().also {
    this.asStringTo(it)
  }.toString()
}

private fun TypeMirror.asStringTo(buffer: StringBuilder) {
  TypeVisitor().apply {
    visitPrimitive = { buffer.append(it.kind.toString()) }

    visitTypeVariable = { buffer.append(it.asElement().simpleName) }

    visitError = { buffer.append(it.toString()) }

    visitArray = { type ->
      type.componentType.let {
        when (it) {
          is PrimitiveType -> visitPrimitive.invoke(it)
          else -> it.asStringTo(buffer)
        }
      }.also { buffer.append("[]") }
    }

    visitDeclared = { type ->
      type.asElement()
          .asString()
          .let { buffer.append(it) }

      type.typeArguments.takeIf { it.isNotEmpty() }
          ?.let {
            buffer.append("<")
            // all the generic types in between, separated by comma
            it.forEachIndexed { index, typeMirror ->
              if (index != 0) buffer.append(", ")
              typeMirror.asStringTo(buffer)
            }
            buffer.append(">")
          }
    }
  }.let { accept(it, null) }
}