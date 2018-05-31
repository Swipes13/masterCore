package org.master.graphics.ui

import java.util

import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.{GLFW, GLFWKeyCallback, GLFWKeyCallbackI, GLFWWindowCloseCallback}
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.{GL_COLOR_BUFFER_BIT, glClear, glClearColor, glViewport}
import org.lwjgl.system.MemoryUtil.NULL
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.component.RadioButton
import org.liquidengine.legui.component.misc.listener.component.TooltipCursorEnterListener
import org.liquidengine.legui.component.misc.listener.togglebutton.ToggleButtonMouseClickListener
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.system.context.Context
//import org.liquidengine.legui.border.SimpleLineBorder
//import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.RadioButtonGroup
import org.liquidengine.legui.event.CursorEnterEvent
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.listener.CursorEnterEventListener
import org.liquidengine.legui.listener.MouseClickEventListener
import org.liquidengine.legui.listener.processor.EventProcessor
import org.liquidengine.legui.system.context.DefaultCallbackKeeper
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import java.io.IOException
import java.util

import org.liquidengine.legui.system.renderer.Renderer
import org.master.core.Window

class Gui {

  var renderer: Renderer = new NvgRenderer()
  val WIDTH = 2000
  val HEIGHT = 4000
  val frame = new Frame(WIDTH, HEIGHT)
  val systemEventProcessor = new SystemEventProcessor

  def init(): Unit = {
//    System.setProperty("joml.nounsafe", "true")
//    System.setProperty("java.awt.headless", "true")
//    if (!GLFW.glfwInit) throw new RuntimeException("Can't initialize GLFW")
    Window.addFrameForEvents(frame)
    createGuiElements(frame)

    renderer.initialize()
//    Window.context.updateGlfwWindow()
  }

  def update(dt: Double): Unit = {
//    val windowSize = Window.context.getWindowSize
//    glViewport(0, 0, windowSize.x * 2, windowSize.y * 2)
    renderer.render(frame, Window.context)
  }

  def destroy(): Unit = renderer.destroy()

  private def createGuiElements(frame: Frame): Unit = { // Set background color for frame
//    frame.getContainer.setBackgroundColor(ColorConstants.lightBlue())
    val button = new Button("Add components", 20, 20, 160, 30)
    val border = new SimpleLineBorder(ColorConstants.black(), 1)
//    button.setBorder(border)
    val added = Array(x = false)
//    button.getListenerMap.addListener(classOf[MouseClickEvent[_ <: Component]],
//      new MouseClickEventListener {
//        override def process(event: MouseClickEvent[_ <: Component]): Unit = {
//          if (!added(0)) {
//            added(0) = true
//            frame.getContainer.addAll(generateOnFly)
//          }
//        }
//      })
//    button.getListenerMap.addListener(classOf[CursorEnterEvent[_ <: Component]], new TooltipCursorEnterListener())
    frame.getContainer.add(button)
  }

  private def generateOnFly = {
    val list = new util.ArrayList[Component]
    val label = new Label(20, 60, 200, 20)
    label.getTextState.setText("Generated on fly label")
    label.getTextState.setTextColor(ColorConstants.red())
    val group = new RadioButtonGroup
    val radioButtonFirst = new RadioButton("First", 20, 90, 200, 20)
    val radioButtonSecond = new RadioButton("Second", 20, 110, 200, 20)
    radioButtonFirst.setRadioButtonGroup(group)
    radioButtonSecond.setRadioButtonGroup(group)
    list.add(label)
    list.add(radioButtonFirst)
    list.add(radioButtonSecond)
    list
  }
}
