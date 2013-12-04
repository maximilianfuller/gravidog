package miweinst.gravidog;

import java.util.Map;

import miweinst.engine.entityIO.Connection;
import miweinst.engine.entityIO.Input;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.WhileSensorEntity;

public class Star extends WhileSensorEntity {

	public Star(GameWorld world) {
		super(world);
		
		GravidogWorld gworld = (GravidogWorld)world;
			
		super.setEntities(gworld.getPlayer());

		this.setVisible(true);
		
		//Connect Sensor.onDetect to World.doDoorReached
		super.onDetect.connect(new Connection(gworld.starCollected));
		super.onDetect.connect(new Connection(new Input() {

			public void run(Map<String, String> args) {
				setVisible(false);
			}
		
		}));
	}

}
