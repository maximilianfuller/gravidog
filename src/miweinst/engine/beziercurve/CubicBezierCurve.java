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
import cs195n.Vec2f;

public class CubicBezierCurve extends BezierCurve {
	
	public Vec2f start, ctrl_one, ctrl_two, end;
	private Vec2f[] _points;
	private ArrayList<LineSegment> _segs;
	
	/*Default constructor and constructor taking in four _points (2 endpoints, 2 ctrl _points)*/
	public CubicBezierCurve() {
		super(new Vec2f(0, 0), new Vec2f(0, 0));	
		_points = new Vec2f[4];
		//List to be populated by findDrawingPoints
		_segs = new ArrayList<LineSegment>();
		this.init(new Vec2f(40, 50), new Vec2f(45.5f, 57), new Vec2f(50, 42), new Vec2f(60, 68.6f));
	}
	public CubicBezierCurve(Vec2f point1, Vec2f point2, Vec2f point3, Vec2f point4) {
		super(point1, new Vec2f(0, 0));
		_points = new Vec2f[4];
		//List to be populated by findDrawingPoints
		_segs = new ArrayList<LineSegment>();
		this.init(point1, point2, point3, point4);		
	}
	
	/*Initializes variables and arr to four _points: point1 and point4 are start
	 * and end point, point2 and point3 are ctrl _points. Sets shape location to start.*/
	private void init(Vec2f point1, Vec2f point2, Vec2f point3, Vec2f point4) {
		start = _points[0] = point1;
		ctrl_one = _points[1] = point2;
		ctrl_two = _points[2] = point3;
		end = _points[3] = point4;
		this.setLocation(start);
		this.updateSegs();
	}
	
	/*Location of curve is defined as the starting endpoint. 
	 * When new location is set, points do not move relative to
	 * each other; all points are translated by same amount so
	 * that start point is at the new location.*/
	@Override
	public Vec2f getLocation() {
		return start;
	}
	@Override
	public void setLocation(Vec2f loc) {
		super.setLocation(loc);
		Vec2f d = new Vec2f(loc.x - loc.x, loc.y - start.y);
		this.translate(d);
	}
	
	/*Translate all ctrl _points and endpoints in curve by specified dx, dy. 
	 * Makes changes in array of _points and then udpates the endpoints and
	 * control _points using updatePoints.*/
	public void translate(Vec2f d) {
		for (int i=0; i<_points.length; i++) {
			Vec2f p = _points[i];
			_points[i] = new Vec2f(p.x + d.x, p.y + d.y);
		}
		this.updatePointVars();
		this.updateSegs();
	}
	/* Rotate each point around the given point rotateAround by
	 * an angle theta. Uses LineSegment's static method to rotate
	 * point around another point.*/
	public void rotate(Vec2f rotateAround, float theta) {
		//First get rotated _points in _points arr
		for (int i=0; i<_points.length; i++)
			_points[i] = LineSegment.rotate(rotateAround, _points[i], theta);
		//Update vars to rotated _points
		this.updatePointVars();
		this.updateSegs();
	}

	/*Updates the array of _points, called if any values of
	 * start, end, ctrl_one or ctrl_two are changed. Should
	 * be called automatically from any methods within BezierCurve classes
	 * that change any of the point values, for encapsulation's sake.*/
	protected void updatePointArr() {
		_points[0] = start;
		_points[1] = ctrl_one;
		_points[2] = ctrl_two;
		_points[3] = end;
		updateSegs();
	}
	/*Other direction: Updates vars for each point if pointsArr has been changed.*/
	protected void updatePointVars() {
		start = _points[0];
		ctrl_one = _points[1];
		ctrl_two = _points[2];
		end = _points[3];
		updateSegs();
	}
	/*Updates LineSegment references in _segs*/
	private void updateSegs() {
		//Populate list of LineSegments _segs
		ArrayList<Vec2f> pointList = new ArrayList<Vec2f>();
		ArrayList<LineSegment> segList = new ArrayList<LineSegment>();
		for (float t=0; t <= 1; t += .01f) 
			pointList.add(getCasteljauPoint(t)); 
		for (int i=0; i < pointList.size()-1; i++) {			
			LineSegment seg = new LineSegment(pointList.get(i), pointList.get(i+1));
			segList.add(seg);
		}	
		_segs = segList;
	}
	
	/*Calculates a point at parameter t along this instance of CubicBezierCurve,
	 * using addition/assignment of each term of the following cubic equation: */
	//Cubic Bezier curve: [x,y] = (1-t)^3*P0 + 3(1-t)^2*tP1 + 3(1-t)ttP2 + tttP3
	public Vec2f calculateBezierPoint(float t) {
		float u = 1-t;
		float uu = u*u;
		float uuu = uu*u;
		float tt = t*t;
		float ttt = tt*t;		
		Vec2f p = start.smult(uuu);
		p = p.plus(ctrl_one.smult(3*uu*t));
		p = p.plus(ctrl_two.smult(3*u*tt));
		p = p.plus(end.smult(ttt));
		return  p;
	}	
	
	/* Implementation of de Casteljau's algorithm to return point
	 * on curve at specified t=u value. Returns point on the curve
	 * at t=u, more efficient than raw mathematical calculation used in
	 * calculateBezierPoint, and more extensible.
	 * Given a ratio u b/w 0 and 1, returns point C = (1-u)A + uB */
	public Vec2f getCasteljauPoint(float u) {
		Vec2f[] arr = new Vec2f[_points.length];
		//Save input in working arr
		for (int i=0; i<_points.length; i++) 
			arr[i] = _points[i];
		for (int i=1; i<arr.length; i++) {
			for (int j=0; j<arr.length-i; j++) {
				arr[j] = arr[j].smult(1-u).plus(arr[j+1].smult(u));
			}
		}
		return arr[0];
	}
	
	/* Find derivative of point on curve at specified t value. The 
	 * derivative of an n-order Bezier curve is an (n-1)-order Bezier curve. 
	 * The ctrl _points of derivative curve are: q0 = p1-p0, q1 = p2-p1, q2 = p3-p2, so on...
	 * Note: The derivative and tangent of C(t) are equivalent
	 * Calculated arithmetically using the following polynomial: */ 
	// dC(t)/dt = C'(t) = 
	// (-3*P0*(1-t)^2) + (P1*(3*(1-t)^2 - 6*(1-t)*t)) + (P2*(6*(1-t)*t - 3*tt)) + (3*P3*tt)
	public Vec2f findDerivative(float t) {
		float u = 1-t;
		float uu = u*u;
		float tt = t*t;
		//Each term added after calculated with Vec2f ops
		Vec2f dt = start.smult(-3*uu);
		dt = dt.plus(ctrl_one.smult(3*uu - 6*u*t));
		dt = dt.plus(ctrl_two.smult(6*u*t - 3*tt));
		dt = dt.plus(end.smult(3*tt));
		return dt.normalized();
	}
	/* Returns the vector perpendicular to the normalized tangent
	 * at the point f(t) for any t value. */
	public Vec2f findNormal(float t) {
		Vec2f dt = findDerivative(t);
		return new Vec2f(-dt.y, dt.x).normalized();
	}
	
	/*Find tangent at given point C(t)*/
	public Vec2f findTangentAt(Vec2f c_t) {
		return new Vec2f((float)Math.tan(c_t.x), (float)Math.tan(c_t.y)).normalized();
	}
	/*Find normal at given point C(t).*/
	public Vec2f findNormalAt(Vec2f c_t) {
		return new Vec2f(-findTangentAt(c_t).y, findTangentAt(c_t).x);
	}

	/*Find the point P (defined by P(t)) on the Bezier curve that is closest to 
	 * the point M, which can be anywhere. The line seg MP (i.e. M-P) is orthogonal
	 * to the tangent/derivative of P (dP/dt), so MP.dot(dP/dt) == 0. 
	 * Therefore M.minus(P).dot(derivative(getT(P))) == 0, then use root finding.*/
	public Vec2f nearestPointOnCurve(Vec2f m) {
		//Minimum distance between M and P, i.e. M.minus(P).dot(derivative(getT(P))) == 0
		float res = 1000;
		float t_val = 0;
		
///Iterating for now, but will solve cubic equation once my cubicRoots() method is fully functioning
		//Dot/Orthoganal check
		float minDot = Float.POSITIVE_INFINITY;
		for (float t=0; t<=1; t=t+1/res) {
			Vec2f p = getCasteljauPoint(t);
			 float dot = m.minus(p).dot(findDerivative(t));
			 
			 //minDot should be trivially close to 0
			 if (Math.abs(dot) < minDot) {
				minDot = Math.abs(dot);
				t_val = t;
			 }
		}
		return getCasteljauPoint(t_val);
	}
///////^^^

/*NOTE: Tune MIN_SQR_DISTANCE and threshold to vary depth of
 * recursion resolution/smoothness of curve drawing.
 *  */
	//Minimum length of segment, at which recursion on that segment stops
	public final float MIN_SQR_DISTANCE = .0001f;
	//Normalized dot (equivalent to angle) under 
	//which a new point does not need to be added at mid
	public final float THRESHOLD = -1f;
	
	/* Recursively finds _points along curve to draw; resolution/closeness of
	 * _points depends on THRESHOLD value.
	 * t0 and t1 are first passed 0 and 1, which are the
	 * t values that correspond to the endpoints. Int insertionIndex maintains
	 * the list order of the _points even while recursive calls are not made 
	 * in point-order starting at 0-index, and pointList is the List<Vec2f> 
	 * that is populated by the method.*/	
	public int findDrawingPoints(float t0, float t1, int insertionIndex, 												
						ArrayList<Vec2f> pointList) {		
		//Get midpoint parameter between t0 and tw
		float tMid = (t0+t1)/2;
		//Get endpoints of segment from t0 --> t1
		Vec2f p0 = calculateBezierPoint(t0);
		Vec2f p1 = calculateBezierPoint(t1);
		//Minimum distance check to avoid normalization short vectors
		if (p0.minus(p1).mag2() < MIN_SQR_DISTANCE) {
			return 0;
		}
		
		//Get point along curve at tMid
		Vec2f pMid = calculateBezierPoint(tMid);
		//Find unit vecs to both sides of pMid: [mid, start], [mid, end]
		Vec2f lDir = p0.minus(pMid).normalized();
		Vec2f rDir = p1.minus(pMid).normalized();
		
		//If the angle formed between segments is too large (i.e. dot too small)
		if (lDir.dot(rDir) > THRESHOLD) {
			//Num _points added to pointList
			int pointsAdded = 0; 
			
			//Recursive step on first segment
			pointsAdded += findDrawingPoints(t0, tMid, insertionIndex, pointList);
			//Add new point at correct index
			pointList.add(insertionIndex + pointsAdded, pMid);
			pointsAdded++;
			
			//Recursive step on second segment; added at index after prior _points 
			pointsAdded += findDrawingPoints(tMid, t1, insertionIndex + pointsAdded, pointList);
			
			return pointsAdded;
		}
		//If threshold not reached, no _points added to pointList
		return 0;
	}
	
	
	/* Draws a Cubic Bezier Curve (with two control _points) by first
	 * recursively getting a List of _points which should be drawn, 
	 * depending on resolution/smoothness set by THRESHOLD (findDrawingPoints),
	 * then draws a line segment between each of the drawing _points.*/
	@Override
	public void draw(Graphics2D g) {		
		g.setStroke(new BasicStroke(.5f));
		g.setColor(Color.RED);
		//Draw each LineSegment calculated by deCasteljau's algorithm in init
		for (LineSegment seg: _segs) 
			seg.draw(g);
	}
	
	
	/**public static methods*/
	/*Returns sign of specified float as 1 or -1*/
	public static int sign(float f) {
		return (int) (f/Math.abs(f));
	}
	/*Static calculation that takes in all four _points as arguments.*/
	public static Vec2f calculateBezierPoint(float t, Vec2f p0, Vec2f p1, Vec2f p2, Vec2f p3) {
		float u = 1-t;
		float uu = u*u;
		float uuu = uu*u;
		float tt = t*t;
		float ttt = tt*t;		
		Vec2f p = p0.smult(uuu);
		p = p.plus(p1.smult(3*uu*t));
		p = p.plus(p2.smult(3*u*tt));
		p = p.plus(p3.smult(ttt));
		return  p;
	}
/*	public static Vec2f[] bezierCoefficients(Vec2f p0, Vec2f p1, Vec2f p2, Vec2f p3) {	
		Vec2f[] coeffs = new Vec2f[4];
		coeffs[0] = p0;
		coeffs[1] = p1.smult(3);	//p1.smult(3*uu*t);
		coeffs[2] = p2.smult(3);	//p2.smult(3*u*tt)
		coeffs[3] = p3;				//p3.smult(ttt)
		return coeffs;
	}	*/
	public static float[] bezierCoeffsX(Vec2f p0, Vec2f p1, Vec2f p2, Vec2f p3) {
		float[] x_cof = new float[4];
		x_cof[0] = -p0.x + 3*p1.x - 3*p2.x + p3.x;
		x_cof[1] = 3*p0.x - 6*p1.x + 3*p2.x;
		x_cof[2] = -3*p0.x + p1.x;
		x_cof[3] = p0.x;
		return x_cof;
	}
	public static float[] bezierCoeffsY(Vec2f p0, Vec2f p1, Vec2f p2, Vec2f p3) {
		float[] y_cof = new float[4];
		y_cof[0] = -p0.y + 3*p1.y - 3*p2.y + p3.y;
		y_cof[1] = 3*p0.y - 6*p1.y + 3*p2.y;
		y_cof[2] = -3*p0.y + p1.y;
		y_cof[3] = p0.y;
		return y_cof;
	}
	public static float[] cubicRoots(float[] arr) {
		
		float c0 = arr[0];
		float c1 = arr[1];
		float c2 = arr[2];
		float c3 = arr[3];
		
		//Turn into form: 0 = x^3 + Ax^2 + Bx + C (by dividing all coeffs by c0)
		float a = c1/c0;
		float b = c2/c0;
		float c = c3/c0;
		
		float q_var = (3*b - a*2)/9;
		float r_var = (float) ((3*a*b - 27*c - Math.pow(a, 3))/54);		
		float dscrm = (float) (Math.pow(q_var, 3) + Math.pow(r_var, 2));
		
		float[] roots = new float[3];
////////
		if (dscrm >= 0) {	//complex or duplicate roots because discriminate is > 0 (complex) or == 0 (duplicate)
			//Get S and T with sign of discriminant to correct for Math's returns
			float s_var = (float)(sign(r_var+(float)Math.sqrt(dscrm))* Math.pow(r_var + Math.sqrt(dscrm), 1/3));
			float t_var = (float)(sign(r_var-(float)Math.sqrt(dscrm))* Math.pow(r_var - Math.sqrt(dscrm), 1/3));

			roots[0] = -a/3 + (s_var + t_var);                    // real root
	        roots[1] = -a/3 - (s_var + t_var)/2;                  // real part of complex root
	        roots[2] = -a/3 - (s_var + t_var)/2;                  // real part of complex root
	        
	        double im = Math.abs(Math.sqrt(3)*(s_var - t_var)/2);    // complex part of root pair   
	        if (im!=0) {
	            roots[1]=-1;
	            roots[2]=-1;
	        }
				
		}
		else {	//distinct real roots
			
			float theta = (float) Math.acos(r_var/Math.sqrt(-Math.pow(q_var, 3)));
			roots[0] = (float) (2*Math.sqrt(-q_var)*Math.cos(theta/3) - a/3);
			roots[1] = (float) (2*Math.sqrt(-q_var)*Math.cos((theta+2*Math.PI)/3) - a/3);
			roots[2] = (float) (2*Math.sqrt(-q_var)*Math.cos((theta+4*Math.PI)/3) - a/3);
			//im = 0f;
		}
		
		for (int i=0; i<roots.length; i++) 
			if (roots[i] < 0 || roots[i] > 0) 
				roots[i] = -1;
			
		//Sort roots and put -1 vals at end
		
//		console.log(t[0]+" "+t[1]+" "+t[2]);
    	return roots;
	}	
	public void collidesLine(LineSegment l) {
		float A = l.end.y - l.start.y;
		float B = l.start.x - l.end.x;
		float C = l.start.x*(l.start.y-l.end.y) + l.start.y*(l.end.x-l.start.x);
		
		float[] bx = bezierCoeffsX(start, ctrl_one, ctrl_two, end);
		float[] by = bezierCoeffsY(start, ctrl_one, ctrl_two, end);
		
		float[] pArr = new float[4];
		pArr[0] = A*bx[0] + B*by[0];		//t^3
		pArr[1] = A*bx[1] + B*by[1];		//t^2
		pArr[2] = A*bx[2] + B*by[2];		//t
		pArr[3] = A*bx[3] + B*by[3] + C;	//1
		
		float[] t = cubicRoots(pArr);
		Vec2f[] pts = new Vec2f[t.length];
		
		for (int i=0; i<t.length; i++) 
			pts[i] = calculateBezierPoint(t[i]);
	}
	
	/**collision detection*/
	@Override
	public boolean collides(Shape s) {
		return s.collidesCurve(this);
	}
	@Override
	public boolean collidesCircle(CircleShape c) {	
		Vec2f on = this.nearestPointOnCurve(c.getCentroid());
		float dist = on.dist(c.getCentroid());
		Vec2f mtv = on.minus(c.getCentroid()).normalized().smult(c.getRadius()-dist);
		if (dist <= c.getRadius()) {
			this.setCollisionInfo(new CollisionInfo(this, c, mtv));
			c.setCollisionInfo(new CollisionInfo(c, this, mtv.smult(-1)));
			return true;
		}
		return false;
	}
	@Override
	public boolean collidesAAB(AARectShape aab) {
		// TODO Auto-generated method stub
//		System.out.println("collidesAAB not implemented (CubicBezierCurve)");
		return false;
	}
	@Override
	public boolean collidesCompound(CompoundShape c) {
		// TODO Auto-generated method stub
//		System.out.println("collidesCompound not implemented (CubicBezierCurve)");
		return false;
	}
	@Override
	public boolean collidesPolygon(PolygonShape p) {
		// TODO Auto-generated method stub
		boolean collision = false;
/*		Vec2f[] verts = p.vertices();
		for (int i=0; i<verts.length; i++) {
			Vec2f src = verts[i];
			Vec2f dst;
			if (i < verts.length-1)
				dst = verts[i+1];			
			else
				dst = verts[0];
			LineSegment side = new LineSegment(src, dst);	*/
			
			//Preferred:
			//Checking for intersections by rotating/translating to x-axis, then root finding
/*			if (side.collidesCurve(this) == true)
				collision = true;*/
			
			//Worst case:
			//Trying to check for intersections of line segments
/*			for (LineSegment seg: _segs) {
				if (side.lineIntersection(seg) != null)
					collision = true;
			}*/
//		}
		return collision;
	}
	@Override
	public boolean contains(Vec2f pnt) {
		// TODO Auto-generated method stub
//		System.out.println("contains not implemented (CubicBezierCurve)");
		return false;
	}
	@Override
	public Vec2f projectOnto(SeparatingAxis sep) {
		// TODO Auto-generated method stub
//		System.out.println("projectOnto not implemented (CubicBezierCurve)");
		return new Vec2f(0, 0);
	}
}
