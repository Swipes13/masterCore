package org.master.keys

import org.lwjgl.glfw.GLFW._
import org.master.core.CoreUnit
import org.master.window.Window

/**
  * Created by valentin on 25/02/2018.
  */
object Keys extends Keys()

class Keys extends CoreUnit {
  override def init(): Boolean = {
    glfwSetKeyCallback(Window.ptr, (window: Long, key: Int, scanCode: Int, action: Int, mods: Int) => {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true)
    })
    true
  }
}
