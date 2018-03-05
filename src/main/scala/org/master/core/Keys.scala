package org.master.core

import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWKeyCallback

/**
  * Created by valentin on 25/02/2018.
  */
object Keys extends Keys()

class Keys extends CoreUnit {
  override def init(): Boolean = Utils.logging() {
    glfwSetKeyCallback(Window.ptr, (window: Long, key: Int, scanCode: Int, action: Int, mods: Int) => {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true)
    })
    true
  }
}
