package miweinst.m;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import miweinst.engine.FileIO;
import miweinst.engine.entityIO.Input;
import miweinst.engine.gfx.shape.CircleShape;
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
	//Input classes: public anonymous
	//doWin calls playerWin if argument is true (as safety, if false won't trigger)
	public Input doWin = new Input() 
	{
		public void run(Map<String, String> args) {
			if (args.containsKey("condition")) 
				if (Boolean.parseBoolean(args.get("condition")))
					((MWorld) _world).playerWin();
		}
	};
	//doLose calls playerLose if argument is true (as safety, if false won't trigger)
	public Input doLose = new Input()
	{
		public void run(Map<String, String> args) {
			if (args.containsKey("condition")) 
				if (Boolean.parseBoolean(args.get("condition")))
					((MWorld) _world).playerLose();
		}
	};
	//switch Gravity
	public Input doGravitySwitch = new Input()
	{
		public void run(Map<String, String> args) {
			if (!_gravitySwitched) {
				PhysicsEntity.GRAVITY*= -1;
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
				if (args.containsKey("checkpoint")) {
					boolean reached = Boolean.parseBoolean(args.get("checkpoint"));
					if (reached) 
						setLocation(new Vec2f(-21.45f, 188.16f));
				}
				if (args.containsKey("color")) {
					setShapeColor(MWorld.stringToColor(args.get("color")));
				}
			}
		}
	};
	
	private GameWorld _world;
	private CircleShape _shape;
	private Grenade _grenade;
	private List<String> _saveData;
	private boolean _gravitySwitched;
	private boolean _dataWritten;

	public Player(GameWorld world) {
		super(world);
		_world = world;
		_grenade = null;
		Vec2f location = new Vec2f(50, 50);
		CircleShape shape = new CircleShape(location, 5f);		
		
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
		if (_grenade != null) {
			_grenade.onTick(nanosSincePreviousTick);
			if (_grenade.isDead()) {
				_world.removeEntity(_grenade);
				_grenade = null;
			}
		}
	}	
	
	/*Center of Player's body when circular.*/
	public Vec2f getCenter() {
		return new Vec2f(this.getLocation().x + _shape.getRadius(), this.getLocation().y + _shape.getRadius());
	}
	
	/*Creates a new grenade and gives it an impulse in the direction of mouse cursor.*/
	public Grenade tossGrenade(Vec2f mouseLoc) {
		if (_grenade == null) {
			_grenade = new Grenade(_world, this);
			Vec2f dir = mouseLoc.minus(this.getCenter()).normalized();
			_grenade.setLocation(_grenade.getLocation().plus(dir.smult(_shape.getRadius())));
			_grenade.applyImpulse(dir.smult(22), _grenade.getCentroid());
		}
		return _grenade;
	}
	
	/*If there is a grenade in mid-flight that the player has thrown.*/
	public boolean hasActiveGrenade() {
		return _grenade != null;
	}
	
	/* Returns whether or not player is on solid object/ground,
	 * so it is able to jump. Checks if MTV of most recent collision
	 * was positive and very very small, therefore standing
	 * still with an upwards normal force.*/
	public boolean isStable() {
		//MTV of last collision
		Vec2f mtv = this.getLastMTV();	
		if (mtv != null) 
			if (Math.abs(mtv.x) < Math.abs(mtv.y)) 
				if (mtv.y >= 0 && mtv.y <= .01f)  
					return true;
		return false;
	}
	
	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		if (_grenade != null) 
			_grenade.draw(g);
	}

///////	
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
		if (new String("doWin").equals(s)) {
			return doWin;
		}
		if (new String("doLose").equals(s)) {
			return doLose;
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
		System.out.println("No input found (Player.getInputOf)");
		return null;
	}

	/* Takes in a String of RGB components of a color, and changes
	 * color of the Player's shape according to corresponding integer.*/
	public void setColor(String rgb) {	
		this.setShapeColor(MWorld.stringToColor(rgb));
	}
	public void setBorder(String width, String color) {
		getShape().setBorderWidth(Float.parseFloat(width));
		this.getShape().setBorderColor(MWorld.stringToColor(color));
	}
}
