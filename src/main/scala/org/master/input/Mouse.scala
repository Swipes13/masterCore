package org.master.input

import org.lwjgl.glfw.{GLFWCursorPosCallback, GLFWMouseButtonCallback}
import org.master.core.Window

case class MouseButton(var action: MouseButtonActionType.Value = MouseButtonActionType.Release) {
  def pushed: Boolean = action == MouseButtonActionType.Push
}
case class MousePos(x: Double, y: Double) {
  def -(pos: MousePos): MousePos = MousePos(x - pos.x, y - pos.y)
}
object MouseButtonType extends Enumeration {
  type ButtonType = Value
  val Right: MouseButtonType.Value = Value(0)
  val Left: MouseButtonType.Value = Value(1)
  val Wheel: MouseButtonType.Value = Value(2)
}
object MouseButtonActionType extends Enumeration {
  type MouseButtonActionType = Value
  val Release: MouseButtonActionType.Value = Value(0)
  val Push: MouseButtonActionType.Value = Value(1)
}

class Mouse {
  private var _positionCbs = scala.collection.immutable.List.empty[(MousePos) => Unit]
  private var _clickCbs = scala.collection.immutable.List.empty[(MousePos) => Unit]

  val buttons: Map[MouseButtonType.Value, MouseButton] = Map(
    MouseButtonType.Right -> MouseButton(),
    MouseButtonType.Left -> MouseButton(),
    MouseButtonType.Wheel -> MouseButton()
  )
  var position = MousePos(0, 0)

  def init(): Unit = {
    Window.addFocusCb((focused: Boolean) => { if (focused) setCursorUpdateCallback() })
    Window.cbKeeper.getChainMouseButtonCallback.add(new GLFWMouseButtonCallback() {
      override def invoke(windowId: Long, button: Int, action: Int, mods: Int): Unit = {
        buttons(MouseButtonType(button)).action = MouseButtonActionType(action)
      }
    })
  }

  def addPositionCb(cb: (MousePos) => Unit): Unit = _positionCbs = _positionCbs :+ cb
  def addMouseClickCb(cb: (MousePos) => Unit): Unit = _clickCbs = _clickCbs :+ cb

  def setCursorUpdateCallback(): Unit = {
    Window.cbKeeper.getChainCursorPosCallback.add(1, new GLFWCursorPosCallback() {
      override def invoke(window: Long, x: Double, y: Double): Unit = {
        position = MousePos(x, y)
        Window.cbKeeper.getChainCursorPosCallback.set(1, new GLFWCursorPosCallback() {
          override def invoke(window: Long, xpos: Double, ypos: Double): Unit = cursorPosCallback(window, xpos, ypos)
        })
      }
    })
  }
  private def cursorPosCallback(window: Long, x: Double, y: Double): Unit = {
    val delta = MousePos(x, y) - position
    position = MousePos(x, y)
    _positionCbs.foreach(f => f(delta))
  }

}
