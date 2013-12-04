package miweinst.gravidog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import miweinst.engine.App;
import miweinst.engine.FileIO;
import miweinst.engine.beziercurve.BezierCurveEntity;
import miweinst.engine.beziercurve.CurvedPathEntity;
import miweinst.engine.contraints.PinEntity;
import miweinst.engine.contraints.SpringEntity;
import miweinst.engine.entityIO.Connection;
import miweinst.engine.entityIO.Input;
import miweinst.engine.entityIO.Output;
import miweinst.engine.graph.HashDecorator;
import miweinst.engine.screen.Viewport;
import miweinst.engine.shape.AARectShape;
import miweinst.engine.shape.CircleShape;
import miweinst.engine.shape.PolygonShape;
import miweinst.engine.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import miweinst.engine.world.RelayEntity;
import miweinst.engine.world.WhileSensorEntity;
import cs195n.CS195NLevelReader;
import cs195n.CS195NLevelReader.InvalidLevelException;
import cs195n.LevelData;
import cs195n.LevelData.ConnectionData;
import cs195n.LevelData.EntityData;
import cs195n.LevelData.ShapeData;
import cs195n.LevelData.ShapeData.Type;
import cs195n.Vec2f;

public class GravidogWorld extends GameWorld {    

	public Input doDoorReached = new Input() 
	{
		public void run(Map<String, String> args) {
			_app.setScreen(new LevelMenuScreen(_app));
		}
	};

	private App _app;
	private Player _player;
	//Player movement using boolean state array
	private boolean[] _arrowKeyStates;
	//Lazor visualization for raycasting
	private Path2D.Float _lazor;
	private boolean _lazorBool;        
	//Class.string mapped to instance of Class<?>
	private HashDecorator<String, Class<? extends PhysicsEntity>> _classes;
	//Variable name mapped to PhysicsEntity instance
	private HashDecorator<String, PhysicsEntity> _entities;

	public GravidogWorld(App app, Viewport viewport, File f) {
		super(app, viewport);
		_app = app;
		_arrowKeyStates = new boolean[4];
		for (int i=0; i<_arrowKeyStates.length; i++) 
			_arrowKeyStates[i]=false;
		_lazorBool = false;

		////////////// START LEVEL READER /////////////////////

		//Map of Strings to Class<?>, for interpreting level data
		_classes = new HashDecorator<String, Class<? extends PhysicsEntity>>();                
		_classes.setDecoration("PhysicsEntity", PhysicsEntity.class);
		_classes.setDecoration("Player", Player.class);
		_classes.setDecoration("StaticBoundary", StaticBoundary.class);
		_classes.setDecoration("WhileSensorEntity", WhileSensorEntity.class);
		_classes.setDecoration("RelayEntity", RelayEntity.class);
		_classes.setDecoration("BezierCurveEntity", BezierCurveEntity.class);
		_classes.setDecoration("CurvedPathEntity", CurvedPathEntity.class);
		_classes.setDecoration("PinEntity", PinEntity.class);
		_classes.setDecoration("SpringEntity", SpringEntity.class);
		_classes.setDecoration("GoalDoor", GoalDoor.class);        

		///Decoration set to each Entity read from LevelEditor!
		_entities = new HashDecorator<String, PhysicsEntity>();                                        

		//        	File f = new File("src/miweinst/resources/level_one.nlf");
		LevelData level = null;
		try {
			level = CS195NLevelReader.readLevel(f);
		}
		catch (InvalidLevelException le) {
			System.err.println("The level you loaded is invalid!! MWorld()");
			le.printStackTrace();
		}
		catch (FileNotFoundException fe) {
			System.err.println("File not found!! MWorld()");
			fe.printStackTrace();
		}
		
		if (level != null) {
			//Properties of entire level
			this.setProperties(level.getProperties());

			//Each Entity in Level
			for (EntityData ent: level.getEntities()) {
				//Make instance of PhysicsEntity
				String entityClass = ent.getEntityClass();
				String entityName = ent.getName();
				//Create new Entity instance out of Class 
				PhysicsEntity entity = null;
				try {
					Constructor<?> c = _classes.getDecoration(entityClass).getConstructor(GameWorld.class);
					entity = (PhysicsEntity) c.newInstance(this);
				} catch (Exception e) {
					System.err.println("Exception...: " + e.getMessage());
					e.printStackTrace();
				}                                                                                
				//Cast PhysicsEntity to specific subclass
				if (entity instanceof Player) {
					//        				entity = _player;
					_player = (Player) entity;
				}       
				else if (entity instanceof WhileSensorEntity) {
					WhileSensorEntity playerSensor = (WhileSensorEntity) entity;
					playerSensor.setEntities(_player);
				}                        
				if (entity != null) {
					//Shapes in Entity
					for (ShapeData s: ent.getShapes()) {
						Type shapeType = s.getType();
						Shape shape = null;
						if (shapeType == Type.CIRCLE){
							float rad = s.getRadius();
							shape = new CircleShape(s.getCenter(), rad);
						} else if (shapeType == Type.BOX) {
							shape = new AARectShape(s.getMin(), new Vec2f(s.getWidth(), s.getHeight()));
						} else if (shapeType == Type.POLY) {
							shape = new PolygonShape(PolygonShape.getCentroidOf(s.getVerts()), s.getVerts().toArray(new Vec2f[s.getVerts().size()]));
						}
						//Parse Shape properties in Entity
						if (shape != null) {                                                        
							//Set properties of Shape
							shape.setProperties(s.getProperties());                                                        
							//Add Shape to Entity
							entity.setShape(shape);
						}                
					}
					//Set PhysicsEntity properties                                                
					entity.setProperties(ent.getProperties());

					//Add Entity to World Map
					_entities.setDecoration(entityName, entity);
					//Add Entity to GameWorld List
					this.addEntity(entity);
				}
			}                                
			//Each Connection in Level
			for (ConnectionData c: level.getConnections()) {
				String src = c.getSource();
				String srcOut = c.getSourceOutput();
				String dst = c.getTarget();
				String dstIn = c.getTargetInput();

				PhysicsEntity source = null;
				PhysicsEntity target = null;
				if (_entities.contains(src)) 
					source = _entities.getDecoration(src);
				else System.err.println("Connection source " + src + " does not exist!");
				if (_entities.contains(dst)) 
					target = _entities.getDecoration(dst);
				else System.err.println("Connection target " + dst + " does not exist!");

				if (source != null && target != null) {                
					Connection toAdd = null;
					Output onOut = source.getOutput(srcOut);
					Input doIn = target.getInput(dstIn);
					if (doIn != null) {
						//Pass in Input target to constructor
						toAdd = new Connection(doIn);        
						//Connect Output source here
						if (onOut !=  null) 
							onOut.connect(toAdd);
						else System.err.println("Source " + src + " has no output " + srcOut);
					}
					else System.err.println("Target " + dst + " has no input " + dstIn);
					//If valid Connection, parse Connection properties
					if (toAdd != null) {
						//Properties of connection
						toAdd.setProperties(c.getProperties());
					}
				}
			}
		} else {
			System.err.println("Level is null! MWorld()");
		}
		
		/////////////////^^^^^^^^^^                

		//Constraint Entities to test stuff
		/*        		Shape pinEntityShape = new AARectShape(new Vec2f(50f, 60f), new Vec2f(15f, 4f)).rectToPoly();
                PinEntity pin = new PinEntity(this, new Vec2f(50f, 60f), pinEntityShape);
                pin.setMass(1f);
                this.addEntity(pin);

                Shape springEntityShape = new AARectShape(new Vec2f(134f,80f), new Vec2f(10f, 10f)).rectToPoly();
                SpringEntity spring = new SpringEntity(this, springEntityShape);
                spring.setMass(1f);
                spring.setSpringConstant(100f);
                spring.setFrictionConstant(1f);
                this.addEntity(spring);
		 */

		/*PolygonShape entityShape = new AARectShape(new Vec2f(134f,80f), new Vec2f(10f, 10f)).rectToPoly();
            PhysicsEntity test = new PhysicsEntity(this);
            test.setShape(entityShape);
            test.setMass(1f);
            this.addEntity(test);
		 */
				//Square to test stuff with
				/*            PolygonShape entityShape = new AARectShape(new Vec2f(134f,80f), new Vec2f(10f, 10f)).rectToPoly();
            PhysicsEntity test = new PhysicsEntity(this);
            test.setShape(entityShape);
            test.setMass(1f);
            this.addEntity(test);*/

				//Get initial distance of Player from screen origin (game units) to maintain panning onTick
				//        	_deltaPlayerPos = new Vec2f(_player.getX() - super.viewport.getScreenInGameLoc().x, _player.getY() - super.viewport.getScreenInGameLoc().y);

				//Restore save_data
				/* MICHAEL--
				 * COMMENTING OUT BECAUSE OF NULL POINTER EXCEPTION WHEN TRYING TO MAKE A NEW LEVEL
				 * 
				 */
				//_player.doRead.run(FileIO.read());
	}

	/*Called when game is quit.*/
	public void quitReset() {
		_player.doResetData.run(new HashMap<String, String>());
	}

	/* Calls tick based on fixed timestep. Passes in an
	 * adjusted nanosSincePreviousTick so speed of entity
	 * movement remains relatively constant regardless of timestep.
	 * Collision response of Entities handled. And movement 
	 * based on boolean state array mutated in onKeyPressed.*/
	@Override
	public void onTick(long nanosSincePreviousTick) {
		super.onTick(nanosSincePreviousTick);
		//        	long nanos = nanosSincePreviousTick/super.getIterations();
		for (int i=1; i<=super.getIterations(); i++) {
			//Conditions by array of boolean key states
			Vec2f norm = PhysicsEntity.GRAVITY.getNormal();
			norm = norm.normalized();
			//Left key down
			if (_arrowKeyStates[0]) {
				_player.goalVelocity(norm.smult(-3000));
			}
			//Up key down
			if (_arrowKeyStates[1]) {
				//        			_player.jump();
			}
			//Right key down
			if (_arrowKeyStates[2]) {
				_player.goalVelocity(norm.smult(3000));
			}
			//Down key down
			if (_arrowKeyStates[3]) {
				_player.goalVelocity(PhysicsEntity.GRAVITY.normalized().smult(3000));           
			}
		}
	}

	/*Remove Entity from GameWorld*/
	@Override
	public void removeEntity(PhysicsEntity toRem) {                
		super.removeEntity(toRem);
	}

	/*Updates AffineTransform of Viewport's draw() method, in case panning or zooming
	 * has changed scale or pxlgameloc. Draws Path2D lazor just to visualize raycasting.*/
	@Override
	public void draw(Graphics2D g) {                
		super.draw(g);                        
		//Draw Path2D lazor while space bar is held down; raycast visualizer
		if (_lazorBool && _lazor != null) {
			Color col = g.getColor();
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(.25f));
			g.draw(_lazor);
			g.setColor(col);
		}
	}

	//USER INPUT
	/* Arrow key sets state boolean which opens
	 * calling of goalVelocity in onTick.*/
	public void onKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == 38) {
			_player.jump();
		}
		else {
			int arrow = e.getKeyCode()-37;
			if (arrow >= 0 && arrow < _arrowKeyStates.length) {
				_arrowKeyStates[arrow] = true;
			}
		}
	}
	/* Releasing arrow key sets state boolean back to false.*/
	public void onKeyReleased(KeyEvent e) {
		int arrow = e.getKeyCode()-37;
		if (arrow >= 0 && arrow < _arrowKeyStates.length) {
			_arrowKeyStates[arrow] = false;
		}
		//for drawing the lazor to visualize raycasting
		if (e.getKeyCode() == 32) _lazorBool = false;                
	}

	public void onMousePressed(MouseEvent e) {
		//convert loc to Game Units, switch y if math coordinates
	}
	public void onMouseDragged(MouseEvent e) {
	}
	public void onMouseMoved(MouseEvent e) {
	}

	public Player getPlayer() {
		return _player;
	}
}