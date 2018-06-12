package org.master.graphics.ui

import org.liquidengine.legui.component.{Button, Component, Panel}
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction
import org.liquidengine.legui.input.Mouse.MouseButton
import org.liquidengine.legui.listener.MouseClickEventListener
import org.master.core.Window
import org.master.graphics.{Graphics, ScreenRecorder}

class VideoPanel (x: Float, y: Float, width: Float) extends Panel(x, y, width, VideoPanel.Height) {
  var sr: Option[ScreenRecorder] = None
  def initView(): Boolean = {
    val buttonWidth = 100
    val btnStart = new Button(width - buttonWidth - 30, 5, buttonWidth, 25)
    btnStart.getTextState.setText("Start record!")
    btnStart.getListenerMap.addListener(classOf[MouseClickEvent[_ <: Component]], new MouseClickEventListener {
      override def process(event: MouseClickEvent[_ <: Component]): Unit = {
        if (event.getButton == MouseButton.MOUSE_BUTTON_1 && event.getAction == MouseClickAction.RELEASE) {
          sr match {
            case None =>
              sr = Some(ScreenRecorder.create("test.mp4", Window.size.width, Window.size.height))
              btnStart.getTextState.setText("Finish record!")
              Graphics.updateFuncs :+= ((dt: Double) => sr.get.update(dt))
            case Some(s) =>
              s.finish()
              Graphics.updateFuncs = Array.empty // TODO: hack!
              sr = None
              btnStart.getTextState.setText("Start record!")
          }

        }
      }
    })
    this.add(btnStart)
  }
  initView()
}

object VideoPanel {
  val Height = 100
}
