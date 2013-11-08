package miweinst.m;

import java.awt.Color;

import cs195n.Vec2f;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.PolygonShape;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;

public class MTestObject extends PhysicsEntity {
	public static final String string = "MTestObject";

	public MTestObject(GameWorld world, Vec2f location, int type) {
		super(world);
				
		float radius = 2f;	
		//Hexagon
		Vec2f[] verts = new Vec2f[6]; //counter-clockwise order
		//Upper right
		verts[0] = new Vec2f(location.x + radius*2, location.y);
		//Upper left
		verts[1] = new Vec2f(location.x, location.y);
		//Middle left
		verts[2] = new Vec2f(location.x - radius*2, location.y + radius*2);
		//Bottom left
		verts[3] = new Vec2f(location.x, location.y + 4*radius);
		//Bottom right
		verts[4] = new Vec2f(location.x + radius*2, location.y + 4*radius);
		//Middle right
		verts[5] = new Vec2f(location.x + 4*radius, location.y + 2*radius);
		PolygonShape poly = new PolygonShape(location, verts);
		poly.setColor(new Color(250, 30, 30));
		poly.setBorderWidth(.25f);
		poly.setBorderColor(Color.GRAY);
		
		Shape shape = null;
		//Lower mass Polygon, salmon
		if (type==0) {
			shape = new PolygonShape(location, verts);
			shape.setColor(new Color(226, 98, 82));
			this.setMass(.5f);
			this.setRestitution(1f);
		}
		//Lower mass Square, green
		else if (type==1) {
			shape = new AARectShape(location, new Vec2f(10, 10));
//			shape = new AARectShape(location, new Vec2f(10, 10)).rectToPoly();
			shape.setColor(new Color(254, 175, 175));	//Green
			this.setMass(.6f);
			this.setRestitution(0f);
		}	
		//Greater mass Circle, turquoise
		else if (type==2) {
			shape = new CircleShape(location, 8.5f); 
			shape.setColor(new Color(54, 85, 93));	//turquoise
			this.setMass(2);
			this.setRestitution(0);
		}				
		if (shape != null) this.setShape(shape);
		this.setLocation(location);
		this.setStatic(false);
	}
}
