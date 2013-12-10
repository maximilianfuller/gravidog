package miweinst.gravidog;

import java.awt.Color;

import miweinst.engine.shape.CircleShape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import cs195n.Vec2f;

public class Boulder extends PhysicsEntity {
	public Boulder(GameWorld world) {
		super(world);
		//		_world = world;
		Vec2f location = new Vec2f(100, 50);
		float radius = 20f;
		CircleShape shape = new CircleShape(location, radius);	

		//Pretty yellow
		Color col = Color.GRAY;	//Yellow pastel
		//Use bright yellow for now, so you can see player.
		shape.setColor(col);
		shape.setBorderWidth(.5f);
		shape.setBorderColor(Color.BLACK);

		this.setShape(shape);
		this.setLocation(location);
		this.setStatic(false);
		
		this.setDensity(.2f);
		this.setRestitution(.6f);
		
		this.setGravitational(false);
	}
}
