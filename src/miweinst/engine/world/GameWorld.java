package miweinst.engine.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import miweinst.engine.App;
import miweinst.engine.Tuple;
import miweinst.engine.collisiondetection.Ray;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.screen.Viewport;
import cs195n.Vec2f;

/**
 * This class should be subclassed for any
 * game with its own GameWorld, larger
 * than the visible area.
 */

public class GameWorld {
	public final String string = "GameWorld";

	//Dimensions of game world
	private Vec2f _worldDim;	
	//Dimensions of Viewport
	private Vec2f _windowDim;
	private int _timestep;
	private int _iters;		
	private ArrayList<PhysicsEntity> _entities;		
		
	public Viewport _viewport;

	public GameWorld(App app, Viewport v) {
		//Default initialized dimensions; mutate in setDimensions()
		_worldDim = new Vec2f(375, 135);		
		_viewport = v;
		_windowDim = _viewport.getScreenSize();
		
		_entities = new ArrayList<PhysicsEntity>();
		
		//Initialize timestep to 5 ms
		_timestep = 5000000;
		_iters = 1;
	}
	
	/*For all Entities in world, do collision detection and 
	 * onTick methods; iterations calculated by fixed timestep.*/
	public void onTick(long nanosSincePreviousTick) {
		//accumulatedTime += nanosSincePreviousTick (or % timestep);
		//nanosSincePreviousTick+accumulatedTime (if %)
		_iters = (int) (nanosSincePreviousTick/_timestep);
		
		//Iterations of fixed timestep
		for (int iter=1; iter <= _iters; iter++) {
			//Collision detection
			for (int i=0; i<_entities.size(); i++) {
				for (int j=i; j < _entities.size(); j++) {	
					if (_entities.get(i) != _entities.get(j)) {
						//Check collisions on remaining pairs
						_entities.get(i).collides(_entities.get(j));
					}
				}
				if (_entities.get(i).isStatic() == false) 
					//Send tick to every entity in list
					_entities.get(i).onTick(nanosSincePreviousTick/_iters);
/////////				
//				Shape s = _entities.get(i).getShape();
//				s.setAngle((float) (s.getAngle()+Math.PI/100));
			}
		}
		// accumulatedTime -= _iters * _timestep; 
	}
	
	public int getIterations() {
		return _iters;
	}
	
	public int getTimestep() {
		return _timestep;
	}
	public void setTimestep(int step) {
		_timestep = step;
	}
	
	/* Returns size of Game World in game units */
	public Vec2f getDimensions() {
		return _worldDim;
	}
	public void setDimensions(Vec2f dim) {
		_worldDim = dim;
	}
	
	/*Stores reference to dimensions of Viewport
	 * window in pixels.*/
	public Vec2f getViewportDimensions() {
		return _windowDim;
	}
	
	/*Gets/Sets Viewport's scale.*/
	public void setScale(float newScale) {
		_viewport.setScale(newScale);
//		_scale = (int) newScale;
	}
	public float getScale() {
		return _viewport.getScale();
	}
	
	/*Accessor/Mutator for screen location of the game origin,
	 * used for conversion between game units and pixels for all
	 * measurements inside the GameWorld. */
	public Vec2f getPixelGameLocation() {
		return _viewport.getPixelGameLocation();
	}
	
	/* Calculates pixel equivalent of specified point
	 * in game units, based on pixelGameLocation that
	 * is passed in by Viewport. GameWorld is the 
	 * container of all game objects, which are 
	 * all set in game units. */
	public Vec2f toPixels(Vec2f gLoc) {
		return _viewport.gamePointToScreen(gLoc);
	}
	public Vec2f toUnits(Vec2f pxlLoc) {
		return _viewport.screenPointToGame(pxlLoc);
	}
	
	/*If specific game is using the math coordinate system (origin
	 * lower left), by toggling Viewport's setMathCoordinateSystem(bool),
	 * this method takes in a y-position and returns the y-position to use 
	 * for math coordinates (height of Viewport screen - y-pos).
	 * Only use in Game Units!*/
	public float getYforMathCoordinates(float y) {
		return getViewportDimensions().y/getScale()-y;
	}
	
	/**Methods to access and edit list of all PhysicsEntities in GameWorld.*/	
	/*Return ArrayList of all PhysicsEntities in world. Protected so
	 * other classes cannot mutate ArrayList directly.*/
	protected ArrayList<PhysicsEntity> getEntities() {
		return _entities;
	}	
	/* Set a new ArrayList of PhysicsEntities in world, replaces old _entities var. 
	 * Both protected because classes should not replace all Entities in GameWorld at once.
	 * Can only be mutated indirectly through add/remove methods.*/
	protected void setEntitiesList(ArrayList<PhysicsEntity> newList) {
		_entities = newList;
	}
	protected void setEntitiesArr(PhysicsEntity[] arr) {
		ArrayList<PhysicsEntity> newlist = new ArrayList<PhysicsEntity>();
		for (PhysicsEntity p: arr) 
			newlist.add(p);	
		_entities = newlist;
	}
	
	/*Return a java Array of PhysicsEntities currently in world. Public
	 * so other classes can access all Entities in GameWorld, but do not
	 * have access to original ArrayList.*/
	public PhysicsEntity[] getEntitiesToArr() {
		return _entities.toArray(new PhysicsEntity[_entities.size()]);
	}
	
	/*Add and remove PhysicsEntity from GameWorld.*/
	public void addEntity(PhysicsEntity e) {
		_entities.add(e);
	}
	public void removeEntity(PhysicsEntity e) {
		if (_entities.contains(e))
			_entities.remove(_entities.indexOf(e));
	}
	/*Adds Entity to front of ArrayList. Ensures that this
	 * Entity's collides method is the one being called in 
	 * each collision with it.*/
	public void addEntityToFront(PhysicsEntity e) {
		_entities.add(0, e);
	}
	
	/*Add multiple PhysicsEntities at once.*/
	public void addEntities(PhysicsEntity... toAdd) {
		for (PhysicsEntity e: toAdd) 
			_entities.add(e);
	}
	/*Add multiple PhysicsEntities to front.*/
	public void addEntitiesToFront(PhysicsEntity... toAddFront) {
		for (PhysicsEntity e: toAddFront) 
			_entities.add(0, e);
	}
	/*Remove multiple PhysicsEntities from list at once. Only
	 * removes the entities that are currently contained in world.*/
	public void removeEntities(PhysicsEntity... toRem) {
		for (PhysicsEntity e: toRem)
			this.removeEntity(e);
	}

	/*Add and remove all Entities in List to ArrayList in GameWorld.*/
	public void addEntitiesInList(List<PhysicsEntity> toAdd) {
		for (PhysicsEntity p: toAdd) 
			this.addEntity(p);
	}
	public void removeEntitiesInList(List<PhysicsEntity> toRemove) {
		for (PhysicsEntity p: toRemove)
			this.removeEntity(p);
	}
	
	/*Add and remove all Entities in Array*/
	public void addEntitiesInArr(PhysicsEntity[] toAdd) {
		for (PhysicsEntity e: toAdd) 
			_entities.add(e);
	}
	public void removeEntitiesInArr(PhysicsEntity[] toRemove) {
		for (PhysicsEntity e: toRemove) 
			this.removeEntity(e);
	}
	/*Return number of PhysicsEntities currently in GameWorld.*/
	public int numEntities() {
		return _entities.size();
	}
	
	/* Casts ray along line between Vec2f src and Vec2f dst against all
	 * the Entities in _shapes and returns the first PhysicsEntity to 
	 * get hit and the point of intersection with that Entity. Takes in an 
	 * Entity to skip, usually the entity from which ray is being cast, so
	 * collision will not be detected if it is colliding with skip. */
	public Tuple<PhysicsEntity, Vec2f> castRay(Vec2f src, Vec2f dst, PhysicsEntity skip) {		
		Tuple<PhysicsEntity, Vec2f> firstHit = null;		
		PhysicsEntity[] ents = this.getEntitiesToArr();
		for (PhysicsEntity ent: ents) {
			if (ent != skip && ent.isStatic()==false) {
				Ray ray = new Ray(src, dst);
				Vec2f cast = ray.cast(ent.getShape());
				if (cast != null) {
					if (firstHit == null || src.dist2(cast) < src.dist2(firstHit.y)) 
						firstHit = new Tuple<PhysicsEntity, Vec2f>(ent, cast);			
				}	
			}
		}	
		return firstHit;
	}
	
	/* This allows subclasses to send
	 * new Transform more specific to the game.
	 * GameWorld superclass forwards methods to and from
	 * the Viewport, where the Transform is actually applied.
	 * AffineTransform should really only be set by GameWorld object,
	 * not some sneaky Enemy or something, hence protected.*/
	protected AffineTransform getTransform() {
		return _viewport.getTransform();
	}
	protected void setTransform(AffineTransform transform) {
		_viewport.setTransform(transform);
	}
	
	
	/*Set properties of GameWorld based on the String it's mapped to,
	 * and the corresponding value stored as String.*/
	public void setProperties(Map<String, String> props) {
		if (props.containsKey("bgcolor")) {
			//WON'T WORK IF VIEWPORT SCREEN IS ALREADY SET TO COLOR OR TRANSPARENCY IN PLAYSCREEN!
			_viewport.setScreenColor(stringToColor(props.get("bgcolor")));
		}
		if (props.containsKey("gravity")) {
			PhysicsEntity.setGravity(Float.parseFloat(props.get("gravity")));
		}	
		if (props.containsKey("scale")) {
			this.setScale(Float.parseFloat(props.get("scale")));
		}
		//X coordinate of _viewport window in game world (game units)
		if (props.containsKey("x")) {
			_viewport.setScreenInGameLoc(new Vec2f(Float.parseFloat(props.get("x")), _viewport.getScreenInGameLoc().y));
		}
		//Y coordinate of _viewport window in game world (game units)
		if (props.containsKey("y")) {
			_viewport.setScreenInGameLoc(new Vec2f(_viewport.getScreenInGameLoc().x, Float.parseFloat(props.get("y"))));
		}
	}
	
	/**Static*/
	/*Turns a string into a Color. Takes String in form
	 * of Java color, i.e. "BLACK", "YELLOW". Also takes
	 * Strings in rgb form with syntax "r,g,b", no spaces. 
	 * If Color can't not be found to match either String, 
	 * returns Color.BLACK as default. Static for access, 
	 * and its a purely algorithmic method.*/
	public static Color stringToColor(String col) {
		Color color;
		//Strings of Java Colors (i.e. "BLACK", "YELLOW")
		try {
		    Field field = Color.class.getField(col);
		    color = (Color)field.get(null);
		} catch (Exception e) {
		    color = null; // Not defined
		}
		//Strings in form "r,g,b"
		if (color == null) {
			try {
				String[] rgb = col.split(",", 3);
				String r = rgb[0];
				String g = rgb[1];
				String b = rgb[2];				
				color = new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
			} catch (NumberFormatException e) {
				System.err.println("NumberFormatException: " + e.getMessage());
				color = Color.BLACK;
			}
		}
		return color;
	}
	
	/* Just draws all Entities currently in GameWorld's
	 * list of Entities. The AffineTransform and clipping
	 * both occur in Viewport's draw method, which is where
	 * this draw method is called from.*/
	public void draw(Graphics2D g) {		
		//Handle draw() for all Entities in GameWorld
		for (PhysicsEntity e: _entities) {
			e.draw(g);
		}
	}
}
