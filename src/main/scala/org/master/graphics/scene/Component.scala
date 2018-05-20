package org.master.graphics.scene

trait Component {
  var node: Node = _

  def update(dt: Double)
}
