package org.master.window

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.system.MemoryUtil.NULL
import org.master.core._
import org.master.keys.Keys

/**
  * Created by valentin on 25/02/2018.
  */

object Window extends Window(fullscreen = false)

case class Size(width: Int = 0, height: Int = 0)

class Window(val fullscreen: Boolean) extends CoreUnit { // TODO: write correct fullscreen
  private var _ptr = 0L
  private var _size = Size()
  private var _delta = 0.0

  def size: Size = _size
  def ptr: Long = _ptr

  override def init(): Boolean = {
    GLFWErrorCallback.createPrint(System.err).set
    if (!glfwInit) throw new IllegalStateException("Unable to initialize GLFW")

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

    _size = getMonitorSize
    val fsFlag = if (fullscreen) glfwGetPrimaryMonitor() else NULL
    _ptr = glfwCreateWindow(_size.width, _size.height, "Core", fsFlag, NULL)

    if (_ptr == NULL) throw new RuntimeException("Failed to create the GLFW window")

    Keys.init()

    glfwMakeContextCurrent(_ptr)
    glfwSwapInterval(1)
    glfwShowWindow(_ptr)
    true
  }

  override def destroy(): Unit = {
    glfwFreeCallbacks(_ptr)
    glfwDestroyWindow(_ptr)

    glfwTerminate()
    glfwSetErrorCallback(null).free()
  }

  def loop(): Unit = {
    while (!glfwWindowShouldClose(_ptr)) {
      _delta = Utils.deltaTime {
        Core.units.foreach(_.update(_delta))
      }
    }
  }

  override def update(dt: Double): Unit = {
    glfwSwapBuffers(_ptr)
    glfwPollEvents()
  }

  private def getMonitorSize = {
    val s = glfwGetVideoMode(glfwGetPrimaryMonitor)
    Size(s.width(), s.height())
  }
}