package miweinst.engine.gfx.shape;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import cs195n.Vec2f;

/**This class is currently abstract because collision
 * and containment are not implemented. It is simply
 * the superclass for the currently implemented CircleShape.*/

public abstract class delete_EllipseShape extends Shape {
	public static final String string = "EllipseShape";

	private Ellipse2D.Float _ellipse;

	public delete_EllipseShape(Vec2f loc, Vec2f dim) {
		super(loc, dim);
		_ellipse = new Ellipse2D.Float(loc.x, loc.y, dim.x, dim.y);
		super.setShape(_ellipse);
	}
	
	/*Returns Vec2f storage of point at center*/
/*	public Vec2f getCenter() {
		return new Vec2f((float)_ellipse.getCenterX(), (float)_ellipse.getCenterY());
	}*/
	
	/*Returns the Maximum and Minimum X and Y values of rect frame.
	 * Does not correspond to values on ellipse perimeter.*/
/*	public float getMaxX() {
		return (float)_ellipse.getMaxX();
	}
	public float getMaxY() {
		return (float) _ellipse.getMaxY();
	}
	public float getMinX() {
		return (float) _ellipse.getMinX();
	}
	public float getMinY() {
		return (float) _ellipse.getMinY();
	}*/
	
	/*Returns precision bounding box which entirely contains ellipse*/
	public Rectangle2D getBounds() {
		return _ellipse.getBounds2D();
	}
	
	/*Returns a copy of this shape. Useful for repetitive features*/
	public Ellipse2D.Float clone() {
		return (Ellipse2D.Float)_ellipse.clone();
	}
	
	public void draw(Graphics2D g) {
		_ellipse.setFrame(super.getX(), super.getY(), super.getWidth(), super.getHeight());
		super.draw(g);
	}

	public abstract boolean contains(Vec2f v);
	
		
	//Implementation of Collision Detection shapeCollisionDetection; double dispatch
	@Override
	public abstract boolean collides(Shape s);
	
	@Override
	public abstract boolean collidesCircle(CircleShape c);
	
	@Override
	public abstract boolean collidesAAB(AARectShape aab);
	
	@Override
	public abstract boolean collidesCompound(CompoundShape c);
	
	@Override
	public abstract boolean collidesPolygon(PolygonShape p);
}
