package miweinst.gravidog;

import java.awt.Color;
import java.awt.Graphics2D;

import miweinst.engine.entityIO.Connection;
import miweinst.engine.shape.AARectShape;
import miweinst.engine.shape.PolygonShape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.WhileSensorEntity;
import cs195n.Vec2f;

/*Just a SensorEntity that draws a Rectangle on top of it,
 * so no physical interactions just sensor detection.*/

public class GoalDoor extends WhileSensorEntity {
	
	
	public GoalDoor(GameWorld world) {
		super(world);
		
		GravidogWorld gworld = (GravidogWorld)world;
			
		super.setEntities(gworld.getPlayer());
		
		this.setVisible(true);
		
		//Connect Sensor.onDetect to World.doDoorReached
		super.onDetect.connect(new Connection(gworld.doDoorReached));
	}
}
