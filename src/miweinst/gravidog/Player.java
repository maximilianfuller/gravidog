package miweinst.gravidog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import miweinst.engine.FileIO;
import miweinst.engine.collisiondetection.PhysicsCollisionInfo;
import miweinst.engine.entityIO.Input;
import miweinst.engine.shape.CircleShape;
import miweinst.engine.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import cs195n.Vec2f;

public class Player extends PhysicsEntity {

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
	//SAVE AND LOAD IO
	//store data regarding player's progress in levell to be written to resources/save_data.txt
	public Input doStore = new Input() {
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
	public Input doWrite = new Input() {
		public void run(Map<String, String> args) {
			if (!_dataWritten) {
				FileIO.write(_saveData);
				_dataWritten = true;
			}
		}
	};
	//resets all save data to initial values; called on quit
	public Input doResetData = new Input() {
		public void run(Map<String, String> args) {
			_saveData.clear();
			setDataWritten(false);
			doWrite.run(args);
		}
	};
	//reads whether or not Player has reached checkpoint, and Player's prev color
	public Input doRead = new Input() {
		public void run(Map<String, String> args) {
			//i.e. if save_data loaded successfully
			if (args != null) {
				/* if (args.containsKey("color")) {
					setShapeColor(MWorld.stringToColor(args.get("color")));
				}*/
			}
		}
	};

	//	private GameWorld _world;
	private final float FRICTION_COEFFICIENT = 10;
	private final float _jumpImpulse;
	private Shape _shape;
	private List<String> _saveData;
	private boolean _gravitySwitched;
	private boolean _dataWritten;
	private HashMap<Integer, Integer> _stars;

	public Player(GameWorld world) {
		super(world);
		//		_world = world;
		Vec2f location = new Vec2f(50, 50);
		float radius = 5f;
		CircleShape shape = new CircleShape(location, radius);	

		//Pretty yellow
		Color col = new Color(235, 235, 110);	//Yellow pastel
		//Use bright yellow for now, so you can see player.
		shape.setColor(col);
		shape.setBorderWidth(.5f);
		shape.setBorderColor(Color.BLACK);
		
		_stars = new HashMap<Integer, Integer>();

		this.setShape(shape);
		this.setLocation(location);
		this.setMass(10f);
		_jumpImpulse = getMass()*120;
		
		this.setStatic(false);		
		_shape = shape;		
		_saveData = new ArrayList<String>();
		_gravitySwitched = false;
		_dataWritten = false;
	}
	
	/**Allows method to override the built in check
	 * on writing save data to file, specifically
	 * used to reset data.*/
	private void setDataWritten(boolean b) {
		_dataWritten = b;
	}
	
	/**Increments by one star on current level.*/	
	public void addStar() {
		_stars.put(LevelMenuScreen.CURRENT_LEVEL, getStars() + 1);
	}
	/** Returns 0 if lvl not contained in HashMap*/
	public int getStars() {
		int lvl = LevelMenuScreen.CURRENT_LEVEL;
		if (_stars.containsKey(lvl))
			return _stars.get(lvl);
		else
			return 0;
	}
	/**Returns stars earned at specified level number*/
	public int getStarsFor(int level) {
		return _stars.get(level);
	}
	
	@Override
	public void onTick(long nanosSincePreviousTick) {
		List<Vec2f> otherMTVs = new ArrayList<Vec2f>();
		super.onTick(nanosSincePreviousTick);
		List<PhysicsCollisionInfo> infos = getCollisionInfo();
		PhysicsCollisionInfo info = null;
		float max = 0;
		//Choose mtv with largest cross product with gravity
		for (PhysicsCollisionInfo i: infos) {
			if (i != null && i.other.isGravitational()) {
				float diffValue = Math.abs(i.mtv.normalized().cross(GRAVITY.normalized()));
				if(diffValue >= max) {
					info = i;
					max = diffValue;
				}
				otherMTVs.add(i.mtv);
			}
		}
		if (info != null) {
			Vec2f mtv = info.mtv;
			float mag = GRAVITY.mag();
						
			Vec2f mtv_norm = mtv.normalized();
			GRAVITY = mtv_norm.smult(-mag);

			//If gravity change between entities
			if(otherMTVs.size() >= 2) {
				/*Give player a small nudge perpindicular to mtv 
				* to avoid switching gravity on every tick
				* when the player is wedged in a corner. */
				Vec2f newPlatformMTV = info.mtv;
				Vec2f oldPlatformMTV = otherMTVs.get(0) == newPlatformMTV ? otherMTVs.get(1) : otherMTVs.get(0);
				Vec2f dir = newPlatformMTV.getNormal();
				if(dir.dot(oldPlatformMTV) < 0) {
					dir = dir.invert();
				}
				this.applyImpulse(dir.smult(this.getMass()*100), getCentroid());

			}			
		}
		if(info != null) {
			applyfriction();
		}
	}	

	private void applyfriction() {
		//Opposes movement perpindicular to gravity
		Vec2f gravityNormal = GRAVITY.getNormal();
		Vec2f force = getVelocity().projectOnto(gravityNormal).smult(-FRICTION_COEFFICIENT);
		this.applyForce(force, getCentroid());
	}

	/* Applies upward impulse if colliding with something by set _jumpImpulse
	 * value.*/
	public void jump() {
		//If there was a valid collision
		List<PhysicsCollisionInfo> infos = getCollisionInfo();
		PhysicsCollisionInfo info = null;
		for (PhysicsCollisionInfo i: infos) {
			if (i != null) {
				info = i;
			}
		}
		if (info != null) {
			Vec2f mtv = info.mtv;
			this.applyImpulse(mtv.normalized().smult(_jumpImpulse), getCentroid());
		}
	}

	/*Center of Player's body.*/
	public Vec2f getCenter() {
		return _shape.getCentroid();
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
