package miweinst.m;

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

import javax.swing.SwingUtilities;

import miweinst.engine.App;
import miweinst.engine.FileIO;
import miweinst.engine.Tuple;
import miweinst.engine.beziercurve.BezierCurveEntity;
import miweinst.engine.contraints.PinEntity;
import miweinst.engine.contraints.SpringEntity;
import miweinst.engine.entityIO.Connection;
import miweinst.engine.entityIO.Input;
import miweinst.engine.entityIO.Output;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.PolygonShape;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.graph.HashDecorator;
import miweinst.engine.screen.Viewport;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import miweinst.engine.world.RelayEntity;
import miweinst.engine.world.WhileSensorEntity;
import miweinst.gravidog.ClosedCurveBoundary;
import cs195n.CS195NLevelReader;
import cs195n.CS195NLevelReader.InvalidLevelException;
import cs195n.LevelData;
import cs195n.LevelData.ConnectionData;
import cs195n.LevelData.EntityData;
import cs195n.LevelData.ShapeData;
import cs195n.LevelData.ShapeData.Type;
import cs195n.Vec2f;

public class MWorld extends GameWorld {        
<<<<<<< HEAD
	public final String string = "MWorld";
	//        private ArrayList<PhysicsEntity> _shapes;
	//        private StaticBoundary[] _boundaries;
	//Math-coordinate AffineTransform
	//        private AffineTransform _tx;
	//        private WhileSensorEntity _playerSensor;

	private App _app;
	private Player _player;

	//Player movement using boolean state array
	private boolean[] _arrowKeyStates;
	//        private boolean _jumping;
	//Lazor visualization for raycasting
	private Path2D.Float _lazor;
	private Vec2f _currMouseLoc;
	private boolean _lazorBool;
	private Vec2f _deltaPlayerPos;
	//        private boolean _jumping;
	////////
	//        private TestCollisionVisualizer _testVisualizer;

	//Class.string mapped to instance of Class<?>
	private HashDecorator<String, Class<? extends PhysicsEntity>> _classes;
	//Variable name mapped to PhysicsEntity instance
	private HashDecorator<String, PhysicsEntity> _entities;

	public MWorld(App app, Viewport viewport) {
		super(app, viewport);
		/////
		//                _testVisualizer = new TestCollisionVisualizer(this);

		_app = app;
		//Initialize Player to avoid NullPointer, in case not instantiated in level editor
		_player = new Player(this);
=======
        public final String string = "MWorld";
        
        private App _app;
        private Player _player;
        //Player movement using boolean state array
        private boolean[] _arrowKeyStates;
        //Lazor visualization for raycasting
        private Path2D.Float _lazor;
        private Vec2f _currMouseLoc;
        private boolean _lazorBool;
        private Vec2f _deltaPlayerPos;
        
        //Class.string mapped to instance of Class<?>
        private HashDecorator<String, Class<? extends PhysicsEntity>> _classes;
        //Variable name mapped to PhysicsEntity instance
        private HashDecorator<String, PhysicsEntity> _entities;

        public MWorld(App app, Viewport viewport) {
                super(app, viewport);
                _app = app;
                //Initialize Player to avoid NullPointer, in case not instantiated in level editor
                _player = new Player(this);             
                //Key code order: Left(37), Up(38), Right(39), Down(40)
                _arrowKeyStates = new boolean[4];
                for (int i=0; i<_arrowKeyStates.length; i++) _arrowKeyStates[i]=false;
                _currMouseLoc = null;
                _lazorBool = false;

////////////// START LEVEL READER /////////////////////
                
                //Map of Strings to Class<?>, for interpreting level data
                _classes = new HashDecorator<String, Class<? extends PhysicsEntity>>();                
                _classes.setDecoration("PhysicsEntity", PhysicsEntity.class);
                _classes.setDecoration("StaticBoundary", StaticBoundary.class);
                _classes.setDecoration("Player", Player.class);
                _classes.setDecoration("Grenade", Grenade.class);
                _classes.setDecoration("WhileSensorEntity", WhileSensorEntity.class);
                _classes.setDecoration("RelayEntity", RelayEntity.class);
                _classes.setDecoration("BezierCurveEntity", BezierCurveEntity.class);
                _classes.setDecoration("ClosedCurveBoundary", ClosedCurveBoundary.class);
>>>>>>> e2b9375cc93e0c48ef1e873b0fc2c4593057308c

		//Key code order: Left(37), Up(38), Right(39), Down(40)
		_arrowKeyStates = new boolean[4];
		for (int i=0; i<_arrowKeyStates.length; i++) _arrowKeyStates[i]=false;
		_currMouseLoc = null;
		_lazorBool = false;

<<<<<<< HEAD
		////////////// START LEVEL READER /////////////////////
=======
                File f = new File("src/miweinst/resources/m_level.nlf");
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
>>>>>>> e2b9375cc93e0c48ef1e873b0fc2c4593057308c

		//Map of Strings to Class<?>, for interpreting level data
		_classes = new HashDecorator<String, Class<? extends PhysicsEntity>>();                
		_classes.setDecoration("PhysicsEntity", PhysicsEntity.class);
		_classes.setDecoration("StaticBoundary", StaticBoundary.class);
		_classes.setDecoration("Player", Player.class);
		_classes.setDecoration("Grenade", Grenade.class);
		_classes.setDecoration("WhileSensorEntity", WhileSensorEntity.class);
		_classes.setDecoration("RelayEntity", RelayEntity.class);
		_classes.setDecoration("BezierCurveEntity", BezierCurveEntity.class);

		///Decoration set to each Entity read from LevelEditor!
		_entities = new HashDecorator<String, PhysicsEntity>();                                        

		File f = new File("src/miweinst/resources/level_one.nlf");
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

<<<<<<< HEAD
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
					entity = _player;
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
							shape = new CircleShape(s.getMin(), rad);
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
=======
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
                }
                else
                	System.err.println("Level is null! MWorld()");
/////////////////^^^^^^^^^^                
                        
/*                Shape pinEntityShape = new AARectShape(new Vec2f(50f, 60f), new Vec2f(15f, 4f)).rectToPoly();
                PinEntity pin = new PinEntity(this, new Vec2f(50f, 60f), pinEntityShape);
                pin.setMass(1f);
                this.addEntity(pin);
                
                Shape springEntityShape = new AARectShape(new Vec2f(134f,80f), new Vec2f(10f, 10f)).rectToPoly();
                SpringEntity spring = new SpringEntity(this, springEntityShape);
                spring.setMass(1f);
                spring.setSpringConstant(100f);
                spring.setFrictionConstant(1f);
                this.addEntity(spring);*/
                
                //Get initial distance of Player from screen origin (game units) to maintain panning onTick
                _deltaPlayerPos = new Vec2f(_player.getX() - super.viewport.getScreenInGameLoc().x, _player.getY() - super.viewport.getScreenInGameLoc().y);
        
                //Restore save_data
                _player.doRead.run(FileIO.read());
        }
        
        
        /*Called on win or lose conditions by Player's corresponding Inputs.*/
        public void playerWin() {
                _app.setScreen(new WinScreen(_app));
        }
        public void playerLose() {
                _app.setScreen(new LoseScreen(_app));
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
        		//Left key down
        		if (_arrowKeyStates[0]) {
        			_player.goalVelocityX(-100);
        		}
        		//Up key down
        		if (_arrowKeyStates[1]) {
        			_player.jump();
        		}
        		//Right key down
        		if (_arrowKeyStates[2]) {
        			_player.goalVelocityX(100);        
        		}
        		//Down key down
        		if (_arrowKeyStates[3]) 
        			_player.goalVelocityY(-140);           
>>>>>>> e2b9375cc93e0c48ef1e873b0fc2c4593057308c

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
		}
		else
			System.err.println("Level is null! MWorld()");
		/////////////////^^^^^^^^^^                

		Shape pinEntityShape = new AARectShape(new Vec2f(50f, 60f), new Vec2f(15f, 4f)).rectToPoly();
		PinEntity pin = new PinEntity(this, new Vec2f(50f, 60f), pinEntityShape);
		pin.setMass(1f);
		this.addEntity(pin);

		Shape springEntityShape = new AARectShape(new Vec2f(134f,80f), new Vec2f(10f, 10f)).rectToPoly();
		SpringEntity spring = new SpringEntity(this, springEntityShape);
		spring.setMass(1f);
		spring.setSpringConstant(100f);
		spring.setFrictionConstant(1f);
		this.addEntity(spring);

		//Restore save_data
		_player.doRead.run(FileIO.read());
	}


	/*Called on win or lose conditions by Player's corresponding Inputs.*/
	public void playerWin() {
		_app.setScreen(new WinScreen(_app));
	}
	public void playerLose() {
		_app.setScreen(new LoseScreen(_app));
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
			//Left key down
			if (_arrowKeyStates[0]) {
				_player.goalVelocityX(-100);
			}
			//Up key down
			if (_arrowKeyStates[1]) {
				_player.jump();
			}
			//Right key down
			if (_arrowKeyStates[2]) {
				_player.goalVelocityX(100);        
			}
			//Down key down
			if (_arrowKeyStates[3]) 
				_player.goalVelocityY(-140);           
		}
		/////
		//                _testVisualizer.onTick(nanos);
	}

	/* Called when user adds a PhysicsEntity to the screen,        
	 * to collide/interact with player. Called from user input methods.*/
	public PhysicsEntity addShape(Vec2f spawnLoc, int type) {
		PhysicsEntity toAdd = new MTestObject(this, spawnLoc, type);        
		this.addEntity(toAdd);
		return toAdd;
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
		/////////
		//                _testVisualizer.draw(g);
	}


	//USER INPUT
	/*Arrow key sets state boolean which opens
	 * calling of goalVelocity in onTick.*/
	public void onKeyPressed(KeyEvent e) {
		int arrow = e.getKeyCode()-37;
		if (arrow >= 0 && arrow < _arrowKeyStates.length) {
			_arrowKeyStates[arrow] = true;
		}
		//Space Bar: Shoot lazor!
		if (e.getKeyCode() == 32) {
			if (_currMouseLoc != null) {
				Vec2f src = _player.getCenter();
				Vec2f dst = _currMouseLoc;                        
				Tuple<PhysicsEntity, Vec2f> firstHit = this.castRay(src, dst, _player);
				//Apply impulse in direction of cast on first object hit by ray
				if (firstHit != null) 
					firstHit.x.applyImpulse(firstHit.y.minus(src), firstHit.y);

				// Make Lazor visuals, not real Entity just temporary path                                
				_lazor = new Path2D.Float();
				_lazor.moveTo(src.x, src.y);
				_lazor.lineTo(dst.x, dst.y);
				_lazorBool = true;        
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
		//ctrl key
		if (e.getKeyCode() == 17) {
			if (_currMouseLoc != null) {
				if (_player.hasActiveGrenade() == false) {
					Grenade nade = _player.tossGrenade(_currMouseLoc);
					//Add to front, because need to call Grenade's overriden collides()
					this.addEntityToFront(nade);
				}
			}
		}
	}

	public void onMousePressed(MouseEvent e) {
		//convert loc to Game Units, switch y if math coordinates
		Vec2f toUnits = super.viewport.screenPointToGame(new Vec2f(e.getX(), e.getY()));
		//Left click
		if (SwingUtilities.isLeftMouseButton(e)) {
			//+Shift
			//Lowest mass Polygon
			if (e.isShiftDown()) {
				this.addShape(toUnits, 0);
			}
			//
			//Lower mass Square
			else this.addShape(toUnits, 1);        
		}
		//Right click        
		if (SwingUtilities.isRightMouseButton(e)) {
			//+Control
			//Greater mass Circle
			if (e.isShiftDown()) 
				this.addShape(toUnits, 2);
			else {
				//Remove shape clicked on unless player or static
				PhysicsEntity[] ents = this.getEntitiesToArr();
				//Loop backwards to avoid ConcurrentModificationException
				for (int i=ents.length-1; i>=0; i--) {
					PhysicsEntity ent = ents[i];
					if (ent != _player && ent.isStatic() == false) {
						if (ent.contains(toUnits)) {
							this.removeEntity(ent);
						}
					}
				}
			}
		}

		/////        
		//                _testVisualizer.onMousePressed(toUnits);
	}
	public void onMouseDragged(MouseEvent e) {
		Vec2f toUnits = super.viewport.screenPointToGame(new Vec2f(e.getX(), e.getY()));
		/////
		//                _testVisualizer.onMouseDragged(toUnits); 
	}
	public void onMouseMoved(MouseEvent e) {
		Vec2f toUnits = super.viewport.screenPointToGame(new Vec2f(e.getX(), e.getY()));
		_currMouseLoc = toUnits;
	}
	
	public Player getPlayer() {
		return _player;
	}
}