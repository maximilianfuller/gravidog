package miweinst.gravidog;

import java.awt.Color;
import java.awt.Graphics2D;

import miweinst.engine.entityIO.Connection;
import miweinst.engine.shape.AARectShape;
import miweinst.engine.shape.PolygonShape;
import miweinst.engine.world.WhileSensorEntity;
import cs195n.Vec2f;

/*Just a SensorEntity that draws a Rectangle on top of it,
 * so no physical interactions just sensor detection.*/

public class GoalDoor extends WhileSensorEntity {
	
	private PolygonShape _doorRect;
	
	public GoalDoor(GravidogWorld world, Player player) {
		super(world);
			
		super.setEntities(player);
		super.setShape(_doorRect);
		
		_doorRect = new AARectShape(new Vec2f(0, 0), new Vec2f(0, 0)).rectToPoly();
		_doorRect.setOutline(Color.RED, 15f);
		
		//Connect Sensor.onDetect to World.doDoorReached
		super.onDetect.connect(new Connection(world.doDoorReached));
	}
	
	
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		_doorRect.draw(g);
	}
}
