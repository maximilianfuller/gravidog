package miweinst.engine.beziercurve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import miweinst.engine.collisiondetection.CollisionInfo;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.PolygonShape;
import miweinst.engine.gfx.shape.Shape;
import cs195n.Vec2f;

public class CubicBezierCurve extends BezierCurve {
	
	public Vec2f start, ctrl_one, ctrl_two, end;
	private Vec2f[] _points;
	
	private ArrayList<LineSegment> _segs;
	private ArrayList<Vec2f> _pois;
	private ArrayList<Vec2f> _mtvs;
//////	
//	private ArrayList<CircleShape> _dots;
	
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
		
////////
		_pois = new ArrayList<Vec2f>();
		_mtvs = new ArrayList<Vec2f>();
//		_dots = new ArrayList<CircleShape>();
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
	public void updatePointArr() {
		_points[0] = start;
		_points[1] = ctrl_one;
		_points[2] = ctrl_two;
		_points[3] = end;
		updateSegs();
	}
	/*Other direction: Updates vars for each point if pointsArr has been changed.*/
	public void updatePointVars() {
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
///////	
/*		for (CircleShape c: _dots) {
			c.draw(g);
		}*/
	}
	
	
	/**public static methods*/
	/*Returns sign of specified float as 1 or -1*/
	public static int sgn(double d) {
	    if (d < 0.0) 
	    	return -1;
	    return 1;
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
/*		Vec2f[] coeffs = bezierCoefficients(p0, p1, p2, p3);
		for (int i=0; i<coeffs.length; i++) {
			x_cof[i] = coeffs[i].x;
		}*/
		x_cof[0] = -p0.x + 3*p1.x - 3*p2.x + p3.x;	//t^3
		x_cof[1] = 3*(p0.x - 2*p1.x + p2.x);		//t^2
		x_cof[2] = 3*(-p0.x + p1.x);				//t
		x_cof[3] = p0.x;							//1 
		return x_cof;
	}
	public static float[] bezierCoeffsY(Vec2f p0, Vec2f p1, Vec2f p2, Vec2f p3) {
		float[] y_cof = new float[4];
/*		Vec2f[] coeffs = bezierCoefficients(p0, p1, p2, p3);
		for (int i=0; i<coeffs.length; i++) {
			y_cof[i] = coeffs[i].y;
		}*/
		y_cof[0] = -p0.y + 3*p1.y - 3*p2.y + p3.y;	//t^3
		y_cof[1] = 3*(p0.y - 2*p1.y + p2.y);		//t^2
		y_cof[2] = 3*(-p0.y + p1.y);				//t
		y_cof[3] = p0.y;							//1
		return y_cof;
	}
	/* Returns array of roots of cubic polynomial in form of:
	 *  arr[0]t^3 + arr[1]t^2 + arr[2]t + arr[3]. 
	 * Specified array of floats specifies coefficients of polynomial.
	 * Non-real or out of range roots set to -1.*/
	public static float[] cubicRoots(float[] arr) {
		//Coefficients
		float c0 = arr[0];
		float c1 = arr[1];
		float c2 = arr[2];
		float c3 = arr[3];	
		//Turn into form: 0 = x^3 + Ax^2 + Bx + C [by dividing all coeffs by c0 (so coefficient of x^3 is 1)]
		float a = c1/c0;
		float b = c2/c0;
		float c = c3/c0;
	
		//calculate vars of cubic formula
		float q = (3*b - a*a)/9;
		float r = (9*a*b - 27*c - 2*a*a*a)/54;		
		float dscrm = q*q*q + r*r;
		//return array
		float[] roots = new float[3];
		
		//complex or duplicate roots; discrim > 0 (complex), or discrim == 0 (duplicate real)
		if (dscrm >= 0) {	
			//Get S and T with sign of discrim
			float s = r + (float)Math.sqrt(dscrm);
			s = (float) (s<0? -Math.pow(-s, 1/3) : Math.pow(s,  1/3));
//			float s = sgn(r+(float)Math.sqrt(dscrm))*((float)Math.pow(Math.abs(r + Math.sqrt(dscrm)), (1/3)));
			float t = r - (float)Math.sqrt(dscrm);
			t = (float) (t<0? -Math.pow(-t, 1/3) : Math.pow(t, 1/3));
//			float t = sgn(r-(float)Math.sqrt(dscrm))*((float)Math.pow(Math.abs(r - Math.sqrt(dscrm)), (1/3)));
			roots[0] = -a/3 + s + t;                    // real root		
			//Don't need non-real roots for interesctions
	        roots[1] = -a/3 - (s + t)/2;                  // real part of complex root
	        roots[2] = -a/3 - (s + t)/2;                  // real part of complex root
	        double im = Math.abs(Math.sqrt(3)*(s - t)/2);    // complex part of root pair   
	        //discard complex roots
	        if (im != 0) {
	            roots[1]=-1;
	            roots[2]=-1;
	        }
		}
		//distinct real roots; discrim is < 0
		else {	
			float theta = (float) Math.acos(r/Math.sqrt(-Math.pow(q, 3)));
			roots[0] = (float) (2*Math.sqrt(-q)*Math.cos(theta/3)) - a/3;
			roots[1] = (float) (2*Math.sqrt(-q)*Math.cos((theta+2*Math.PI)/3)) - a/3;
			roots[2] = (float) (2*Math.sqrt(-q)*Math.cos((theta+4*Math.PI)/3)) - a/3;
			//im = 0f;
		}

///////Necessary? <=/>= or </> (I think former, because t is [0,1], but online has latter	
		//discard roots before or after curve endpoints [out of range of t [0, 1]]
/*		for (int i=0; i<3; i++) {
			if (roots[i] < 0 || roots[i] > 1) {
				roots[i] = -1;
			}
		}	*/
//////Need to sort roots and put -1 vals at end?		
    	return roots;
	}	
/////////////////////REWRITE THIS////////////////////////
	/* Takes in four coefficients in c as [0, t, t^2, t^3] and 
	 * an array s. Returns number of real roots and populates s.
	 * c = double[4], s = double[3]*/
	public static int solveCubic(double[] c, double[] s)  {
		int i, num;  
		double sub;  
		double A, B, C;  
		double sq_A, p, q;  
		double cb_p, D;  
		/* turn into normal form: x^3 + Ax^2 + Bx + C = 0 */  
		A = c[ 2 ] / c[ 3 ];  
		B = c[ 1 ] / c[ 3 ];  
		C = c[ 0 ] / c[ 3 ];  

		/*  substitute x = y - A/3 to eliminate quadric term: 
			x^3 +px + q = 0 */  
		sq_A = A * A;  
		p = 1.0/3 * (- 1.0/3 * sq_A + B);  
		q = 1.0/2 * (2.0/27 * A * sq_A - 1.0/3 * A * B + C);  

		/* use Cardano's formula */  
		cb_p = p * p * p;  
		D = q * q + cb_p;  

		if (D == 0)  {  
			/* one triple solution */  
			if (q == 0) {  
				s[ 0 ] = 0;  
				num = 1;  
			}  
			/* one single and one double solution */
			else  {  
				double u = Math.cbrt(-q);  
				s[ 0 ] = 2 * u;  
				s[ 1 ] = - u;  
				num = 2;  
			}  
		}  
		/* three real solutions */
		else if (D < 0) {  
			double phi = 1.0/3 * Math.acos(-q / Math.sqrt(-cb_p));  
			double t = 2 * Math.sqrt(-p);  

			s[ 0 ] =   t * Math.cos(phi);  
			s[ 1 ] = - t * Math.cos(phi + Math.PI / 3);  
			s[ 2 ] = - t * Math.cos(phi - Math.PI / 3);  
			num = 3;  
		}  
		/* one real solution*/
		else {  
			double sqrt_D = Math.sqrt(D);  
			double u = Math.cbrt(sqrt_D - q);  
			double v = - Math.cbrt(sqrt_D + q);  
			s[ 0 ] = u + v;  
			num = 1;  
		}  
		/* resubstitute */  
		sub = 1.0/3 * A;  
		for (i = 0; i < num; ++i)  
			s[ i ] -= sub;  
		return num;  
	}  
/////////////////////////////////////^^^^^^^^^^^^
	
	/*Calculates intersection between curve and line.
	 * Useful for POI and collision detection. */
	public ArrayList<Vec2f> collidesLine(LineSegment l) {
		//Express line in form: Ax + By + C = 0
		float A = l.end.y - l.start.y;		//A = y2-y1
		float B = l.start.x - l.end.x;		//B = x1-x2
		float C = l.start.x*(l.start.y-l.end.y) + l.start.y*(l.end.x-l.start.x);	//C = x1*(y1-y2)+y1*(x2-x1)
		//Coefficients of Bezier polynomial
		float[] bx = bezierCoeffsX(start, ctrl_one, ctrl_two, end);
		float[] by = bezierCoeffsY(start, ctrl_one, ctrl_two, end);
		//Plug in Bezier coefficients into line equation to get degree-3 polynomial
		double[] pArr = new double[4];
		pArr[3] = A*bx[0] + B*by[0];		//t^3
		pArr[2] = A*bx[1] + B*by[1];		//t^2
		pArr[1] = A*bx[2] + B*by[2];		//t
		pArr[0] = A*bx[3] + B*by[3] + C;	//1
		//Find roots of the new polynomial, curve plugged into line 
		double[] r = new double[3];
		solveCubic(pArr, r);
		
		//Three roots; given -1 if invalid or imaginary
		assert r.length == 3;	
		
		ArrayList<Vec2f> pts = new ArrayList<Vec2f>();
		for (int i=0; i<3; i++) {
			//get t for each root
			float t = (float) r[i];		
			//Plug in t_val for root into polynomial
			Float x0 = bx[0]*t*t*t+bx[1]*t*t+bx[2]*t+bx[3];
			Float y0 = by[0]*t*t*t+by[1]*t*t+by[2]*t+by[3];
			Vec2f p = new Vec2f(x0, y0);
			
			//Add poi to list, then remove if out of range
			pts.add(p);
		
			//Check if intersections are in bounds of line seg (so far, intersections with infinite line)
			float check;
			//If not vertical line
			if ((l.end.x - l.start.x) != 0) {	
				check = (x0 - l.start.x)/(l.end.x - l.start.x);	// s=(X[0]-lx[0])/(lx[1]-lx[0]);
			}
			else {
				check = (y0 - l.start.y)/(l.end.y - l.start.y);	//s=(X[1]-ly[0])/(ly[1]-ly[0]);
			}
			//If poi is out of t e[0, 1] or off line seg, remove
			if (t<=0 || t>=1.0 || check<0 || check>1.0) {
				pts.remove(p);
			}
		}
		return pts;
	}
	
	
	/**collision detection and POI*/
	/*Double dispatch for collision and poi*/
	@Override
	public boolean collides(Shape s) {
		return s.collidesCurve(this);
	}
	@Override
	public Vec2f poi(Shape s) {
		return s.poiCurve(this);
	}
	
	/*Collision and point of intersection for circles. POI returns 
	 * nearest point to circle's center on curve. Doesn't really matter,
	 * since circles have arbitrary rotation and curves don't rotate.*/
	@Override
	public boolean collidesCircle(CircleShape c) {			
		Vec2f p = this.nearestPointOnCurve(c.getCentroid());
		float dist = p.dist(c.getCentroid());
		Vec2f mtv = p.minus(c.getCentroid()).normalized().smult(c.getRadius()-dist);
		if (dist <= c.getRadius()) {
			this.setCollisionInfo(new CollisionInfo(this, c, mtv));
			c.setCollisionInfo(new CollisionInfo(c, this, mtv.smult(-1)));			
			return true;
		}
		return false;
	}
	@Override
	public Vec2f poiCircle(CircleShape c) {
		return this.nearestPointOnCurve(c.getCentroid());
	}
	
	@Override
	public boolean collidesAAB(AARectShape aab) {
		PolygonShape p = aab.rectToPoly();
		boolean collides = this.collidesPolygon(p);
		aab.setCollisionInfo(p.getCollisionInfo());
		return collides;
	}
	
	/*Collision and point of intersection for Polygons.*/
	@Override
	public boolean collidesPolygon(PolygonShape p) {
		// TODO Auto-generated method stub
		boolean collision = false;
		Vec2f[] verts = p.getVertices();
		_mtvs = new ArrayList<Vec2f>();
		
		Vec2f mintv = null;
		float minDist = Float.POSITIVE_INFINITY;
		
		for (int i=0; i<verts.length; i++) {
			Vec2f src = verts[i];
			//if not last segment, endpoint is next vertex
			Vec2f dst;
			if (i < verts.length-1) 
				dst = verts[i+1];
			else 
				dst = verts[0];
			LineSegment seg = new LineSegment(src, dst);
			ArrayList<Vec2f> pts = this.collidesLine(seg);
			if (!pts.isEmpty()) {
				collision = true;
				_pois = pts;
///////// MTV calc...
				for (Vec2f poi: pts) {
					float ldist = poi.dist2(dst);
					float rdist = poi.dist2(src);
					if (ldist < rdist) {
						_mtvs.add(dst.minus(poi));
						if (ldist < minDist) {
							minDist = ldist;
							mintv = dst.minus(poi);
						}
					}
					else {
						_mtvs.add(src.minus(poi));
						if (rdist < minDist) {
							minDist = rdist;
							mintv = src.minus(poi);
						}
					}
				}	
			}				
///////////	VIEW POIS
/*			for (Vec2f pt: pts) {
				float rad = .8f;
				CircleShape c = new CircleShape(new Vec2f(pt.x-rad, pt.y-rad), rad);
				c.setColor(Color.WHITE);
				_dots.add(c);
			}*/
////////^^^^^^
		}
		
		if (!_mtvs.isEmpty()) {
//			Vec2f mtv = Vec2f.average(_mtvs);
			Vec2f mtv = mintv;
			this.setCollisionInfo(new CollisionInfo(this, p, mtv));
			p.setCollisionInfo(new CollisionInfo(p, this, mtv.smult(-1)));
		}
		
		return collision;
	}
	@Override
	public Vec2f poiPolygon(PolygonShape p) {
		// TODO Auto-generated method stub
		//update _pois
//		return p.getCentroid();
		collidesPolygon(p);
		if (_pois.isEmpty())
			return null;
		return Vec2f.average(_pois);
	}
}
