package org.master.graphics.ui

import org.joml.Vector3f

class QuasarColorScale {
  var scale = List.empty[(Double, Vector3f)]

  def getColor(value: Double): Vector3f = {
    val res = scale.sliding(2, 1).find(two => value <= two(1)._1).getOrElse(List((value, new Vector3f()), (value, new Vector3f())))
    val (left, right) = (res(0), res(1))

    val a = (value - left._1) / (right._1 - left._1)
    QuasarColorScale.lerp(left._2, right._2, a)
  }

}

object QuasarColorScale {
  def classic(): QuasarColorScale = {
    val cs = new QuasarColorScale()
    cs.scale = List(
      0.0 -> new Vector3f(0, 0, 1),
      0.5 -> new Vector3f(0, 1, 1),
      1.0 -> new Vector3f(1, 0, 0)
    )
    cs
  }
  def lerp(left: Vector3f, right: Vector3f, a: Double): Vector3f = left.add(right.sub(left, new Vector3f()).mul(a.toFloat, new Vector3f()), new Vector3f())
  def lerp(left: Float, right: Float, a: Float): Float = left + (right - left) * a
}
