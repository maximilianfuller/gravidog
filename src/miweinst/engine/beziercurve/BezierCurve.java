package miweinst.engine.beziercurve;

import miweinst.engine.collisiondetection.SeparatingAxis;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.CompoundShape;
import miweinst.engine.gfx.shape.PolygonShape;
import cs195n.Vec2f;
import miweinst.engine.gfx.shape.Shape;

public abstract class BezierCurve extends Shape {

	public BezierCurve(Vec2f loc, Vec2f dim) {
		super(loc, dim);
	}

	@Override
	public boolean collides(miweinst.engine.gfx.shape.Shape s) {
		return s.collidesCurve(this);
	}

	@Override
	public abstract boolean collidesAAB(AARectShape aab);

	@Override
	public boolean collidesCompound(CompoundShape c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Vec2f pnt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vec2f projectOnto(SeparatingAxis sep) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean collidesCurve(BezierCurve c) {
		// TODO Auto-generated method stub
		return false;
	}

	/*No momentOfInertia because BezierCurve should
	 * not be rotatable and is always static.*/
	@Override
	public float getMomentOfInertia(float mass) {
		return 0;
	}

	@Override
	public float getArea() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public Vec2f poiCurve(BezierCurve c) {
		// TODO Won't have curve-curve collisions ever!?
		return null;
	}
	
	/*No centroid in BezierCurve, which is OK
	 * because it should always be static!*/
	@Override
	public abstract Vec2f getCentroid();
	
	@Override
	public abstract boolean collidesPolygon(PolygonShape p);
	
	@Override
	public abstract boolean collidesCircle(CircleShape c);

	@Override
	public abstract Vec2f poi(Shape s);

	@Override
	public abstract Vec2f poiPolygon(PolygonShape p);

	@Override
	public abstract Vec2f poiCircle(CircleShape c);
}
