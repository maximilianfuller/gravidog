package miweinst.gravidog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import miweinst.engine.FileIO;
import miweinst.engine.beziercurve.CubicBezierCurve;
import miweinst.engine.entityIO.Input;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import cs195n.Vec2f;

public class Player extends PhysicsEntity {
	public static final String string = "Player";

	//Inputs: defined as anonymous classes, only one method to override
	//doSetColor sets Player color; args "color"
	public Input doSetColor = new Input()
	{
		public void run(Map<String, String> args) {
			setColor(args.get("color"));
		}
	};
	//doSetBorder sets border attr.; args "border_width", "border_color"
	public Input doSetBorder = new Input()
	{
		public void run(Map<String, String> args) {
			setBorder(args.get("border_width"), args.get("border_color"));
		}
	};
	//switch Gravity
	public Input doGravitySwitch = new Input()
	{
		public void run(Map<String, String> args) {
			if (!_gravitySwitched) {
				PhysicsEntity.GRAVITY = GRAVITY.smult(-1);
				_gravitySwitched = true;
			}
		}
	};
	//reset gravitySwitched so doGravitySwitch can run
	public Input doResetGravitySwitch = new Input()
	{
		public void run(Map<String, String> args) {
			_gravitySwitched = false;
		}
	};
	//store data regarding player's progress in levell to be written to resources/save_data.txt
	public Input doStore = new Input() 
	{
		/* For checkpoint saving in M4, doWrite actually called at same
		 * time as data storage. So for M4, doWrite not run from connection 
		 * but rather from doStore.run*/
		public void run(Map<String, String> args) {
			if (args.containsKey("checkpoint"))
				_saveData.add(new String("checkpoint: " + args.get("checkpoint")));
			
			//Store Player's color for restoration at save.
			Color curr = getShapeColor();
			String col = new String(Integer.toString(curr.getRed()) + "," +  Integer.toString(curr.getGreen()) + "," + Integer.toString(curr.getBlue()));
			_saveData.add(new String("color: " + col));
			
			doWrite.run(args);
		}
	};
	//write any stored data to resources/save_data.txt, only once
	public Input doWrite = new Input()
	{
		public void run(Map<String, String> args) {
			if (!_dataWritten) {
				FileIO.write(_saveData);
				_dataWritten = true;
			}
		}
	};
	//resets all save data to initial values; called on quit
	public Input doResetData = new Input() 
	{
		public void run(Map<String, String> args) {
			_saveData.clear();
			setDataWritten(false);
			doWrite.run(args);
		}
	};
	//reads whether or not Player has reached checkpoint, and Player's prev color
	public Input doRead = new Input()
	{
		public void run(Map<String, String> args) {
			//i.e. if save_data loaded successfully
			if (args != null) {
/*				if (args.containsKey("checkpoint")) {
					boolean reached = Boolean.parseBoolean(args.get("checkpoint"));
					if (reached) 
						setLocation(new Vec2f(-21.45f, 188.16f));
				}
				if (args.containsKey("color")) {
					setShapeColor(MWorld.stringToColor(args.get("color")));
				}*/
			}
		}
	};
	
//	private GameWorld _world;
	private Shape _shape;
	private List<String> _saveData;
	private boolean _gravitySwitched;
	private boolean _dataWritten;
	//collisionTimer measures ms between collisions
	private float _collisionTimer;
	//jumpDelay is delay before Player can jump again
	private float _jumpDelay;
	//boolean for whether jump is allowed
	private boolean _jumping;

	public Player(GameWorld world) {
		super(world);
//		_world = world;
		Vec2f location = new Vec2f(50, 50);
		float radius = 5f;
		CircleShape shape = new CircleShape(location, radius);	

/*///////TEST PLAYER AS POLYGON SHAPE
		Vec2f[] verts = new Vec2f[6];
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
		PolygonShape shape = new PolygonShape(verts);*/
		
		//Pretty yellow
		Color col = new Color(235, 235, 110);	//Yellow pastel
		//Use bright yellow for now, so you can see player.
		shape.setColor(col);
		shape.setBorderWidth(.5f);
		shape.setBorderColor(Color.BLACK);
		
		this.setShape(shape);
		this.setLocation(location);
		this.setMass(40f);
		this.setStatic(false);		
		_shape = shape;		
		_saveData = new ArrayList<String>();
		_gravitySwitched = false;
		_dataWritten = false;
		this.setRestitution(100f);
		_jumpDelay = 5;
		_collisionTimer = 0;
		_jumping = false;		
	}
	
	/*Allows method to override the built in check
	 * on writing save data to file, specifically
	 * used to reset data.*/
	private void setDataWritten(boolean b) {
		_dataWritten = b;
	}
	
	@Override
	public void onTick(long nanosSincePreviousTick) {		
		super.onTick(nanosSincePreviousTick);
		_collisionTimer += nanosSincePreviousTick/1000000;
		_jumpDelay += nanosSincePreviousTick/1000000;
		Vec2f mtv = getLastMTV();
		//Gravity follows Player only on curved surfaces. Otherwise touching an enemy/object switches it.
		if (getShape().getCollisionInfo() != null) {
			Shape other = getShape().getCollisionInfo().getOther();
			if (other instanceof CubicBezierCurve) {
				if (mtv != null) {
					float mag = GRAVITY.mag();
					Vec2f mtv_norm = mtv.normalized();
					//Reverse direction of MTV by putting negative sign in front of mag
					GRAVITY = mtv_norm.smult(-mag);		
				}
			}
		}
	}	
	
	@Override
	public boolean collides(PhysicsEntity other) {
		boolean collision = super.collides(other);	
		if (!other.isInteractive()) 
			collision = false;
		if (collision) {
			//if collisions are < 6 ms apart, Player is touching an object
			if (_collisionTimer <= 14) 
				_jumping = false;
			//starts timer on collision
			_collisionTimer = 0;
		}
		return collision;
	}
	
	public void jump() {
		Vec2f mtv = this.getLastMTV();
		if (mtv != null) {
			//This condition was here to not jump off vertical walls, but now has to be described relative to current Gravity...
//			if (Math.abs(mtv.y) > Math.abs(mtv.x)/2) {
				if (!_jumping && _jumpDelay > 50) {
					this.applyImpulse(mtv.normalized().smult(5000), getCentroid());
					_jumping = true;
					_jumpDelay = 0;
				}
//			}
		}
	}
	
	/*Center of Player's body.*/
	public Vec2f getCenter() {
		return _shape.getCentroid();
		//When circular
//		return new Vec2f(this.getLocation().x + _shape.getRadius(), this.getLocation().y + _shape.getRadius());
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
	}

	@Override
	public Input getInput(String s) {
		if (new String("doGravitySwitch").equals(s)) {
			return doGravitySwitch;
		}
		if (new String("doResetGravitySwitch").equals(s)) {
			return doResetGravitySwitch;
		}
		if (new String("doSetColor").equals(s)) {
			return doSetColor;
		}
		if (new String("doSetBorder").equals(s)) {
			return doSetBorder;
		}
		if (new String("doStore").equals(s)) {
			return doStore;
		}
		if (new String("doWrite").equals(s)) {
			return doWrite;
		}
		if (new String("doRead").equals(s)) {
			return doRead;
		}
		System.err.println("No input found (Player.getInputOf)");
		return null;
	}

	/* Takes in a String of RGB components of a color, and changes
	 * color of the Player's shape according to corresponding integer.*/
	public void setColor(String rgb) {	
		this.setShapeColor(GameWorld.stringToColor(rgb));
	}
	public void setBorder(String width, String color) {
		getShape().setBorderWidth(Float.parseFloat(width));
		this.getShape().setBorderColor(GameWorld.stringToColor(color));
	}
}
