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
}
