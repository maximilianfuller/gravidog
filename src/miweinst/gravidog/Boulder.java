package miweinst.gravidog;

import java.awt.Color;
import java.util.Map;

import miweinst.engine.entityIO.Connection;
import miweinst.engine.entityIO.Input;
import miweinst.engine.shape.CircleShape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import miweinst.engine.world.WhileSensorEntity;
import cs195n.Vec2f;

public class Boulder extends WhileSensorEntity {
	
	GravidogWorld _world;
	public Boulder(GameWorld world) {
		super(world);
		_world = (GravidogWorld) world;

		this.setDensity(.2f);
		this.setRestitution(.6f);
		super.onDetect.connect(new Connection(new Input() 
		{
			public void run(Map<String, String> args) {
				
			}
		}));
		this.setGravitational(false);
	}
}
