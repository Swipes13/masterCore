package org.master.graphics

/**
  * Created by valentin on 27/02/2018.
  */

import org.joml.Vector3f
import org.lwjgl.opengl.{GL, GLUtil}
import org.lwjgl.opengl.GL11._
import org.master.core.{CoreUnit, Window}
import org.master.core
import org.master.graphics.scene.{LabelTTF, Node, Scene}

class Graphics extends CoreUnit {
  private var _shaderPrograms = Array.empty[ShaderProgram]
  private var _projMatrix = new Matrix4fU().withName("projectionMatrix")
  private val _camera = new Camera(); _camera.withName("viewMatrix")
  private val _testRed = new Float1U(0.5f); _testRed.withName("red")
  private val _testLight = new Float3U(0, 0, -1); _testLight.withName("lightDir")

  private val _scene = new Scene(); _scene.node = Node.create()

  private var _textures = Array.empty[Texture2D]
  override def init(): Boolean = core.Utils.logging() {
    GL.createCapabilities
    GLUtil.setupDebugMessageCallback
    glClearColor(0.05f, 0.45f, 0.45f, 0.0f)
    glViewport(0, 0, Window.size.width * 2, Window.size.height * 2)
//    glShadeModel(GL_SMOOTH)
//    glClearDepth(1.0f)
    glDisable(GL_DEPTH_TEST)
//    glDepthFunc(GL_LEQUAL)
//    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST)

    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    prepareShaders()
    addTextures()
    initTestScene()
    true
  }

  def initTestScene(): Unit = {
//    val l = LabelTTF.create(24, antialias = true)
//    l.node = Node.create()
//    l.node.addComponent(l)
//    l.string = "test"
    _scene.node.addComponent(_scene)
//    _scene.node.addChild(l.node)
  }

  def prepareShaders(): Unit = {
    val b = scala.collection.mutable.ListBuffer.empty[ShaderProgram]
    val colorSp = ShaderProgram.create(Array(
       Shader.create("shaders/simplev.glsl", ShaderType.Vertex),
       Shader.create("shaders/simplef.glsl", ShaderType.Fragment)
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

    program.uniformLocs.foreach(println)

    val vertexes = Array(
      new Vertex(VertexElement(-1, -1), VertexElement(0, 0, 1), VertexElement(0, 0)),
      new Vertex(VertexElement(1, -1),  VertexElement(0, 0, 1), VertexElement(1, 0)),
      new Vertex(VertexElement(0, 1),   VertexElement(0, 0, 1), VertexElement(0, 1)),
      new Vertex(VertexElement(1, 1),   VertexElement(0, 0, 1), VertexElement(1, 1))
    )
//    program.addVao(Vao.createInterleaved(DrawType.Triangles, vertexes, Array[Int](0, 1, 2, 2, 1, 3)))
    val mesh = Mesh.fromFile("mesh/1.mesh")
    mesh.vao.foreach(program.addVao)
  }

  override def update(dt: Double): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    _textures.foreach(t => t.bind())
    _shaderPrograms.foreach { p =>
      _projMatrix.set()
      _testLight.update(_camera.look.negate(new Vector3f())).set()
      _camera.updateWithRender()
      p.render()
    }

//    Node.draw(_scene.node, dt)
  }

  override def destroy(): Unit = {
    ShaderProgram.clear()
    _shaderPrograms.foreach(ShaderProgram.clear)
  }

}

object Graphics extends Graphics() {}

