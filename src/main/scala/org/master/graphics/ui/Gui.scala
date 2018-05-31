package org.master.graphics.ui

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30._
import org.liquidengine.legui.component.misc.listener.component.TooltipCursorEnterListener
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.style.color.ColorConstants
import org.master.graphics.FrameBuffer
import org.liquidengine.legui.component.RadioButtonGroup
import org.liquidengine.legui.event.CursorEnterEvent
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.listener.MouseClickEventListener
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer
import java.util
import org.liquidengine.legui.component._
import org.liquidengine.legui.style.Style.DisplayType
import org.liquidengine.legui.style.Style.PositionType
import org.lwjgl.nanovg.NanoVGGL2
import org.lwjgl.nanovg.NanoVGGL3
import org.liquidengine.legui.system.renderer.Renderer
import org.master.core.Window
import org.liquidengine.legui.image.FBOImage

class Gui {
  private var _renderer: Renderer = new NvgRenderer()
  private val _testWidth = Window.size.width
  private val _testHeight = Window.size.height
  private val _frame = new Frame(_testWidth, _testHeight)
  private var _frameBuffer: FrameBuffer = _
  private var _isVersionNew = false
  private var _nvgContext = 0L

  def nvgContext: Long = _nvgContext
  def frameBuffer(): FrameBuffer = _frameBuffer

  def init(): Unit = {
    Window.addFrameForUpdate(_frame)
    createGuiElements(_frame)
    _renderer.initialize()
    _isVersionNew = (glGetInteger(GL_MAJOR_VERSION) > 3) || ((glGetInteger(GL_MAJOR_VERSION) == 3) && glGetInteger(GL_MINOR_VERSION) >= 2)
    if (_isVersionNew) {
      val flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS
      _nvgContext = NanoVGGL3.nvgCreate(flags)
    }
    else {
      val flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS
      _nvgContext = NanoVGGL2.nvgCreate(flags)
    }
    if (_nvgContext != 0) {
      _frameBuffer = FrameBuffer.create(1000, 1000)
      val widget = new Widget(10, 10, 1000, 1000)
      widget.setCloseable(false)
      widget.setMinimizable(false)
      widget.setResizable(true)
      widget.getContainer.getStyle.setDisplay(DisplayType.FLEX)

      val imageView = new ImageView(new FBOImage(_frameBuffer.texture.id, _frameBuffer.width, _frameBuffer.height))
      imageView.setPosition(10, 10)
      imageView.getStyle.setPosition(PositionType.RELATIVE)
      imageView.getStyle.getFlexStyle.setFlexGrow(1)
      imageView.getStyle.setMargin(10f)
      imageView.getStyle.setMinimumSize(50, 50)
      widget.getContainer.add(imageView)
      _frame.getContainer.add(widget)
    }
  }

  def update(dt: Double): Unit = {
//    val windowSize = Window.context.getWindowSize
//    glViewport(0, 0, windowSize.x * 2, windowSize.y * 2)
    _renderer.render(_frame, Window.context)
  }

  def destroy(): Unit = {
    _frameBuffer.destroy()
    _renderer.destroy()
    if (_isVersionNew) NanoVGGL3.nnvgDelete(_nvgContext)
    else NanoVGGL2.nnvgDelete(_nvgContext)
  }

  private def createGuiElements(frame: Frame): Unit = { // Set background color for frame
//    frame.getContainer.setBackgroundColor(ColorConstants.lightBlue())
    val button = new Button("Add components", 20, 20, 160, 30)
    val border = new SimpleLineBorder(ColorConstants.black(), 1)
//    button.setBorder(border)
    val added = Array(x = false)
    button.getListenerMap.addListener(classOf[MouseClickEvent[_ <: Component]],
      new MouseClickEventListener {
        override def process(event: MouseClickEvent[_ <: Component]): Unit = {
          if (!added(0)) {
            added(0) = true
            frame.getContainer.addAll(generateOnFly)
          }
        }
      })
    button.getListenerMap.addListener(classOf[CursorEnterEvent[_ <: Component]], new TooltipCursorEnterListener())
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
