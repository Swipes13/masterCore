package org.master.core

/**
  * Created by valentin on 25/02/2018.
  */

import org.lwjgl.Version
import org.master.graphics.Graphics
import org.master.window.Window

abstract class CoreUnit {
  def init(): Boolean
  def destroy(): Unit = {}
  def update(dt: Double): Unit = {}
}

class Core {
  def run(): Unit = {
    println(s"Master core run with version ${Version.getVersion}!")
    Core.units.foreach(_.init())
    Window.loop()
    Core.units.reverse.foreach(_.destroy())
  }
}

object Core {
  def apply() = new Core()
  def main(args: Array[String]): Unit = Core().run()
  val units = Array(Window, Graphics)
}
