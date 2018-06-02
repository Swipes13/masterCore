package org.master.graphics.ui

import org.liquidengine.legui.component.misc.listener.component.TooltipCursorEnterListener
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.style.color.ColorConstants
import org.master.graphics.{FrameBuffer, RTContext}
import org.liquidengine.legui.component.RadioButtonGroup
import org.liquidengine.legui.event.{CursorEnterEvent, FocusEvent, MouseClickEvent}
import org.liquidengine.legui.listener.MouseClickEventListener
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer
import java.util
import javafx.stage.FileChooser

import org.joml.{Vector2i, Vector4f}
import org.liquidengine.legui.component._
import org.liquidengine.legui.style.Style.DisplayType
import org.liquidengine.legui.style.Style.PositionType
import org.liquidengine.legui.system.renderer.Renderer
import org.master.core.Window
import org.liquidengine.legui.image.FBOImage
import org.lwjgl.nanovg.NanoVG._
import org.lwjgl.opengl.GL11._
import org.liquidengine.legui.listener.FocusEventListener
import org.liquidengine.legui.style.color.ColorConstants

import scala.collection.mutable.ArrayBuffer

class Gui {
  private var _renderer: Renderer = new NvgRenderer()
  private val _testWidth = Window.innerSize.width
  private val _testHeight = Window.innerSize.height
  private val _frame = new Frame(_testWidth, _testHeight)

  var rtContexts: ArrayBuffer[RTContext] = ArrayBuffer.empty[RTContext]

  def init(): Gui = {
    Window.addFrameForUpdate(_frame)
    createGuiElements()
    createMainGui()
    _renderer.initialize()
    this
  }

  def update(): Unit = _renderer.render(_frame, Window.context)

  def destroy(): Unit = {
    rtContexts.foreach(RTContext.destroy)
    _renderer.destroy()
  }

  private def createGuiElements(): Unit = { // Set background color for frame
//    frame.getContainer.setBackgroundColor(ColorConstants.lightBlue())
    val button = new Button("Add components", 20, 20, 160, 30)
//    button.setBorder(border)
    val added = Array(x = false)
    button.getListenerMap.addListener(classOf[MouseClickEvent[_ <: Component]],
      new MouseClickEventListener {
        override def process(event: MouseClickEvent[_ <: Component]): Unit = {
          if (!added(0)) {
            added(0) = true
            _frame.getContainer.addAll(generateOnFly)
          }
        }
      })
    button.getListenerMap.addListener(classOf[CursorEnterEvent[_ <: Component]], new TooltipCursorEnterListener())
    _frame.getContainer.add(button)
  }

  private def createMainGui(): Unit = {
    val rpBorderWidth = 2
    val rpWidth = 400
    val rpHeight = Window.innerSize.height

    val rightPanel = new Panel(Window.innerSize.width - rpWidth + rpBorderWidth * 2, 0, rpWidth - 4 * rpBorderWidth, rpHeight)

    rightPanel.getStyle.getBackground.setColor(new Vector4f(237 / 255.0f, 237 / 255.0f, 237 / 255.0f, 1))
    rightPanel.getStyle.setBorder(new SimpleLineBorder(new Vector4f(157 / 255.0f, 157 / 255.0f, 157 / 255.0f, 1), rpBorderWidth))

    _frame.getContainer.add(rightPanel)

    val rtSize = new Vector2i((Window.innerSize.width - rpWidth) / 2 - 6, Window.innerSize.height / 2 - 6)
//    val rtSize = new Vector2i(500, 500)
    for (i <- 0 until 2; j <- 0 until 2) {
      val context = new RTContext(rtSize)

      val imageView = new ImageView(new FBOImage(context.frameBuffer.texture.id, rtSize.x, rtSize.y))
      imageView.setPosition(2 + i * (rtSize.x + 4), 2 + j * (rtSize.y + 4))
      imageView.setSize(rtSize.x, rtSize.y)
      imageView.getStyle.setPosition(PositionType.RELATIVE)
      imageView.getStyle.getFlexStyle.setFlexGrow(1)
      imageView.getStyle.setMargin(10f)
      imageView.getStyle.setMinimumSize(50, 50)

      imageView.getListenerMap.addListener(classOf[FocusEvent[_ <: Component]], new FocusEventListener {
        override def process(event: FocusEvent[_ <: Component]): Unit = context.hasFocus = event.isFocused
      })

      _frame.getContainer.add(imageView)
      rtContexts += context
    }
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


  def nvgRenderExample(): Unit = {
//    import org.lwjgl.nanovg.NVGColor
//    glEnable(GL_BLEND)
//    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//    nvgBeginFrame(Window.nvgContext.id, _testGui.frameBuffer().width, _testGui.frameBuffer().height, 1)
//
//    val nvgColorOne: NVGColor = NVGColor.calloc
//    val nvgColorTwo: NVGColor = NVGColor.calloc
//
//    nvgColorOne.r(0)
//    nvgColorOne.g(1)
//    nvgColorOne.b(0)
//    nvgColorOne.a(1)
//
//    nvgColorTwo.r(0)
//    nvgColorTwo.g(0)
//    nvgColorTwo.b(0)
//    nvgColorTwo.a(1)
//
//    nvgTranslate(Window.nvgContext.id, _testGui.frameBuffer().width / 2f, _testGui.frameBuffer().height / 2f)
//    nvgRotate(Window.nvgContext.id, 5)
//
//    nvgBeginPath(Window.nvgContext.id)
//    nvgRect(Window.nvgContext.id, -_testGui.frameBuffer().width / 4f, -_testGui.frameBuffer().height / 4f, _testGui.frameBuffer().width / 2f, _testGui.frameBuffer().height / 2f)
//    nvgStrokeColor(Window.nvgContext.id, nvgColorTwo)
//    nvgStroke(Window.nvgContext.id)
//
//    nvgBeginPath(Window.nvgContext.id)
//    nvgRect(Window.nvgContext.id, -_testGui.frameBuffer().width / 4f, -_testGui.frameBuffer().height / 4f, _testGui.frameBuffer().width / 2f, _testGui.frameBuffer().height / 2f)
//    nvgFillColor(Window.nvgContext.id, nvgColorOne)
//    nvgFill(Window.nvgContext.id)
//
//    nvgColorOne.free()
//    nvgColorTwo.free()
//
//    nvgEndFrame(Window.nvgContext.id)
  }
}
