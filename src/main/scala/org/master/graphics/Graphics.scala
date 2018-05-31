package org.master.graphics

/**
  * Created by valentin on 27/02/2018.
  */

import java.awt.image.BufferedImage
import java.io.{File, IOException}
import java.nio.ByteBuffer

import org.joml.Vector3f
import org.liquidengine.legui.component.Frame
import org.lwjgl.opengl.{GL, GLUtil}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengles.INTELFramebufferCMAA
import org.master.core.{CoreUnit, Window}
import org.master.core
import org.master.graphics.scene.{Node, Scene}
import org.master.graphics.ui.Gui
import org.master.http_client.QuasarClient
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.EXTFramebufferObject._
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import java.nio.ByteBuffer
import javax.imageio.ImageIO

class Graphics extends CoreUnit {
  private var _shaderPrograms = Array.empty[ShaderProgram]
  private var _projMatrix = new Matrix4fU().withName("projectionMatrix")
  private val _camera = new Camera(); _camera.withName("viewMatrix")
  private val _testRed = new Float1U(0.5f); _testRed.withName("red")
  private val _testLight = new Float3U(0, 0, -1); _testLight.withName("lightDir")
  private val _testView = new Float3U(0, 0, -1); _testView.withName("viewDir")

  private val _testGui = new Gui()

  private val _scene = new Scene(); _scene.node = Node.create()

  private var _textures = Array.empty[Texture2D]
  override def init(): Boolean = core.Utils.logging() {

    GL.createCapabilities
    GLUtil.setupDebugMessageCallback

    _testGui.init()

    glClearColor(0.05f, 0.45f, 0.45f, 0.0f)
    Window.context.updateGlfwWindow()
    val windowSize = Window.context.getWindowSize
    glViewport(0, 0, windowSize.x * 2, windowSize.y * 2)
//    glViewport(0, 0, Window.size.width * 2, Window.size.height * 2)
//    glShadeModel(GL_SMOOTH)
//    glClearDepth(1.0f)
    glEnable(GL_DEPTH_TEST)
//    glDepthFunc(GL_LEQUAL)
//    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    prepareShaders()
    addTextures()
    initTestScene()

    true
  }

  var framebufferID: Int = _
  var colorTextureID: Int = _
  var depthRenderBufferID: Int = _

  def initTestScene(): Unit = {
//    l.node = Node.create()
//    l.node.addComponent(l)
//    l.string = "test"
    _scene.node.addComponent(_scene)
//    _scene.node.addChild(l.node)

    framebufferID = glGenFramebuffersEXT // create a new framebuffer
    colorTextureID = glGenTextures // and a new texture used as a color buffer
    depthRenderBufferID = glGenRenderbuffersEXT // And finally a new depthbuffer

    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID) // switch to the new framebuffer
    glBindTexture(GL_TEXTURE_2D, colorTextureID) // Bind the colorbuffer texture
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR) // make it linear filterd
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 512, 512, 0, GL_RGBA, GL_INT, null.asInstanceOf[ByteBuffer]) // Create the texture data
    glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorTextureID, 0) // attach it to the framebuffer

    // initialize depth renderbuffer
    glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID) // bind the depth renderbuffer
    glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, 512, 512) // get the data space for it
    glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, depthRenderBufferID) // bind it to the renderbuffer

    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
  }

  def prepareShaders(): Unit = {
    val b = scala.collection.mutable.ListBuffer.empty[ShaderProgram]
    val colorSp = ShaderProgram.create(Array(
       Shader.create("shaders/v_cookTorrance.glsl", ShaderType.Vertex),
       Shader.create("shaders/f_cookTorrance.glsl", ShaderType.Fragment)
    ))
    addVaosToProgram(colorSp)
    b += colorSp
    _shaderPrograms = b.toArray
  }

  def addTextures(): Unit = {
    val b = scala.collection.mutable.ListBuffer.empty[Texture2D]
    b += Texture2D.create("textures/Star.png", TextureLocation._1)
    _textures = b.toArray
  }

  def addVaosToProgram(program: ShaderProgram): Unit = {
    val lineP = (Array(-1f, -1, 0,  1, -1, 1), 2)
    val lineC = (Array(1f, 1, 0, 0, 0, 1, 0, 1, 1), 3)
//    program.addVao(Vao.create(DrawType.LineLoop, 3, Array(lineP, lineC)))
    _projMatrix = Matrix4fU.perspective(60, 0.01f, 100).withName("projectionMatrix")

    program.updateLocForU(this._projMatrix)
    program.updateLocForU(this._camera)
    program.updateLocForU(this._testRed)
    program.updateLocForU(this._testView)
    program.updateLocForU(this._testLight)

    program.uniformLocs.foreach(println)

    val vertexes = Array(
      new Vertex(VertexElement(-1, -1), VertexElement(0, 0, 1), VertexElement(0, 0)),
      new Vertex(VertexElement(1, -1),  VertexElement(0, 0, 1), VertexElement(1, 0)),
      new Vertex(VertexElement(0, 1),   VertexElement(0, 0, 1), VertexElement(0, 1)),
      new Vertex(VertexElement(1, 1),   VertexElement(0, 0, 1), VertexElement(1, 1))
    )
//    program.addVao(Vao.createInterleaved(DrawType.Triangles, vertexes, Array[Int](0, 1, 2, 2, 1, 3)))
//    QuasarClient.init()
    val mesh = Mesh.fromFile("mesh/test.mesh")
    mesh.vao.foreach { case (physicalIndex, vao) =>
      if (physicalIndex == 1) program.addVao(vao)
    }

    mesh.nodes
  }

  override def update(dt: Double): Unit = {
    glBindTexture(GL_TEXTURE_2D, 0)                          // unlink textures because if we dont it all is gonna fail
    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID)        // switch to rendering on our FBO

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    _textures.foreach(t => t.bind())

    glEnable(GL_DEPTH_TEST)
    _shaderPrograms.foreach { p =>
      _projMatrix.set()
//      _testLight.update(_camera.look.negate(new Vector3f())).set()
      _testView.update(_camera.look.negate(new Vector3f())).set()
      _camera.updateWithRender()
      p.render()
    }


    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    _testGui.update(dt)

    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID)
//    Node.draw(_scene.node, dt)
  }

  def saveRtToFile(fileName: String) = {
    GL11.glReadBuffer(GL11.GL_FRONT)
    val width = Window.size.width
    val height = Window.size.height
    val bpp = 4
    // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
    val buffer = BufferUtils.createByteBuffer(width * height * bpp)
    GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)

    val file = new File(fileName)
    val format = "PNG"
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for(x <- 0 until width) {
      for(y <- 0 until height) {
        val i = (x + (width * y)) * bpp
        val r = buffer.get(i) & 0xFF
        val g = buffer.get(i + 1) & 0xFF
        val b = buffer.get(i + 2) & 0xFF
        image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b)
      }
    }

    ImageIO.write(image, format, file)
  }

  override def destroy(): Unit = {
    ShaderProgram.clear()
    _shaderPrograms.foreach(ShaderProgram.clear)
    _testGui.destroy()
  }

}

object Graphics extends Graphics() {}

