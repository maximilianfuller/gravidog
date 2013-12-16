package miweinst.gravidog;

import java.awt.Color;
import java.util.Map;

import cs195n.Vec2f;
import miweinst.engine.shape.AARectShape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;

public class StaticBoundary extends PhysicsEntity {
	public static final String string = "StaticBoundary";

	private float _thickness;
	public StaticBoundary(GameWorld world) {
		super(world);	
		this.setStatic(true);
	}
	
	public float getThickness() {
		return _thickness;
	}
	
	@Override
	public void setProperties(Map<String, String> map) {
		super.setProperties(map);
		this.getShape().setBorderWidth(3f);
	}
	
}
