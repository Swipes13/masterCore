package org.master.graphics.ui

import org.liquidengine.legui.style.border.SimpleLineBorder
import org.master.graphics.RTContext
import org.liquidengine.legui.event._
import org.liquidengine.legui.listener._
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer
import java.util

import org.joml.{Vector2i, Vector4f}
import org.liquidengine.legui.component._
import org.liquidengine.legui.component.event.selectbox.{SelectBoxChangeSelectionEvent, SelectBoxChangeSelectionEventListener}
import org.liquidengine.legui.style.Style.PositionType
import org.liquidengine.legui.system.renderer.Renderer
import org.master.core.Window
import org.liquidengine.legui.image.FBOImage
import org.liquidengine.legui.style.color.ColorConstants

import scala.collection.mutable.ArrayBuffer

class Gui {
  private var _renderer: Renderer = new NvgRenderer()
  private val _testWidth = Window.innerSize.width
  private val _testHeight = Window.innerSize.height
  private val _frame = new Frame(_testWidth, _testHeight)
  private var _rightPanel: Panel = _


  var rtContexts: ArrayBuffer[RTContext] = ArrayBuffer.empty[RTContext]

  def init(): Gui = {
    Window.addFrameForUpdate(_frame)
    createMainGui()
    _renderer.initialize()
    this
  }

  def update(): Unit = _renderer.render(_frame, Window.context)

  def destroy(): Unit = {
    rtContexts.foreach(RTContext.destroy)
    _renderer.destroy()
  }

  def addSelectBox(elements: Array[String]): Unit = {
    val selectBox: SelectBox = new SelectBox(5, 5, _rightPanel.getSize.x - 10, 20)
    elements.foreach(selectBox.addElement)
    selectBox.setVisibleCount(Math.min(elements.length, 5))
    selectBox.setElementHeight(20)
    selectBox.addSelectBoxChangeSelectionEventListener(new SelectBoxChangeSelectionEventListener {
      override def process(event: SelectBoxChangeSelectionEvent[_ <: SelectBox]): Unit = {
        println(event)
      }
    })
    _rightPanel.add(selectBox)
  }

  private def createMainGui(): Unit = {
    val rpBorderWidth = 2
    val rpWidth = 400
    val rpHeight: Int = Window.innerSize.height
    _rightPanel = new Panel(Window.innerSize.width - rpWidth + rpBorderWidth * 2, 0, rpWidth - 4 * rpBorderWidth, rpHeight)
    _rightPanel.getStyle.getBackground.setColor(new Vector4f(237 / 255.0f, 237 / 255.0f, 237 / 255.0f, 1))
    _rightPanel.getStyle.setBorder(new SimpleLineBorder(new Vector4f(157 / 255.0f, 157 / 255.0f, 157 / 255.0f, 1), rpBorderWidth))

    _frame.getContainer.add(_rightPanel)

    val rtSize = new Vector2i((Window.innerSize.width - rpWidth) / 2 - 6, Window.innerSize.height / 2 - 6)
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
    addSelectBox(Array("hello", "world", "qwer", "plz", "check", "6 elements"))
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
