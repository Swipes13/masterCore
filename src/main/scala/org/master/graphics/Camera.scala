package org.master.graphics

import org.joml.{Matrix4f, Vector3f}
import org.master.core.{KeyType, Keys, MousePos}

class Camera extends Matrix4fU {
  val deltaCf = 0.01f
  Keys.addKeyPressCb(KeyType.W, () => walk(-deltaCf))
  Keys.addKeyPressCb(KeyType.A, () => strafe(-deltaCf))
  Keys.addKeyPressCb(KeyType.S, () => walk(deltaCf))
  Keys.addKeyPressCb(KeyType.D, () => strafe(deltaCf))
  Keys.addKeyPressCb(KeyType.LShift, () => speed = 5)
  Keys.addKeyReleaseCb(KeyType.LShift, () => speed = 1)
  Keys.addMousePosCb((pos: MousePos) => { pitch(-pos.y/100); rotY(pos.x/100) })

  var position = new Vector3f(0, 0, 1)
  var look = new Vector3f(0, 0, -1)
  var right = new Vector3f(1, 0, 0)
  var up = new Vector3f(0, 1, 0)
  var speed = 1.0f

  var needToPrint = true

  def updateWithRender(location: Int = this.location): Unit = { update(); set(location) }
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

  def walk(delta: Float): Vector3f = {
    position.add(look.mul(delta * speed, new Vector3f()))
  }
  def strafe(delta: Float): Vector3f = position.add(right.mul(delta * speed, new Vector3f()))
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
