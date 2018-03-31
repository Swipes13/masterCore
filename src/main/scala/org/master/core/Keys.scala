package org.master.core

import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.BufferUtils

/**
  * Created by valentin on 25/02/2018.
  */
object Keys extends Keys()

object KeyType extends Enumeration {
  type KeyType = Value
  val None = 0
  val W: KeyType.Value = Value(GLFW_KEY_W)
  val A: KeyType.Value = Value(GLFW_KEY_A)
  val S: KeyType.Value = Value(GLFW_KEY_S)
  val D: KeyType.Value = Value(GLFW_KEY_D)
  val Escape: KeyType.Value = Value(GLFW_KEY_ESCAPE)
}

class Keys extends CoreUnit {
  var keyPressFuncs = scala.collection.immutable.Map.empty[Int, () => Unit]
  var mousePosFuncs = scala.collection.immutable.List.empty[(Double, Double) => Unit]

  var mouseX = 0.0
  var mouseY = 0.0

  override def init(): Boolean = Utils.logging() {
    glfwSetKeyCallback(Window.ptr, (window: Long, key: Int, scanCode: Int, action: Int, mods: Int) => {
      if (key == KeyType.Escape.id && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true)

      keyPressFuncs.foreach { case (t, f) => if (key == t && action == GLFW_REPEAT) f() }
    })
    glfwSetCursorPosCallback(Window.ptr, (window: Long, x: Double, y: Double) => {
      val xDelta = mouseX - x
      val yDelta = mouseY - y
      mouseX = x
      mouseY = y
      mousePosFuncs.foreach(f => f(xDelta, yDelta))
    })

    val coords = BufferUtils.createDoubleBuffer(2)
    glfwGetCursorPos(Window.ptr, coords, coords)
    mouseX = coords.get(0)
    mouseY = coords.get(1)
    true
  }
}
