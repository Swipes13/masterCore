package org.master.graphics.scene

import java.awt.Font

import org.joml.Vector3f
import org.newdawn.slick.{Color, TrueTypeFont}
import org.newdawn.slick.util.ResourceLoader

class LabelTTF extends Component {
  private var _font: Option[TrueTypeFont] = None

  var string: String = ""
  var color: Color = Color.white

  def setFont(font: Font, antialias: Boolean): Unit = _font = Some(new TrueTypeFont(font, antialias))
  def update(dt: Double): Unit = {
    val p = Some(node).map(_.position).getOrElse(new Vector3f())
    _font.foreach(_.drawString(p.x, p.y, string, color))
  }
}

object LabelTTF {
  def create(fontSize: Int, antialias: Boolean): LabelTTF = {
    try {
      val l = new LabelTTF()

      import org.newdawn.slick.util.ResourceLoader
      import java.awt.Font
      val inputStream = ResourceLoader.getResourceAsStream("fonts/test.ttf")

      var awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream)
      awtFont2 = awtFont2.deriveFont(fontSize) // set font size

      l.setFont(awtFont2, antialias)
      l
    } catch {
      case e: Exception => println(e); null
    }
  }
}

//            GL11.glEnable(GL11.GL_TEXTURE_2D);
//            GL11.glShadeModel(GL11.GL_SMOOTH);       
//            GL11.glDisable(GL11.GL_DEPTH_TEST);
//            GL11.glDisable(GL11.GL_LIGHTING);                   
//      
//            GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);               
//            GL11.glClearDepth(1);                                      
//      
//            GL11.glEnable(GL11.GL_BLEND);
//            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//      
//            GL11.glViewport(0,0,width,height);
//            GL11.glMatrixMode(GL11.GL_MODELVIEW);
//      
//            GL11.glMatrixMode(GL11.GL_PROJECTION);
//            GL11.glLoadIdentity();
//            GL11.glOrtho(0, width, height, 0, 1, -1);
//            GL11.glMatrixMode(GL11.GL_MODELVIEW);
