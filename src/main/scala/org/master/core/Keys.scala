package org.master.core

import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.{GLFWCursorPosCallback, GLFWKeyCallback, GLFWWindowFocusCallbackI}
import org.master.core.KeyType.KeyType

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
  val LShift: KeyType.Value = Value(GLFW_KEY_LEFT_SHIFT)
  val Escape: KeyType.Value = Value(GLFW_KEY_ESCAPE)
}

case class MousePos(x: Double, y: Double) {
  def -(pos: MousePos): MousePos = MousePos(x - pos.x, y - pos.y)
}
case class KeyFunc(func: () => Unit, var active: Boolean = false)

class Keys extends CoreUnit {
  private var keyPressCbs = scala.collection.immutable.Map.empty[Int, scala.collection.immutable.List[KeyFunc]]
  private var keyReleaseCbs = scala.collection.immutable.Map.empty[Int, scala.collection.immutable.List[KeyFunc]]
  private var mousePosCbs = scala.collection.immutable.List.empty[(MousePos) => Unit]

  var mousePos = MousePos(0, 0)

  override def init(): Boolean = Utils.logging() {
    Window.cbKeeper.getChainKeyCallback.add(new GLFWKeyCallback() {
      override def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        if (key == KeyType.Escape.id && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true)
        keyPressCbs.foreach { case (t, cbs) =>
          if (key == t) {
            if (action == GLFW_PRESS) cbs.foreach(_.active = true)
            else if (action == GLFW_RELEASE) cbs.foreach(_.active = false)
          }
        }
        keyReleaseCbs.foreach { case (t, cbs) => if (key == t) cbs.foreach(_.func()) }
      }
    })
    Window.addFocusCb((focused: Boolean) => { if (focused) this.setCursorUpdateCallback() })
    true
  }
  def addKeyPressCb(t: KeyType, cb: () => Unit): Unit = keyPressCbs = addKeyCb(keyPressCbs, t.id, cb)
  def addKeyReleaseCb(t: KeyType, cb: () => Unit): Unit = keyReleaseCbs = addKeyCb(keyReleaseCbs, t.id, cb)
  private def addKeyCb(map: scala.collection.immutable.Map[Int, scala.collection.immutable.List[KeyFunc]], t: Int, cb: () => Unit) = {
    val newList = map.get(t) match {
      case Some(list) => list :+ KeyFunc(cb)
      case None => scala.collection.immutable.List(KeyFunc(cb))
    }
    map + (t -> newList)
  }
  def addMousePosCb(cb: (MousePos) => Unit): Unit = mousePosCbs = mousePosCbs :+ cb
  private def setCursorUpdateCallback() = {
    Window.cbKeeper.getChainCursorPosCallback.add(new GLFWCursorPosCallback() {
      override def invoke(window: Long, x: Double, y: Double): Unit = {
        mousePos = MousePos(x, y)
        Window.cbKeeper.getChainCursorPosCallback.set(1, new GLFWCursorPosCallback() {
          override def invoke(window: Long, xpos: Double, ypos: Double): Unit = cursorPosCallback(window, xpos, ypos)
        })
      }
    })
  }
  private def cursorPosCallback(window: Long, x: Double, y: Double): Unit = {
    val delta = MousePos(x, y) - mousePos
    mousePos = MousePos(x, y)
    mousePosCbs.foreach(f => f(delta))
  }

  override def update(dt: Double): Unit = {
    keyPressCbs.foreach { case (_, funcs) => funcs.foreach(kf => if (kf.active) kf.func()) }
  }
}
