package miweinst.engine.contraints;

import java.awt.Color;
import java.awt.Graphics2D;

import cs195n.Vec2f;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.PolygonShape;
import miweinst.engine.world.*;

public class PinEntity extends PhysicsEntity {

	private Vec2f _pinOffsetFromCentroid;
	private Vec2f _pinLoc;
	private float _momentOfInertia;

	public PinEntity(GameWorld world) {
		super(world);
		_pinLoc = null;
		_momentOfInertia = 0f;
		setStatic(false);
	}

	public PinEntity(GameWorld world, Vec2f pinLoc) {
		super(world);
		_pinLoc = pinLoc;
		init();
	}

	@Override
	public void onTick(long _nanosSincePreviousTick) {
		super.onTick(_nanosSincePreviousTick);
		//translateToPin();
		//setAngle(getAngle() + ((float)Math.PI)/100f);
	}

	@Override
	public float getMomentOfInertia(float mass) {
		return super.getMomentOfInertia(mass);
	}

	@Override
	public Vec2f getCentroid() {
		return getShape().getCentroid();
		//return _pinLoc;
	}
	

	/**
	 * requires pinLoc to be non-null. Assumes shape is in starting position.
	 */
	private void init() {
		assert(_pinLoc != null);

		Vec2f shapeCentroid = getShape().getCentroid();

		//init _momentOfInertia;
		float pinOffset = shapeCentroid.minus(_pinLoc).mag();
		float oldInertia = super.getMomentOfInertia(getMass());
		//add MR^2
		_momentOfInertia = oldInertia + getMass()*pinOffset*pinOffset;

		//init _pinOffsetFromCentroid
		_pinOffsetFromCentroid = shapeCentroid.minus(_pinLoc);

	}

	private void translateToPin() {
		Vec2f shapeCentroid = getShape().getCentroid();
		//calculate the position of where the pin should be relative to the shape
		float theta = getAngle();


		float pinOnShapeX = (float)(Math.cos(theta)*_pinOffsetFromCentroid.x - 
				Math.sin(theta)*_pinOffsetFromCentroid.y + 
				shapeCentroid.x);
		float pinOnShapeY = (float)(Math.sin(theta)*_pinOffsetFromCentroid.x + 
				Math.cos(theta)*_pinOffsetFromCentroid.y + 
				shapeCentroid.y);
		Vec2f translation = _pinLoc.minus(new Vec2f(pinOnShapeX, pinOnShapeY));
		this.setLocation(this.getLocation().plus(translation));
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		CircleShape circle = new CircleShape(_pinLoc, 1);
		circle.setColor(Color.red);
		circle.draw(g);
		/*PolygonShape thisShape = (PolygonShape)this.getShape();
		for(Vec2f v : thisShape.getVertices()) {
			CircleShape vCircle = new CircleShape(v, 1);
			vCircle.draw(g);
		}
		*/
		
	}



}
