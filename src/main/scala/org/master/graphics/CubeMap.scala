package org.master.graphics
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL13._

class CubeMap {
  val id: Int = glGenTextures()

  def bind(): CubeMap = { glBindTexture(GL_TEXTURE_CUBE_MAP, id); this }
}

object CubeMap {
  def create(filenames: Seq[String]): CubeMap = {
    val cube = new CubeMap().bind()

    for ((filename, i) <- filenames.zipWithIndex) {
      val data = Utils.loadPNGTexture(filename)
      val (width, height) = (data.size.width, data.size.height)
      glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data.buffer)
    }
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)

    cube
  }
  def bind(cubeMap: CubeMap): CubeMap = cubeMap.bind()
  def unbind(): Unit = glBindTexture(GL_TEXTURE_CUBE_MAP, 0)
}