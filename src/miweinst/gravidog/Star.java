package miweinst.gravidog;

import java.util.Map;

import miweinst.engine.entityIO.Connection;
import miweinst.engine.entityIO.Input;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.WhileSensorEntity;

public class Star extends WhileSensorEntity {

	private GravidogWorld _gworld;
	private Player _player;
	public Star(GameWorld world) {
		super(world);
		
		_gworld = (GravidogWorld)world;
		_player = _gworld.getPlayer();
		super.setEntities(_player);
		
		this.setVisible(true);
		
		//Connect Sensor.onDetect to World.doDoorReached
		super.onDetect.connect(new Connection(_gworld.starCollected));
		super.onDetect.connect(new Connection(new Input() 
		{
			public void run(Map<String, String> args) {
				setVisible(false);
			}
		}));
	}

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
