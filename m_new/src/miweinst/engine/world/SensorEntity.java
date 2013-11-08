package miweinst.engine.world;

import java.util.ArrayList;

import miweinst.engine.entityIO.Connection;
import miweinst.engine.entityIO.Output;
import miweinst.engine.gfx.shape.Shape;

public class SensorEntity extends PhysicsEntity {	
	public final String string = "Entity";

	public Output onDetect;
	//Entities to watch whether collides with area
	private ArrayList<PhysicsEntity> _entities;
	
	public SensorEntity(GameWorld world) {
		super(world);
		onDetect = new Output();
		_entities = new ArrayList<PhysicsEntity>();
		this.setInteractive(false);
		this.setVisible(false);
	}
	
	/* Take in sensor area as Shape object, and an Entity that triggers Output
	 * when enters the area. Basic Sensor functionality, can override condition() 
	 * in subclasses.*/
	public SensorEntity(GameWorld world, Shape area, PhysicsEntity... other) {
		super(world);
		//Set Entity's shape as detection area
		this.setShape(area);
		onDetect = new Output();
		for (PhysicsEntity e: other) 
			_entities.add(e);
		//Invisible and non-interactive
		this.setInteractive(false);
		this.setVisible(false);
	}
	
	/**/
	public void setEntities(PhysicsEntity... other) {
		for (PhysicsEntity e: other) 
			_entities.add(e);
	}
	
	/* Pass a connection to define response when
	 * Sensor is activated, specifying input. Must connect
	 * Output onDetect to some other Input in order to 
	 * actually pass event.*/
	public void connect(Connection c) {
		onDetect.connect(c);
	}
		
	/* Generic collision sensor condition; can be overriden
	 * in subclasses. Checks if a player touches Sensor,
	 * which is a non-interactive entity, no collision response. 
	 * Condition checked on every tick in this.onTick(long)*/
	public boolean condition() {
		for (PhysicsEntity ent: _entities) {
			if (ent != null)
				if (this.collides(ent))
					return true;
		}
		return false;
	}
	
	/* Opens trigger when condition is met.*/
	@Override
	public void onTick(long nanosSincePreviousTick) {
		if (this.condition()) {
			onDetect.run();
		}
	}
	
	@Override
	public Output getOutput(String o) {
		if (new String("onDetect").equals(o)) {
			return onDetect;
		}
		return null;
	}
}
