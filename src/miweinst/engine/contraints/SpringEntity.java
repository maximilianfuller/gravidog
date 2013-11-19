package miweinst.engine.contraints;

import cs195n.Vec2f;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;

public class SpringEntity extends PhysicsEntity {

	private Vec2f _pivot;
	private float _springConstant;
	
	public SpringEntity(GameWorld world, Shape shape) {
		super(world);
		_pivot = shape.getCentroid();
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public void onTick(long nanosSincePreviousTick) {
		super.onTick(nanosSincePreviousTick);
		
	}

}
