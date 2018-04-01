package org.master.core

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.system.MemoryUtil.NULL

/**
  * Created by valentin on 25/02/2018.
  */

object Window extends Window(fullscreen = true)

case class Size(width: Int = 0, height: Int = 0)

class Window(val fullscreen: Boolean) extends CoreUnit { // TODO: write correct fullscreen
  private var changeFocusCbs = scala.collection.immutable.List.empty[(Boolean) => Unit]

  private var _ptr = 0L
  private var _size = Size()
  private var _delta = 0.0

  def size: Size = _size
  def ptr: Long = _ptr

  override def init(): Boolean = Utils.logging() {
    GLFWErrorCallback.createPrint(System.err).set
    if (!glfwInit) throw new IllegalStateException("Unable to initialize GLFW")

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)


    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, org.lwjgl.opengl.GL11.GL_TRUE)

    _size = getMonitorSize
    println(_size)
    val fsFlag = if (fullscreen) glfwGetPrimaryMonitor() else NULL
    _ptr = glfwCreateWindow(_size.width, _size.height, "Core", fsFlag, NULL)

    if (_ptr == NULL) throw new RuntimeException("Failed to create the GLFW window")

    glfwMakeContextCurrent(_ptr)
    glfwSwapInterval(1)
    glfwShowWindow(_ptr)

    glfwSetWindowFocusCallback(Window.ptr, (window: Long, focused: Boolean) => changeFocusCbs.foreach(f => f(focused)))

    // TODO: in fullscreen ?
//    glfwSetInputMode(ptr, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
    true
  }

  def addFocusCb(cb: (Boolean) => Unit): Unit = this.changeFocusCbs = this.changeFocusCbs :+ cb

  override def destroy(): Unit = Utils.logging() {
    glfwFreeCallbacks(_ptr)
    glfwDestroyWindow(_ptr)

    glfwTerminate()
    glfwSetErrorCallback(null).free()
  }

  def loop(updateFunction: Double => Unit): Unit = {
    while (!glfwWindowShouldClose(_ptr)) {
      _delta = Utils.deltaTime {
        updateFunction(_delta)
      }
    }
  }

  override def update(dt: Double): Unit = {
    glfwPollEvents()
    glfwSwapBuffers(_ptr)
  }

  private def getMonitorSize = {
    val s = glfwGetVideoMode(glfwGetPrimaryMonitor)
    Size(s.width(), s.height())
  }
}