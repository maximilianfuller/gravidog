package miweinst.engine.beziercurve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import miweinst.engine.collisiondetection.SeparatingAxis;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.CompoundShape;
import miweinst.engine.gfx.shape.PolygonShape;
import miweinst.engine.gfx.shape.Shape;
import cs195n.Vec2f;

public class BezierPath extends Shape {
	
	//All endpoints/ctrl points in path
	private ArrayList<Vec2f> _pts;	
	private ArrayList<LineSegment> _segs;
	
	private ArrayList<CubicBezierCurve> _curves;
	private CubicBezierCurve _lastCurve;
/////	
	private ArrayList<CircleShape> _drawDots;

	public BezierPath() {
		super(new Vec2f(0, 0), new Vec2f(0, 0));
		_pts = new ArrayList<Vec2f>();
		_segs = getDrawingSegments();
		_curves = new ArrayList<CubicBezierCurve>();
		_lastCurve = null;
		_drawDots = new ArrayList<CircleShape>();		
	}
	public BezierPath(ArrayList<Vec2f> points) {
		super(new Vec2f(0, 0), new Vec2f(0, 0));
		_pts = points;
		_segs = getDrawingSegments();
		_curves = new ArrayList<CubicBezierCurve>();
		
		_drawDots = new ArrayList<CircleShape>();
		for (int i=0; i<points.size(); i++) {
			CircleShape circle = new CircleShape(points.get(i), .5f);
			circle.setColor(Color.BLACK);
			_drawDots.add(circle);
		}
		updateSegs();
	}
	
	public void addPoint(Vec2f pt) {
		_pts.add(pt);
		updateSegs();
///////		
		CircleShape circle = new CircleShape(pt, 1f);
		circle.setColor(Color.BLACK);
		_drawDots.add(circle);
	}
	public void updateSegs() {
		_segs = getDrawingSegments();
	}
	
	public ArrayList<LineSegment> getDrawingSegments() {
		float resolution = 100;
		ArrayList<Vec2f> drawingPoints = new ArrayList<Vec2f>();
		
		_curves = new ArrayList<CubicBezierCurve>();
		for (int i=0; i<_pts.size() -3; i+= 3) {
			//Four points for curr curve in path
			Vec2f p0 = _pts.get(i);
			Vec2f p1 = _pts.get(i+1);
			Vec2f p2 = _pts.get(i+2);
			Vec2f p3 = _pts.get(i+3);
		
			if (i==0) {
				drawingPoints.add(CubicBezierCurve.calculateBezierPoint(0, p0, p1, p2, p3));
			}
			for (int j=1; j<resolution; j++) {
				float t = j/resolution;
				drawingPoints.add(CubicBezierCurve.calculateBezierPoint(t, p0, p1, p2, p3));
			}
////		
			CubicBezierCurve curve = new CubicBezierCurve(p0, p1, p2, p3);
			_curves.add(curve);
		}		
		return LineSegment.pointsToSegs(drawingPoints);
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(.6f));
		for (LineSegment seg: _segs) {
			seg.draw(g);
		}
/*		for (CubicBezierCurve curve: _curves) {
			curve.draw(g);
		}*/
/////
		for (CircleShape dot: _drawDots) {
			dot.draw(g);
		}		
	}
	@Override
	public boolean collides(Shape s) {	
		//Optimizes by checking curve of last collision first.
		if (_lastCurve != null) {
			if (s.collides(_lastCurve)) {
				this.setCollisionInfo(_lastCurve.getCollisionInfo());
				return true;
			}
		}
		//If any curve collides, collision detected
		for (CubicBezierCurve curve: _curves) {
			boolean collision = s.collides(curve);
			if (collision) {
				_lastCurve = curve;
				this.setCollisionInfo(curve.getCollisionInfo());
				return true;
			}
		}
		return false;
	}	
	@Override
	public boolean collidesCircle(CircleShape c) {
		for (CubicBezierCurve curve: _curves) {
			if (curve.collidesCircle(c)) {
				this.setCollisionInfo(curve.getCollisionInfo());
				return true;
			}
		}	
		return false;
	}
	@Override
	public boolean collidesAAB(AARectShape aab) {
		for (CubicBezierCurve curve: _curves) {
			if (curve.collidesAAB(aab)) {
				this.setCollisionInfo(curve.getCollisionInfo());
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean collidesPolygon(PolygonShape p) {
		for (CubicBezierCurve curve: _curves) {
			if (curve.collidesPolygon(p)) {
				this.setCollisionInfo(curve.getCollisionInfo());
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Vec2f poi(Shape s) {
		for (CubicBezierCurve curve: _curves) {
			if (curve.poi(s) != null)
				return curve.poi(s);
		}
		return null;
	}
	@Override
	public Vec2f poiPolygon(PolygonShape p) {
		for (CubicBezierCurve curve: _curves) {
			if (curve.poiPolygon(p) != null)
				return curve.poiPolygon(p);
		}
		return null;
	}
	@Override
	public Vec2f poiCircle(CircleShape c) {
		for (CubicBezierCurve curve: _curves) {
			if (curve.poiCircle(c) != null)
				return curve.poi(c);
		}
		return null;
	}


	@Override
	public Vec2f getCentroid() {
		ArrayList<Vec2f> centroids = new ArrayList<Vec2f>();
		for (CubicBezierCurve curve: _curves) {
			centroids.add(curve.getCentroid());
		}
		return Vec2f.average(centroids);
	}
	
	
	@Override
	public boolean collidesCompound(CompoundShape c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean collidesCurve(BezierCurve c) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public float getMomentOfInertia(float mass) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public float getArea() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Vec2f poiCurve(BezierCurve c) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean contains(Vec2f pnt) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Vec2f projectOnto(SeparatingAxis sep) {
		// TODO Auto-generated method stub
		return null;
	}
}
