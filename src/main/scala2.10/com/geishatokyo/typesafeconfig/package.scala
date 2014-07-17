package com.geishatokyo.typesafeconfig


import scala.reflect.runtime.universe._
/**
 * Implicits to support scala 2.10
 */
package object impl {


  implicit class Scala210TypeWrapper(t : Type) {
    def typeArgs(i : Int) : Type = {
      t match{
        case TypeRef(_,_,args) => args(i)
      }
    }

    def companion = {
      t.typeSymbol.companionSymbol.typeSignature
    }

    def decl(name : Name) = {
      t.declaration(name)
    }

  }

  implicit class Scala210SymbolWrapper(t : Symbol){

    def companion = {
      t.companionSymbol
    }

  }

  def TermName(str : String) : TermName = {
    val t : TermName = str
    t
  }


}
