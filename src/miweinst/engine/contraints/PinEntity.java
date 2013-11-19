package miweinst.engine.contraints;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

import cs195n.Vec2f;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.PolygonShape;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.world.*;

public class PinEntity extends PhysicsEntity {

	private Vec2f _pinOffsetFromCentroid;
	private Vec2f _pinLoc;
	private float _momentOfInertia;

	/*public PinEntity(GameWorld world) {
		super(world);
		_pinLoc = null;
		_momentOfInertia = 0f;
		setStatic(false);
	}
	*/

	public PinEntity(GameWorld world, Vec2f pinLoc, Shape shape) {
		super(world);
		setShape(shape);
		_pinLoc = pinLoc;
		System.out.println(this.getShape());
		init();
	}

	@Override
	public void onTick(long _nanosSincePreviousTick) {
		super.onTick(_nanosSincePreviousTick);
		//this.applyImpulse(new Vec2f(0f, -10000000f), getCentroid());
		translateToPin();
		setVelocity(new Vec2f(0, 0));
		//setAngle(getAngle() + ((float)Math.PI)/100f);
	}

	@Override
	public float getMomentOfInertia(float mass) {
		return super.getMomentOfInertia(mass);
	}

	@Override
	public Vec2f getCentroid() {
		return _pinLoc;
		//return _pinLoc;
	}


	/**
	 * requires pinLoc to be non-null. Assumes shape is in starting position.
	 */
	private void init() {
		assert(_pinLoc != null);

		Vec2f shapeCentroid = PolygonShape.getCentroidOf(Arrays.asList(((PolygonShape)getShape()).getVertices()));
		System.out.println(shapeCentroid);

		//init _pinOffsetFromCentroid
		_pinOffsetFromCentroid = _pinLoc.minus(shapeCentroid);

		//init _momentOfInertia;
		float pinOffsetMag = _pinOffsetFromCentroid.mag();
		float oldInertia = super.getMomentOfInertia(getMass());
		//add MR^2
		_momentOfInertia = oldInertia + getMass()*pinOffsetMag*pinOffsetMag;



	}

	private void translateToPin() {
		Vec2f shapeCentroid = getShape().getCentroid();
		//calculate the position of where the pin should be relative to the shape
		float theta = getAngle();
		float pinOnShapeX = (float)(Math.cos(theta)*_pinOffsetFromCentroid.x - 
				Math.sin(theta)*_pinOffsetFromCentroid.y);
		float pinOnShapeY = (float)(Math.sin(theta)*_pinOffsetFromCentroid.x + 
				Math.cos(theta)*_pinOffsetFromCentroid.y);
		Vec2f pinRelativeToShape = new Vec2f(pinOnShapeX, pinOnShapeY).plus(shapeCentroid);
		
		Vec2f translation = _pinLoc.minus(pinRelativeToShape);
		this.setLocation(this.getLocation().plus(translation));
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		CircleShape circle = new CircleShape(_pinLoc, 1);
		circle.setColor(Color.red);
		circle.draw(g);
		
		CircleShape centroid = new CircleShape(super.getCentroid(), 1);
		centroid.setColor(Color.blue);
		centroid.draw(g);
		
		
		/*PolygonShape thisShape = (PolygonShape)this.getShape();
		for(Vec2f v : thisShape.getVertices()) {
			CircleShape vCircle = new CircleShape(v, 1);
			vCircle.draw(g);
		}
		 */

	}



}
