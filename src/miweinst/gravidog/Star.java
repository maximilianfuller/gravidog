package miweinst.gravidog;

import java.util.Map;

import miweinst.engine.entityIO.Connection;
import miweinst.engine.entityIO.Input;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.WhileSensorEntity;

public class Star extends WhileSensorEntity {

	private GravidogWorld _gworld;
	private Player _player;
	private boolean _collected;
	public Star(GameWorld world) {
		super(world);
		
		_gworld = (GravidogWorld)world;
		_player = _gworld.getPlayer();
		super.setEntities(_player);
		
		this.setVisible(true);
		//Star can only be collected once
		_collected = false;
				
		//Connect Sensor.onDetect to World.doDoorReached
		super.onDetect.connect(new Connection(_gworld.doStarCollected));
		super.onDetect.connect(new Connection(new Input() 
		{
			public void run(Map<String, String> args) {
				setVisible(false);
				_collected = true;
			}
		}));
	}
	
	/**Partial override condition() to return
	 * false if star has already been
	 * collected (i.e. _collected == true). Otherwise
	 * returns super.condition()*/
	@Override
	public boolean condition() {
		boolean condition = super.condition();
		if (_collected) {
			return false;
		}
		return condition;
	}

	/**Partial override onTick() in order to set
	 * reference to Player if null
	 * at constructor.*/
	@Override
	public void onTick(long nanos) {
		super.onTick(nanos);
		//In case Star is instantiated before Player
		if (_player == null) {
			_player = _gworld.getPlayer();
			super.setEntities(_player);
		}
	}
}
