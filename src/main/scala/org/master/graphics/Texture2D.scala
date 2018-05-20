package org.master.graphics

import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._

object TextureLocation extends Enumeration {
  type TextureLocation = Value
  val None = 0
  val _1: TextureLocation.Value = Value(GL_TEXTURE0)
  val _2: TextureLocation.Value = Value(GL_TEXTURE1)
  val _3: TextureLocation.Value = Value(GL_TEXTURE2)
  val _4: TextureLocation.Value = Value(GL_TEXTURE3)
}

class Texture2D(val id: Int, val location: TextureLocation.Value) {
  def bind(): Texture2D = {
    glActiveTexture(location.id)
    glBindTexture(GL_TEXTURE_2D, id)
    this
  }
}

object Texture2D {
  def create(filename: String, location: TextureLocation.Value): Texture2D = {
    val texture = new Texture2D(glGenTextures, location).bind()

    loadBuffer(Utils.loadPNGTexture(filename))
    genMipMaps()
    setWrapParams()
    setFilterParams()

    texture
  }

  def loadBuffer(b: BufferWithSize): Unit = {
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, b.size.width, b.size.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, b.buffer)
  }
  def genMipMaps(): Unit = glGenerateMipmap(GL_TEXTURE_2D)
  def setWrapParams(): Unit = {
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
  }
  def setFilterParams(): Unit = {
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
  }
}
