package org.master.core

/**
  * Created by valentin on 25/02/2018.
  */

import org.lwjgl
import org.master.graphics.Graphics

class Core {
  def run(): Unit = Utils.logging(s"Master core run with version ${lwjgl.Version.getVersion}!") {
    Core.units.foreach(CoreUnit.init)
    Window.loop(update)
    Core.runits.foreach(CoreUnit.destroy)
  }
  def update(delta: Double): Unit = Core.runits.foreach(_.update(delta))
}

object Core {
  def apply() = new Core()
  val units: Array[CoreUnit] = Array(Window, Graphics, Keys)
  val runits: Array[CoreUnit] = units.reverse
  def main(args: Array[String]): Unit = Core().run()
}
