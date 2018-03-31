package org.master.graphics

import org.joml.{Matrix4f, Vector3f}
import org.master.core.{KeyType, Keys}

class Camera extends Matrix4fUniform {
  val deltaCf = 0.01f
  Keys.keyPressFuncs += (KeyType.W.id, () => walk(-deltaCf))
  Keys.keyPressFuncs += (KeyType.A.id, () => strafe(-deltaCf))
  Keys.keyPressFuncs += (KeyType.S.id, () => walk(deltaCf))
  Keys.keyPressFuncs += (KeyType.D.id, () => strafe(deltaCf))
  Keys.mousePosFuncs = Keys.mousePosFuncs :+ ((x: Double, y: Double) => {
    println(x, y)
    pitch(y/100); rotY(x/100)
  })

  var position = new Vector3f(0, 0, 1)
  var look = new Vector3f(0, 0, -1)
  var right = new Vector3f(1, 0, 0)
  var up = new Vector3f(0, 1, 0)

  var needToPrint = true

  def updateWithRender(location: Int = this.location): Unit = { update(); render(location) }
  def update(): Unit = { // TODO: optimize with dirty flag
    look.normalize()
    look.cross(right, up).normalize()
    up.cross(look, right).normalize() // TODO: remove normalize and check

    m00(right.x); m10(right.y); m20(right.z); m30(-position.dot(right))
    m01(up.x);    m11(up.y);    m21(up.z);    m31(-position.dot(up))
    m02(look.x);  m12(look.y);  m22(look.z);  m32(-position.dot(look))

    if (needToPrint) {
      println(right.x)
      println(this)
      needToPrint = false
    }
  }

  def walk(delta: Float): Vector3f = position.add(look.mul(delta, new Vector3f()))
  def strafe(delta: Float): Vector3f = position.add(right.mul(delta, new Vector3f()))
  def pitch(delta: Double): Unit = {
    val r = new Matrix4f().rotation(delta.toFloat, right)
    r.transformDirection(up)
    r.transformDirection(look)
  }
  def rotY(delta: Double): Unit = {
    val r = new Matrix4f().rotationY(delta.toFloat)
    r.transformDirection(right)
    r.transformDirection(up)
    r.transformDirection(look)
  }
}
