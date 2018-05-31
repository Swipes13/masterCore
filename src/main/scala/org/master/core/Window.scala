package org.master.core

import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.listener.processor.EventProcessor
import org.liquidengine.legui.system.context.{Context, DefaultCallbackKeeper}
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.{GLFWErrorCallback, GLFWWindowFocusCallback}
import org.lwjgl.system.MemoryUtil.NULL
import org.liquidengine.legui.animation.Animator
import org.liquidengine.legui.system.layout.LayoutManager

import scala.collection.mutable.ListBuffer

/**
  * Created by valentin on 25/02/2018.
  */

object Window extends Window(fullscreen = false)

case class Size(width: Int = 0, height: Int = 0)

class Window(val fullscreen: Boolean) extends CoreUnit { // TODO: write correct fullscreen
  private var changeFocusCbs = scala.collection.immutable.List.empty[(Boolean) => Unit]
  private var _framesForUpdate = ListBuffer.empty[Frame]

  private var _ptr = 0L
  private var _size = Size()
  private var _delta = 0.0
  private var _context: Context = _
  private val _keeper = new DefaultCallbackKeeper
  private val _systemEventProcessor = new SystemEventProcessor
  def size: Size = _size
  def ptr: Long = _ptr
  def context: Context = _context
  def cbKeeper: DefaultCallbackKeeper = _keeper

  override def init(): Boolean = Utils.logging() {
    GLFWErrorCallback.createPrint(System.err).set

    System.setProperty("joml.nounsafe", "true")
    System.setProperty("java.awt.headless", "true")

    if (!glfwInit) throw new IllegalStateException("Unable to initialize GLFW")

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, org.lwjgl.opengl.GL11.GL_TRUE)
    glfwWindowHint(GLFW_SAMPLES, 4)

    _size = getMonitorSize
    println(_size)
    val fsFlag = if (fullscreen) glfwGetPrimaryMonitor() else NULL
    _ptr = glfwCreateWindow(_size.width, _size.height, "Core", fsFlag, NULL)

    if (_ptr == NULL) throw new RuntimeException("Failed to create the GLFW window")

    glfwMakeContextCurrent(_ptr)
    glfwSwapInterval(1)
    glfwShowWindow(_ptr)

    _context = new Context(ptr)
    _keeper.registerCallbacks(ptr)

    _keeper.getChainWindowFocusCallback.add(new GLFWWindowFocusCallback() {
      override def invoke(window: Long, focused: Boolean): Unit = changeFocusCbs.foreach(f => f(focused))
    })

    _systemEventProcessor.addDefaultCallbacks(_keeper)

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

    // legui update needed
    _framesForUpdate.foreach(_systemEventProcessor.processEvents(_, context))
    EventProcessor.getInstance.processEvents()
    _framesForUpdate.foreach(LayoutManager.getInstance.layout(_))
    Animator.getInstance.runAnimations()
  }

  private def getMonitorSize = {
    val s = glfwGetVideoMode(glfwGetPrimaryMonitor)
    Size(s.width(), s.height())
  }

  def addFrameForUpdate(frame: Frame): ListBuffer[Frame] = _framesForUpdate += frame
}