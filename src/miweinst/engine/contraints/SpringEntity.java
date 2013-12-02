package miweinst.engine.contraints;

import cs195n.Vec2f;
import miweinst.engine.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;

public class SpringEntity extends PhysicsEntity {

	private Vec2f _pivot;
	private float _springConstant;
	private float _frictionConstant;
	
	public SpringEntity(GameWorld world, Shape shape) {
		super(world);
		setShape(shape);
		init();
		
	}
	
	
	
	@Override
	public void onTick(long nanosSincePreviousTick) {
		super.onTick(nanosSincePreviousTick);
		applyRestorativeForce();
		applyFriction();
		
		
	}
	
	private void applyRestorativeForce() {
		Vec2f displacementFromPivot = _pivot.minus(getLocation());
		float restorativeForce = displacementFromPivot.smult(_springConstant).mag();
		Vec2f dir = displacementFromPivot.normalized();
		applyForce(dir.smult(restorativeForce), this.getCentroid());
	}
	
	private void applyFriction() {
		float force = getVelocity().mag() * _frictionConstant;
		Vec2f dir = getVelocity().invert().normalized();
		applyForce(dir.smult(force), this.getCentroid());
	}
	
	
	/**
	 * requires shape to be set
	 */
	private void init() {
		_pivot = getShape().getCentroid();
		setRotatable(false);
	}
	
	public void setSpringConstant(float springConstant) {
		_springConstant = springConstant;
	}
	
	public void setFrictionConstant(float frictionConstant) {
		_frictionConstant = frictionConstant;
	}
			

}
