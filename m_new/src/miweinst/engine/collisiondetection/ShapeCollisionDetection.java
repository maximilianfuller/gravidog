package miweinst.engine.collisiondetection;

import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.CompoundShape;
import miweinst.engine.gfx.shape.PolygonShape;
import miweinst.engine.gfx.shape.Shape;

public interface ShapeCollisionDetection {	
	boolean collides(Shape s);
	boolean collidesCircle(CircleShape c);
	boolean collidesAAB(AARectShape aab);
	boolean collidesCompound(CompoundShape c);
	boolean collidesPolygon(PolygonShape p);
}
