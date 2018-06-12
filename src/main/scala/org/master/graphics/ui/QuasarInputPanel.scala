package org.master.graphics.ui

import org.joml.{Vector3f, Vector4f}
import org.liquidengine.legui.component.misc.listener.textinput.TextInputCharEventListener
import org.liquidengine.legui.component._
import org.liquidengine.legui.component.event.checkbox.{CheckBoxChangeValueEvent, CheckBoxChangeValueEventListener}
import org.liquidengine.legui.component.event.slider.{SliderChangeValueEvent, SliderChangeValueEventListener}
import org.liquidengine.legui.event.{CursorEnterEvent, MouseClickEvent}
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction
import org.liquidengine.legui.input.Mouse.MouseButton
import org.liquidengine.legui.listener.{CursorEnterEventListener, MouseClickEventListener}
import org.liquidengine.legui.style.Style
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.master.http_client.{Point, QuasarClient, QuasarResultType}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}
import scala.util.control.Exception.allCatch

class QuasarInputPanel(x: Float, y: Float, width: Float) extends Panel(x, y, width, QuasarInputPanel.Height) {
  getStyle.getBackground.setColor(QuasarInputPanel.PanelColor)
  getStyle.setBorder(new SimpleLineBorder(QuasarInputPanel.BorderColor, QuasarInputPanel.BorderWidth))
  add(new VideoPanel(5, 500, 400))

  private var _resultNameSelectBox: SelectBox = _
  private var _areaPanel: AreaPanel = _

  def updateView(t: QuasarResultType.Value, elements: Seq[String]): QuasarInputPanel = {
    if (QuasarClient.resultTypeSelected != QuasarResultType.None) {
      _resultNameSelectBox.getElements.iterator().foreach(s => _resultNameSelectBox.removeElement(s))
      elements.foreach(_resultNameSelectBox.addElement)
      _resultNameSelectBox.setVisibleCount(Math.min(elements.length, 5))
      _resultNameSelectBox.setElementHeight(20)
      _resultNameSelectBox.setSelected(0, false)
      _areaPanel.disable()
    } else {
      val (p, sb) = Gui.selectBoxWithName(this.getSize.x, "Result name:", elements, updateAreaView)
      _resultNameSelectBox = sb
      _areaPanel = new AreaPanel(10, 40, width - 20)
      this.add(_areaPanel)
      this.add(p)
    }
    QuasarClient.resultTypeSelected = t
    this
  }

  def updateAreaView(resultName: String): Unit = {
    QuasarClient.resultNameSelected = resultName
    _areaPanel.enable()
  }
}

object QuasarInputPanel {
  val Height = 1000.0f
  val BorderWidth = 2
  val PanelColor = new Vector4f(237 / 255.0f, 237 / 255.0f, 237 / 255.0f, 1)
  val BorderColor = new Vector4f(127 / 255.0f, 127 / 255.0f, 127 / 255.0f, 1)
}

class AreaPanel(x: Float, y: Float, width: Float) extends Panel(x, y, width, AreaPanel.Height) {
  var inputs: ArrayBuffer[TextInput] = ArrayBuffer.empty[TextInput]
  var timeSlider: Slider = _
  var autoPlayCheckbox: CheckBox = _
  var requestProgress: ProgressBar = _

  def enable(): Unit = inputs.foreach(_.setEditable(true))
  def disable(): Unit = inputs.foreach(_.setEditable(false))

  def min: Vector3f = vecFromInput(0)
  def max: Vector3f = vecFromInput(3)

  def vecFromInput(offset: Int): Vector3f = {
    val v = inputs.slice(offset, 3 + offset).map(_.getTextState.getText.toFloat)
    new Vector3f(v(0), v(1), v(2))
  }

  def initView(): Unit = {
    val minstr = Array("-0.25","-0.25","-0.25")
    val maxstr = Array("4","1","3.25")
    for (i <- Range(0, 2)) {
      val label = new Label(5, 5 + i * 30, 40, 20)
      label.getTextState.setText(if (i == 0) "min:" else "max:")
      this.add(label)
      for (j <- Range(0, 3)) {
        inputs += new TextInput(55 + j * 55, 5 + i * 30, 50, 20)
        inputs.last.getTextState.setText(if (i == 0) minstr(j) else maxstr(j))
        this.add(inputs.last)
      }
    }

    { // point density [6]
      val label = new Label(5, 5 + 80, 50, 20); this.add(label)
      label.getTextState.setText("point density:")
      inputs += new TextInput(120, 5 + 80, 70, 20)
      inputs.last.getTextState.setText("12")
      this.add(inputs.last)
    }
    { // time density: [7]
      val label = new Label(5, 5 + 100, 50, 20); this.add(label)
      label.getTextState.setText("time density:")
      inputs += new TextInput(120, 5 + 105, 70, 20)
      inputs.last.getTextState.setText("10")
      this.add(inputs.last)
    }


    { // auto play checkbox
      val label = new Label(5, 5 + 135, 50, 20); this.add(label)
      label.getTextState.setText("auto play:")
      autoPlayCheckbox = new CheckBox(120, 140, 225, 24)
      autoPlayCheckbox.setChecked(QuasarClient.resultView.autoPlay)
      autoPlayCheckbox.getTextState.setText("Enabled")
      autoPlayCheckbox.getListenerMap.addListener(classOf[CheckBoxChangeValueEvent[_ <: CheckBox]], new CheckBoxChangeValueEventListener {
        override def process(event: CheckBoxChangeValueEvent[_ <: CheckBox]): Unit = QuasarClient.resultView.autoPlay = event.isNewValue
      })
      QuasarClient.autoPlayCheckbox = Some(autoPlayCheckbox)
      this.add(autoPlayCheckbox)
    }
    { // time slider
      val label = new Label(5, 5 + 165, 50, 20); this.add(label)
      label.getTextState.setText("time value:")
      timeSlider = new Slider(120, 170, 225, 24)
      timeSlider.getListenerMap.addListener(classOf[SliderChangeValueEvent[_ <: Slider]], new SliderChangeValueEventListener {
        override def process(event: SliderChangeValueEvent[_ <: Slider]): Unit = {
          QuasarClient.resultView.update(event.getNewValue, forced = true)
        }
      })
      QuasarClient.timeValueSlider = Some(timeSlider)
      this.add(timeSlider)
    }

    val buttonWidth = 100
    val buttonCompute = new Button(width - buttonWidth - 10, AreaPanel.Height - 105, buttonWidth, 25)
    buttonCompute.getTextState.setText("Get Result!")
    buttonCompute.getListenerMap.addListener(classOf[MouseClickEvent[_ <: Component]], new MouseClickEventListener {
      override def process(event: MouseClickEvent[_ <: Component]): Unit = {
        if (event.getButton == MouseButton.MOUSE_BUTTON_1 && event.getAction == MouseClickAction.RELEASE) {
          if (checkInputsDouble()) {
            println("ready to send")
            // TODO: check volume non zero
            // TODO: density to ui
            QuasarClient.pointDensity = inputs(6).getTextState.getText.toInt
            QuasarClient.timeDensity = inputs(7).getTextState.getText.toInt
            val density = QuasarClient.pointDensity
            val timeDensity = QuasarClient.timeDensity
            val step = max.sub(min, new Vector3f()).div(density)
            var points = ArrayBuffer.empty[Vector3f]
            for (i <- 0 until density; j <- 0 until density; k <- 0 until density) {
              points += min.add(step.mul(new Vector3f(i, j, k), new Vector3f()))
            }
            import scala.concurrent.ExecutionContext.Implicits.global
            val inverseFuture : Future[Unit] = Future {
              QuasarClient.updateResultView(points.toArray, timeDensity)
            }
          } else {
            // TODO: Show error input label
          }
        }
      }
    })
    this.add(buttonCompute)

    {
      val label = new Label(5, AreaPanel.Height - 45, 50, 20); this.add(label)
      label.getTextState.setText("request progress:")
      val requestPgBarW = 200
      requestProgress = new ProgressBar(width - requestPgBarW - 10, AreaPanel.Height - 45, requestPgBarW, 25)
      requestProgress.setValue(0)
      this.add(requestProgress)
      QuasarClient.requestProgress = Some(requestProgress)
    }

  }

  def checkInputsDouble(): Boolean = inputs.map(_.getTextState.getText).forall(text => text.nonEmpty && (allCatch opt text.replace(',', '.').toDouble).isDefined)
  def checkInputsInt(): Boolean = inputs.map(_.getTextState.getText).forall(text => text.nonEmpty && (allCatch opt text.replace(',', '.').toInt).isDefined)

  initView()
  disable()
}

object AreaPanel {
  val Height = 360
}