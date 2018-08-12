package com.jzallas.endpointparser.extensions

import com.jzallas.endpointparser.ProcessingException
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.ErrorType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable
import javax.lang.model.type.UnknownTypeException
import javax.lang.model.util.SimpleTypeVisitor6

class TypeVisitor : SimpleTypeVisitor6<Void, Void>() {
  lateinit var visitDeclared: (type: DeclaredType) -> Unit
  override fun visitDeclared(p0: DeclaredType, p1: Void?): Void? {
    visitDeclared.invoke(p0)
    return null
  }

  lateinit var visitPrimitive: (type: PrimitiveType) -> Unit
  override fun visitPrimitive(p0: PrimitiveType, p1: Void?): Void? {
    visitPrimitive.invoke(p0)
    return null
  }

  lateinit var visitArray: (type: ArrayType) -> Unit
  override fun visitArray(p0: ArrayType, p1: Void?): Void? {
    visitArray.invoke(p0)
    return null
  }

  lateinit var visitTypeVariable: (type: TypeVariable) -> Unit
  override fun visitTypeVariable(p0: TypeVariable, p1: Void?): Void? {
    visitTypeVariable.invoke(p0)
    return null
  }

  lateinit var visitError: (type: ErrorType) -> Unit
  override fun visitError(p0: ErrorType, p1: Void?): Void? {
    visitError.invoke(p0)
    return null
  }

  override fun defaultAction(p0: TypeMirror?, p1: Void?): Void {
    // not sure???
    throw ProcessingException(UnknownTypeException(p0, p1))
  }
}