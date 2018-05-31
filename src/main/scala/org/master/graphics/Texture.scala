package org.master.graphics

import org.lwjgl.opengl.GL11._

class Texture {
  val id: Int = glGenTextures

  def bind(): Texture = { glBindTexture(GL_TEXTURE_2D, id); this }
}

object Texture {
  def create(width: Int, height: Int): Texture = {
    val texture = new Texture().bind()
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    texture
  }
}