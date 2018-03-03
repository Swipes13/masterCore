package org.master.core

object Utils {
  def deltaTime[R](block: => R): Double = {
    val t0 = System.nanoTime()
    block
    (System.nanoTime() - t0).toDouble / 1e9
  }
}
