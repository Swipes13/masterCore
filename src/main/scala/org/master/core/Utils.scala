package org.master.core

object Utils {
  def deltaTime[R](block: => R): Double = {
    val t0 = System.nanoTime()
    block
    (System.nanoTime() - t0).toDouble / 1e9
  }
  def logging[R](logMessage: String = "", endLogMessage: String = "")(block: => R): R = {
    val st = Thread.currentThread.getStackTrace()(2)
    println(if (logMessage.isEmpty) s"${st.getClassName}.${st.getMethodName} start" else logMessage)
    val res = block
    println(if (endLogMessage.isEmpty) s"${st.getClassName}.${st.getMethodName} finish" else endLogMessage)
    res
  }
  def arrayToStrWithDelim[T](array: Array[T], delim: Char, toStringFunc: (T) => String = null): String = {
    if (toStringFunc == null) {
      array.tail.foldLeft(s"${array.head}") { case (prev, next) => prev + s"$delim$next" }
    } else {
      array.tail.foldLeft(s"${toStringFunc(array.head)}") { case (prev, next) =>
        prev + s"$delim${toStringFunc(next)}"
      }
    }
  }
}
