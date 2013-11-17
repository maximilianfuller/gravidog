package miweinst.engine.beziercurve;

import miweinst.engine.collisiondetection.SeparatingAxis;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.CompoundShape;
import miweinst.engine.gfx.shape.PolygonShape;
import cs195n.Vec2f;
import miweinst.engine.gfx.shape.Shape;

public class BezierCurve extends Shape {

	public BezierCurve(Vec2f loc, Vec2f dim) {
		super(loc, dim);
	}

	@Override
	public boolean collides(miweinst.engine.gfx.shape.Shape s) {
		return false;
	}

	@Override
	public boolean collidesCircle(CircleShape c) {
		return false;
	}

	@Override
	public boolean collidesAAB(AARectShape aab) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean collidesCompound(CompoundShape c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean collidesPolygon(PolygonShape p) {
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

	@Override
	public Vec2f getCentroid() {
		return null;
	}

	@Override
	public float getMomentOfInertia(float mass) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getArea() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vec2f poi(Shape s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec2f poiPolygon(PolygonShape p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec2f poiCircle(CircleShape c) {
		// TODO Auto-generated method stub
		return null;
	}
}
