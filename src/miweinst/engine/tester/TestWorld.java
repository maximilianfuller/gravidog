package miweinst.engine.tester;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.HashMap;

import miweinst.engine.App;
import miweinst.engine.FileIO;
import miweinst.engine.Tuple;
import miweinst.engine.contraints.SpringEntity;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.graph.HashDecorator;
import miweinst.engine.screen.Viewport;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import miweinst.m.Grenade;
import miweinst.m.LoseScreen;
import miweinst.m.MTestObject;
import miweinst.m.Player;
import miweinst.m.WinScreen;
import cs195n.Vec2f;

public class TestWorld extends GameWorld {        
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
        private boolean _jumping;
        //Lazor visualization for raycasting
        private Path2D.Float _lazor;
        private Vec2f _currMouseLoc;
        private boolean _lazorBool;
        private Vec2f _deltaPlayerPos;
        
////////
        private TestCollisionVisualizer _testVisualizer;
        
        //Class.string mapped to instance of Class<?>
        private HashDecorator<String, Class<? extends PhysicsEntity>> _classes;
        //Variable name mapped to PhysicsEntity instance
        private HashDecorator<String, PhysicsEntity> _entities;

        public TestWorld(App app, Viewport viewport) {
                super(app, viewport);
/////
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
                //Avoids keyRepeat on jumping, i.e. so impulse not applied multiple times for one MTV
                _jumping = false;
                _currMouseLoc = null;
                _lazorBool = false;
   
                //Get initial distance of Player from screen origin (game units) to maintain panning onTick
                _deltaPlayerPos = new Vec2f(_player.getX() - super._viewport.getScreenInGameLoc().x, _player.getY() - super._viewport.getScreenInGameLoc().y);
        
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
                                if (_player.isStable()) {
                                        if (_jumping == false) {
                                                _player.applyImpulse(new Vec2f(0, 45), _player.getCentroid());
                                                _jumping = true;
                                        }
                                }
                        }
                        //Right key down
                        if (_arrowKeyStates[2]) {
                                _player.goalVelocityX(100);        
                        }
                        //Down key down
                        if (_arrowKeyStates[3]) 
                                _player.goalVelocityY(-140);                
                        //Reset Jumping variable if stable on ground
                        if (_player.isStable()==false) 
                                if (_player.getLastMTV() != null) 
                                        if (Math.abs(_player.getLastMTV().x) < Math.abs(_player.getLastMTV().y)) 
                                                _jumping = false;        
                        
                        /*//Move camera to keep _player on screen; delta b/w locations before/after move
                        if (_deltaPlayerPos != null) {
                                float x = _player.getLocation().x - super._viewport.getScreenInGameLoc().x;
                                float y = _player.getLocation().y - super._viewport.getScreenInGameLoc().y;
                                if (super._viewport.isMathCoordinateSystem())
                                        y = super._viewport.getScreenSize().y/super.getScale() - _player.getLocation().y - super._viewport.getScreenInGameLoc().y;
                                super._viewport.pan(x - _deltaPlayerPos.x, y - _deltaPlayerPos.y);
                        }*/
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
                Vec2f toUnits = super.toUnits(new Vec2f(e.getX(), e.getY()));
                if (super._viewport.isMathCoordinateSystem())
                        toUnits = new Vec2f(toUnits.x, super.getViewportDimensions().y/super.getScale() - toUnits.y);
                //Left click
              /*  if (SwingUtilities.isLeftMouseButton(e)) {
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
                }*/
                
/////        
                _testVisualizer.onMousePressed(toUnits);
        }
        public void onMouseDragged(MouseEvent e) {
                Vec2f toUnits = super.toUnits(new Vec2f(e.getX(), e.getY()));
                toUnits = new Vec2f(toUnits.x, super.getViewportDimensions().y/super.getScale()-toUnits.y);
/////
                _testVisualizer.onMouseDragged(toUnits); 
        }
        public void onMouseMoved(MouseEvent e) {
                Vec2f toUnits = super.toUnits(new Vec2f(e.getX(), e.getY()));
                if (super._viewport.isMathCoordinateSystem())
                        toUnits = new Vec2f(toUnits.x, super.getViewportDimensions().y/super.getScale()-toUnits.y);
                _currMouseLoc = toUnits;
        }
}