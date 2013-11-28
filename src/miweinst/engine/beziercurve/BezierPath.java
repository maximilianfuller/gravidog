package miweinst.engine.beziercurve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import miweinst.engine.collisiondetection.CollisionInfo;
import miweinst.engine.collisiondetection.SeparatingAxis;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.CompoundShape;
import miweinst.engine.gfx.shape.PolygonShape;
import miweinst.engine.gfx.shape.Shape;
import support.pj2.lib.edu.rit.util.Tridiagonal;
import cs195n.Vec2f;

public class BezierPath extends Shape {
	
	//All endpoints/ctrl points in path
	private ArrayList<Vec2f> _pts;	
	private ArrayList<LineSegment> _segs;	
	private ArrayList<CubicBezierCurve> _curves;
//// Visualization
	private static ArrayList<CircleShape> _drawDots;

	public BezierPath() {
		super(new Vec2f(0, 0), new Vec2f(0, 0));
		_pts = new ArrayList<Vec2f>();
		_segs = getDrawingSegments();
		_curves = new ArrayList<CubicBezierCurve>();
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
	
	public ArrayList<CubicBezierCurve> getCurves() {
		return _curves;
	}
	
	public void addPoint(Vec2f pt) {
		_pts.add(pt);
		updateSegs();
/////// VISUALIZER	
/*		CircleShape circle = new CircleShape(pt, 1f);
		circle.setColor(Color.BLACK);
		_drawDots.add(circle);*/
	}
	public void updateSegs() {
		_segs = getDrawingSegments();
	}
	
	/* Creates a curve between each knot, so returns 
	 * BezierPath with n curves, and ~n*3 points. 
	 * Populates a_ctrls and b_ctrls arrays. */
	public static BezierPath generateClosedCurve(Vec2f[] knots, 
				ArrayList<Vec2f> a_ctrls, ArrayList<Vec2f> b_ctrls) 
	{
		int n = knots.length;
		if (n <= 2) 
			return null;
		//Calculate first control points for Bi (curve i)
		//1: P1 + 4P0 + Pn-1
		//Set up Matrix
		double[] f = new double[n], 
				d = new double[n], 
				e = new double[n];
		for (int i=0; i<n; i++) {
			f[i] = 1;
			d[i] = 4;
			e[i] = 1;
		}
		//X coordinates 
		double[] rhs = new double[n];
		for (int i=0; i<n; i++) {
			//avoid out of bounds errors
			int j = (i==n-1)? 0: i+1;
			//right hand side: 4*p0 + 2*p1
			rhs[i] = 4*knots[i].x + 2*knots[j].x;
		}
		//Solve Ax=B where A is cyclic matrix (f,d,e), x is solution vector, 
		 	//b is right hand side (rhs) 
		double[] x = new double[n];
		Tridiagonal.solveCyclic(d, e, f, rhs, x);
		
		//Y coordinates
		for (int i=0; i<n; i++) {
			int j = (i==n-1)? 0: i+1;
			rhs[i] = 4*knots[i].y + 2*knots[j].y;
		}
		double[] y = new double[n];
		Tridiagonal.solveCyclic(d, e, f, rhs, y);
		
		for (int i=0; i<n; i++) {
			//First controls
			a_ctrls.add(i, new Vec2f((float)x[i], (float)y[i]));
			//Second controls. Calculated with equation from first derivative continuity:
				//P1i + P2i = 2Pi for (i=0, ..., n-1) 
			b_ctrls.add(i, new Vec2f((float)(2*knots[i].x-x[i]), (float)(2*knots[i].y - y[i])));
		}		
//////TESTING USING EQUIVALENT LIBRARY METHOD TO SEE IF BUGS GO AWAY
/*
		double[] knots_x = new double[knots.length];
		double[] knots_y = new double[knots.length];
		for (int i=0; i<knots.length; i++) {
			Vec2f k = knots[i];
			knots_x[i] = k.x;
			knots_y[i] = k.y;
		}
		double[] a_ctrls_x = new double[n];
		double[] a_ctrls_y = new double[n];
		double[] b_ctrls_x = new double[n];
		double[] b_ctrls_y = new double[n];		
		CurveSmoothing.computeBezierClosed(knots_x, a_ctrls_x, b_ctrls_x, 0, n);
		CurveSmoothing.computeBezierClosed(knots_y, a_ctrls_y, b_ctrls_y, 0, n);
		for (int i=0; i<knots.length; i++) {
			a_ctrls.add(new Vec2f((float)a_ctrls_x[i], (float)a_ctrls_y[i]));
			b_ctrls.add(new Vec2f((float)b_ctrls_x[i], (float)b_ctrls_y[i]));
		}
*/
//////^^^^^^^		
		ArrayList<Vec2f> orderedPoints = new ArrayList<Vec2f>();
		for (int i=0; i < n; i++) {
			if (i < n-1) {
				//0 - n-2 curves
				orderedPoints.add(knots[i]);		//Curve start
				orderedPoints.add(a_ctrls.get(i));	//First control for curve
				orderedPoints.add(b_ctrls.get(i+1));	//Second control for curve
			}
			else {
				orderedPoints.add(knots[i]);
				orderedPoints.add(a_ctrls.get(i));
				orderedPoints.add(b_ctrls.get(0));
				//closing n-1 curve
				orderedPoints.add(knots[0]);
			}
		}
		BezierPath path = new BezierPath(orderedPoints);
////// DRAW KNOTS FOR VISUALIZATION
		_drawDots.clear();
		for (Vec2f knot: knots) {
			CircleShape circle = new CircleShape(knot, 2f);
			circle.setColor(Color.BLACK);
			_drawDots.add(circle);
		}
		return path;
	}
	
	/*Return LineSegments for drawing.*/
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
			CubicBezierCurve curve = new CubicBezierCurve(p0, p1, p2, p3);
			_curves.add(curve);
		}		
		return LineSegment.pointsToSegs(drawingPoints);
	}
	
	/* Returns the curve in path that is closest to Shape s.
	 * Compares distances to each curve in path. Optimized by
	 * only comparing curves that s is nearby (Convex hull check). 
	 * Returns null if no curves are nearby. Called in collision
	 * methods below, solved bug where shapes would fall out or
	 * slow down at knots between curves.*/
	public CubicBezierCurve findClosestCurve(Shape s) {
		float minDist = Float.POSITIVE_INFINITY;
		CubicBezierCurve curve = null;
		for (CubicBezierCurve seg: _curves) {
			//Optimize by checking convex hull collision first
			if (seg.getWideBounds().collides(s)) {
				Vec2f point = seg.nearestPointOnCurve(s.getCentroid());
				float dist = point.dist2(s.getCentroid());
				if (dist < minDist) {
					minDist = dist;
					curve = seg;
				}
			}
		}
		return curve;
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
		//If any curve collides, collision detected
		for (CubicBezierCurve curve: _curves) {
			boolean collision = s.collides(curve);
			if (collision) {
				this.setCollisionInfo(curve.getCollisionInfo());
				return true;
			}
		}
		return false;
	}	
	/* Optimized by only colliding with the curve that
	 * is closest to c. Avoids bugs at knots between curves.*/
	@Override
	public boolean collidesCircle(CircleShape c) {
		//Only collides with curve that is closest to c
		CubicBezierCurve closestCurve = this.findClosestCurve(c);
		//Null check
		if (closestCurve == null)
			return false;
		if (closestCurve.collidesCircle(c)) {
			this.setCollisionInfo(closestCurve.getCollisionInfo());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean collidesAAB(AARectShape aab) {
		PolygonShape poly = aab.rectToPoly();
		if (collidesPolygon(poly)) {
			aab.setCollisionInfo(poly.getCollisionInfo());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean collidesPolygon(PolygonShape p) {
		CubicBezierCurve closestCurve = this.findClosestCurve(p);
		if (closestCurve == null)
			return false;
		if (closestCurve.collidesPolygon(p)) {
			this.setCollisionInfo(closestCurve.getCollisionInfo());
			return true;
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
