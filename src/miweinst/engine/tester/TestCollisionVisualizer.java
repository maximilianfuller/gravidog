package miweinst.engine.tester;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import miweinst.engine.beziercurve.BezierPath;
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
	private TestEntity _closedBezier;
	private TestEntity[] _testArr;
	
	private boolean _showPoi;
	private ArrayList<CircleShape> _pois;
	
	private Vec2f _mouseLast;
	private int _currIndex;

	public TestCollisionVisualizer(GameWorld world) {
		
/////
		PhysicsEntity.GRAVITY = PhysicsEntity.GRAVITY.smult(10);
		_pois = new ArrayList<CircleShape>();
		_showPoi = false;

		_testCircle = new TestEntity(world, "circle");
//		_testSquare = new TestEntity(world, "square");
		_testRect = new TestEntity(world, "rect");
//		_testCompound = new TestEntity(world, "compound");
		_testPolygonA = new TestEntity(world, "polygonA");
		_testPolygonB = new TestEntity(world, "polygonB");
		_testCurve = new TestEntity(world, "curve");
		_closedBezier = new TestEntity(world, "closed bezier");
		
		_testArr = new TestEntity[6];
		_testArr[0] = _testCircle; 
//		_testArr[1] = _testSquare; 
		_testArr[1] = _testRect;
//		_testArr[2] = _testCompound;
		_testArr[2] = _testPolygonA;
		_testArr[3] = _testCurve;
		_testArr[4] = _testPolygonB;
		_testArr[5] = _closedBezier;
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
							_pois.add(new CircleShape(poi, .4f));
						}
						else {
							_showPoi = false;
						}
					}
				}
			}
//////		GRAVITY
			_testArr[i].onTick(nanos);
		}

//////	TESTER FOR ONLY CHECKING ONE SHAPE AGAINST THE OTHER
/*		if (_testArr[2].collides(_testArr[3])) {
			_testArr[2].setShapeColor(new Color(r,g,b));
			_testArr[3].setShapeColor(new Color(r,g,b));
		}*/
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
/////		If you want to find cool colors, prints randomized color
				System.out.println("Shape Color: " + arr[i].getShapeColor() + " (TestCollisionVisualizer.onMousePressed)");
			}
////Get location for debugging or reorganizing objects on screen
			if (arr[i].getShape() instanceof CircleShape) {
				System.out.println(arr[i].getLocation());
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
//		_showPoi = false;
		if (_showPoi) 
			for (CircleShape poi: _pois) 
				poi.draw(g);
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
//			Vec2f circloc = new Vec2f(10, 60);
			Vec2f circloc = new Vec2f(118, 53);
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
			Vec2f rectloc  = new Vec2f(42, 10);
			Vec2f rectdim = new Vec2f(7, 4);
			PolygonShape rect = new AARectShape(rectloc, rectdim).rectToPoly();
			rect.setColor(Color.GREEN);
			rect.setBorderColor(Color.WHITE);
			rect.setBorderWidth(0);
			test_shape = rect;	
//////
//			this.setStatic(true);
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
			CubicBezierCurve curve = new CubicBezierCurve
					(new Vec2f(35, 40), new Vec2f(45.5f, 63), 
							new Vec2f(40, 22), new Vec2f(80, 55.6f));
			curve.setBorderColor(Color.BLACK);
			curve.setBorderWidth(.5f);
//			curve.rotate(curve.start, 5);
			curve.translate(new Vec2f(-8, 0));
			test_shape = curve;

			this.setStatic(true);
			this.setRestitution(0);
			this.setMass(1);
		} else if (shape == "closed bezier") {
			Vec2f[] knots = new Vec2f[4];
			knots[0] = new Vec2f(100, 75);
			knots[1] = new Vec2f(150, 50);
			knots[2] = new Vec2f(100, 25);
			knots[3] = new Vec2f(50, 50);
			ArrayList<Vec2f> firstControls = new ArrayList<Vec2f>(knots.length);
			ArrayList<Vec2f> secondControls = new ArrayList<Vec2f>(knots.length);
			BezierPath closedCurve = BezierPath.generateClosedCurve(knots, firstControls, secondControls);
			
			test_shape = closedCurve;
			this.setStatic(true);
			this.setRestitution(0);
			this.setMass(1);;
		}

		super.setShape(test_shape);
	}
}
}

