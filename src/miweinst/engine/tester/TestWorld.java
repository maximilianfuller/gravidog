package miweinst.engine.tester;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import miweinst.engine.App;
import miweinst.engine.FileIO;
import miweinst.engine.contraints.SpringEntity;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.screen.Viewport;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import miweinst.m.LoseScreen;
import miweinst.m.MTestObject;
import miweinst.m.Player;
import miweinst.m.WinScreen;
import cs195n.Vec2f;

public class TestWorld extends GameWorld {        
	private App _app;
	private Player _player;

	//Player movement using boolean state array
	private boolean[] _arrowKeyStates;

	private TestCollisionVisualizer _testVisualizer;


	public TestWorld(App app, Viewport viewport) {
		super(app, viewport);

		_testVisualizer = new TestCollisionVisualizer(this);

		_app = app;
		//Initialize Player to avoid NullPointer, in case not instantiated in level editor
		_player = new Player(this);

		Shape springEntityShape = new AARectShape(new Vec2f(134f,80f), new Vec2f(10f, 10f)).rectToPoly();
		SpringEntity spring = new SpringEntity(this, springEntityShape);
		spring.setMass(1f);
		spring.setSpringConstant(100f);
		spring.setFrictionConstant(1f);
		this.addEntity(spring);

		//Key code order: Left(37), Up(38), Right(39), Down(40)
		_arrowKeyStates = new boolean[4];
		for (int i=0; i<_arrowKeyStates.length; i++) _arrowKeyStates[i]=false;

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
		long nanos = nanosSincePreviousTick/super.getIterations();
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
		_testVisualizer.onTick(nanos);
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
		_testVisualizer.draw(g);
	}

	//USER INPUT
	/*Arrow key sets state boolean which opens
	 * calling of goalVelocity in onTick.*/
	public void onKeyPressed(KeyEvent e) {
	}
	/* Releasing arrow key sets state boolean back to false.*/
	public void onKeyReleased(KeyEvent e) {
	}

	public void onMousePressed(MouseEvent e) {
		//convert loc to Game Units, switch y if math coordinates
		Vec2f toUnits = viewport.screenPointToGame(new Vec2f(e.getX(), e.getY()));          
		_testVisualizer.onMousePressed(toUnits);
	}
	public void onMouseDragged(MouseEvent e) {
		Vec2f toUnits = viewport.screenPointToGame(new Vec2f(e.getX(), e.getY()));
		_testVisualizer.onMouseDragged(toUnits); 
	}
	public void onMouseMoved(MouseEvent e) {
		Vec2f toUnits = viewport.screenPointToGame(new Vec2f(e.getX(), e.getY()));
		toUnits = new Vec2f(toUnits.x, super.getViewportDimensions().y/super.getScale()-toUnits.y);
	}
}