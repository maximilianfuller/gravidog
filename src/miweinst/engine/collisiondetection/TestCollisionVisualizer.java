package miweinst.engine.collisiondetection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import miweinst.engine.beziercurve.CubicBezierCurve;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.PolygonShape;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import cs195n.Vec2f;

/**
 * This class is instantiated in a subclass of GameWorld, 
 * used to test collision detection on test objects.
 * Works with shapes wrapped in MovingEntity, and
 * works all in game units, in order to test
 * the system in as close a context as it will
 * really be used in the game. Creates an inner
 * subclass of MovingEntity in order to use it. So
 * this draw method is called in GameWorld, which
 * means it is inside the Graphics AffineTransform
 * and inside the Viewport's clipRect.
 * 
 * @author miweinst
 */

public class TestCollisionVisualizer {
	
	private TestEntity _testCircle, _testRect, _testPolygonA, _testPolygonB, _testCurve;
	private TestEntity[] _testArr;
	
	private boolean _showPoi;
	private CircleShape _poi;
	
	private Vec2f _mouseLast;
	private int _currIndex;

	public TestCollisionVisualizer(GameWorld world) {
		
/////
		_poi = new CircleShape(new Vec2f(0, 0), 1);
		_poi.setColor(Color.BLACK);
		_showPoi = false;

		_testCircle = new TestEntity(world, "circle");
//		_testSquare = new TestEntity(world, "square");
		_testRect = new TestEntity(world, "rect");
//		_testCompound = new TestEntity(world, "compound");
		_testPolygonA = new TestEntity(world, "polygonA");
		_testPolygonB = new TestEntity(world, "polygonB");
		_testCurve = new TestEntity(world, "curve");
		
		_testArr = new TestEntity[5];
		_testArr[0] = _testCircle; 
//		_testArr[1] = _testSquare; 
		_testArr[1] = _testRect;
//		_testArr[2] = _testCompound;
		_testArr[2] = _testPolygonA;
		_testArr[3] = _testCurve;
		_testArr[4] = _testPolygonB;
	}
	
	//Returns all shapes
	public TestEntity[] toArr() {
		return _testArr;
	}
	
	public void onTick(long nanos) {
//Random colors on tick for Collision
		Random rand = new Random();
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();

		//Collision Check!
		for (int i=0; i<_testArr.length; i++) {
			for (int j=i; j<_testArr.length; j++) {
				if (_testArr[i] != _testArr[j]) {
					if (_testArr[i].collides(_testArr[j])) {
						_testArr[i].setShapeColor(new Color(r,g,b));
						_testArr[j].setShapeColor(new Color(r,g,b));
////////
						//POI tester
						Vec2f poi = _testArr[i].getShape().poi(_testArr[j].getShape());
						if (poi != null) {
							_showPoi = true;
							_poi = new CircleShape(poi, .85f);
						}
						else {
							_showPoi = false;
						}
					}
				}
			}
//////		GRAVITY
//			_testArr[i].onTick(nanos);
		}

//////	TESTER FOR ONLY CHECKING ONE SHAPE AGAINST THE OTHER
/*		if (_testArr[2].collides(_testArr[3])) {
			_testArr[2].setShapeColor(new Color(r,g,b));
			_testArr[3].setShapeColor(new Color(r,g,b));
		}*/
		//Gravity
/*		_testArr[0].onTick(nanos);
		_testArr[3].onTick(nanos);*/
////^^^	
				
	}
	
	/*Takes in location of where the mouse goes down, used
	 * to measure the change in mouse location when 
	 * MouseDragged finds next loc.*/
	public void onMousePressed(Vec2f e) {
		TestEntity[] arr = this.toArr();
		boolean inShape = false;
		for (int i=0; i<arr.length; i++) {
			if (arr[i].contains(e)) {
				inShape = true;
				_currIndex = i;
/////
//				System.out.println("Shape Color: " + arr[i].getShapeColor() + " (TestCollisionVisualizer.onMousePressed)");
			}
		}
		//If pressing outside of Shape, no selected shape
		if (inShape == false) _currIndex = arr.length+1;
		_mouseLast = e;
	}
	/*Takes in the new mouse location in game units, and the 
	 * current scale.*/
	public void onMouseDragged(Vec2f e) {
		TestEntity[] arr =this.toArr();
		float dx = e.x - _mouseLast.x;
		float dy = e.y -  _mouseLast.y;
		if (_currIndex < arr.length) 
			arr[_currIndex].move(dx, dy);	
		_mouseLast = e;
	}
	
	public void draw(Graphics2D g) {
		PhysicsEntity[] testarr = toArr();
		for (int i=0; i<testarr.length; i++)
			testarr[i].draw(g);
		if (_showPoi) {
			_poi.draw(g);
		}
	}

/**This is an inner class in order to use Visualizer with
 * Entities that wrap the shapes. It is a test Entity to
 * test shapes and collisions within. Simple conditionals
 * in order to add new shapes, etc...*/
private class TestEntity extends PhysicsEntity {

	private Shape test_shape;
	
	public TestEntity(GameWorld world, String shape) {
		super(world);
		
		if (shape == "circle") {
			Vec2f circloc = new Vec2f(10, 60);
			float radius = 2;
			CircleShape circle = new CircleShape(circloc, radius);
			circle.setColor(Color.PINK);
			circle.setBorderColor(Color.WHITE);
			circle.setBorderWidth(0);
			test_shape = circle;
		} else if (shape == "square") {
			Vec2f squareLoc = new Vec2f(13, 13);
			Vec2f squareDim = new Vec2f(3, 3);
			AARectShape square = new AARectShape(squareLoc, squareDim);
			square.setColor(Color.ORANGE);
			square.setBorderColor(Color.WHITE);
			square.setBorderWidth(0);
//			test_shape = square;
		} else if (shape == "rect") {
			Vec2f rectloc  = new Vec2f(42, 20);
			Vec2f rectdim = new Vec2f(7, 4);
			AARectShape rect = new AARectShape(rectloc, rectdim);
			rect.setColor(Color.GREEN);
			rect.setBorderColor(Color.WHITE);
			rect.setBorderWidth(0);
			test_shape = rect;	
//////
			this.setStatic(true);
		} else if (shape == "compound") {
			Shape[] shapes = new Shape[3];
			shapes[0] = new CircleShape(new Vec2f(3, 10), 4);
			shapes[1] = new CircleShape(new Vec2f(10, 10), 3);
			shapes[2] = new AARectShape(new Vec2f(14, 9), new Vec2f(5, 2));
//			CompoundShape compound = new CompoundShape(Color.CYAN, new Vec2f(10, 10), shapes);
//			test_shape = compound;
		} else if (shape == "polygonA") {
			Vec2f[] verts = new Vec2f[3];
			//Counter-Clockwise order
			verts[0] = new Vec2f(15, 40);
			verts[1] = new Vec2f(11, 35);
			verts[2] = new Vec2f(11, 45);
			PolygonShape poly = new PolygonShape(new Vec2f(11, 11), verts);
			poly.setLocation(new Vec2f(45, 60));
			poly.setOutline(Color.RED, .14f);
			test_shape = poly;
		} else if (shape == "polygonB") {
			Vec2f[] verts = new Vec2f[3];
			//Counter-Clockwise order
			verts[0] = new Vec2f(28, 14);
			verts[1] = new Vec2f(24, 14);
			verts[2] = new Vec2f(26, 18);			
			PolygonShape poly = new PolygonShape(new Vec2f(24, 4), verts);
			poly.setOutline(Color.MAGENTA, .14f);
			test_shape = poly;
		} else if (shape == "curve") {
			CubicBezierCurve curve = new CubicBezierCurve();
			curve.setBorderColor(Color.RED);
			curve.setBorderWidth(1);
//			curve.rotate(curve.start, 5);
			curve.translate(new Vec2f(-8, 0));
			test_shape = curve;
//////
			this.setStatic(true);
			this.setRestitution(5);
			this.setMass(1);
		}
/////
//		this.setInteractive(false);
		super.setShape(test_shape);
	}	
}
}
