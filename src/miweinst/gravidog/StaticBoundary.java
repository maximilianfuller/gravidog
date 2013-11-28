package miweinst.gravidog;

import java.awt.Color;

import cs195n.Vec2f;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;

public class StaticBoundary extends PhysicsEntity {
	public static final String string = "StaticBoundary";

	private float _thickness;
	public StaticBoundary(GameWorld world) {
		super(world);
		_thickness = 100;
		//Wider than game world by _thickness on each side, overlaps with other boundaries
		AARectShape _rect = new AARectShape(new Vec2f(-_thickness, -(_thickness-1)), new Vec2f(world.getDimensions().x + _thickness*2, _thickness));
		_rect.setColor(new Color(192, 237, 237));	//Pale Blue
		_rect.setBorderWidth(.4f);
		_rect.setBorderColor(Color.BLACK);
		
		this.setShape(_rect);		
		this.setMass(10000); //unnecessary, but y'know
		this.setStatic(true);
	}
	
	public float getThickness() {
		return _thickness;
	}
}
