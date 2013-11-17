package miweinst.engine.beziercurve;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import cs195n.Vec2f;

public class LineSegment {
	
	public Vec2f start;
	public Vec2f end;

	public LineSegment() {
		this.start = new Vec2f(0, 0);
		this.end = new Vec2f(0, 10);
	}
	public LineSegment(Vec2f a, Vec2f b) {
		this.start = a;
		this.end = b;
	}
	
	public void setStart(Vec2f s) {
		start = s;
	}
	public void setEnd(Vec2f e) {
		end = e;
	}
	
	/* Returns point of intersection between two line segments,
	 * if intersection occurs within both line segments' endpoints. 
	 * Else returns null. Also null if line segments are parallel (determinant == 0).
	 * Converts each line into form Ax + By = C. then solves for intersection.*/
	public Vec2f lineIntersection(LineSegment other) {
		//Values for line a
		float this_A = this.end.y - this.start.y;
		float this_B = this.end.x - this.start.x;
		float this_C = this_A*this.start.x + this_B*this.start.y;
		//Values for line b
		float other_A = other.end.y - other.start.y;
		float other_B = other.end.x - other.start.x;
		float other_C = other_A*other.start.x + other_B*other.start.y;
		
		float det = this_A*other_B - other_A*this_B;
		//if lines are parallel, no intersection
		if (det == 0) 
			return null;
		else {
			float x = (other_B*this_C - this_B*other_C)/det;
			float y = (this_A*other_C - other_A*this_C)/det;
			
			boolean onA = false;
			boolean onB = false;
			//Check if intersection point lies within line segment a
			if (x >= Math.min(this.start.x, this.end.x) && x <= Math.max(this.start.x, this.end.x)) 
				if (y >= Math.min(this.start.y, this.end.y)&& y <= Math.max(this.start.y, this.end.y))
					onA = true;
			//Check if intersection point lies within line segment b
			if (x >= Math.min(other.start.x, other.end.x) && x <= Math.max(other.start.x, other.end.x)) 
				if (y >= Math.min(other.start.y, other.end.y)&& y <= Math.max(other.start.y, other.end.y))
					onB = true;
			//If lies on both segments, return intersection
			if (onA && onB)
				return new Vec2f(x,y);
			//If lies outside of segments, no intersection
			else 
				return null;
		}
	}
	
	/*Translates line segment by delta values.*/
	public void translate(Vec2f delta) {
		this.start = start.plus(delta);
		this.end = end.plus(delta);
	}
	/*Rotate point*/
	public static Vec2f rotate(Vec2f rotateAround, Vec2f point, float theta) {
		float newX = (float) (Math.cos(theta)*(point.x-rotateAround.x)- Math.sin(theta)*(point.y-rotateAround.y) + rotateAround.x); 
		float newY = (float) (Math.sin(theta)*(point.x-rotateAround.x) + Math.cos(theta)*(point.y-rotateAround.y) + rotateAround.y);
		return new Vec2f(newX, newY);
	}
	/*Moves line segment to x-axis using translation and rotation.*/
	public void toXAxis() {
		//Translate start point to x-axis, straight down
		this.translate(new Vec2f(start.x, -start.y));
		//Rotate end point so line segment is on x-axis
		float theta = (float) Math.atan2(end.y-start.y, end.x-start.x);
		end = rotate(start, end, theta);
	}
	

/*	public boolean collidesCurve(CubicBezierCurve curve) {			
//////
 MAYBE USE A ROTATION/TRANSLATION 3X3 MATRIX? See cs053 lab. 
 * 		Could multiple start/end of line, and all points of curve
		
		//First move so line segment to x-axis
		this.translate(new Vec2f(0, -start.y));
		curve.translate(new Vec2f(0, -start.y));
		//Rotate line segment to x-axis
		float theta = (float) Math.atan2(end.y-start.y, end.x-start.x);
		end = rotate(start, end, theta);
		curve.rotate(curve.start, theta);
		
		//Now do root finding
		
		
		
		//Rotate both line and curve back
		
		//Translate both line and curve back
		
		return false;
	}*/
	
	/* Uses a Path2D in order to maintain floating point 
	 * accuracy. Graphics' drawLine only takes ints.*/
	public void draw(Graphics2D g) {
		Path2D path = new Path2D.Float();
		path.moveTo(start.x, start.y);
		path.lineTo(end.x, end.y);
		g.draw(path);
	}
}
