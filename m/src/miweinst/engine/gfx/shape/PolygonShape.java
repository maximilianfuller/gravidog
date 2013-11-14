package miweinst.engine.gfx.shape;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import cs195n.Vec2f;
import miweinst.engine.beziercurve.BezierCurve;
import miweinst.engine.collisiondetection.CollisionInfo;
import miweinst.engine.collisiondetection.SeparatingAxis;

/**
 * This class takes in an array of 
 * Vec2f vertices that must be 
 * in counter-clockwise order!
 * @author miweinst
 */

public class PolygonShape extends Shape {
	public static final String string = "PolygonShape";
	
	//Location stored in Shape, reference
	private Vec2f _location;
	//Stores points of path
	private Vec2f[] _vertices;
	
/////  Stores other Shape in collision (K) and MTV (V)
//	private HashDecorator<Shape, Vec2f> _collisionDecorator;
	private CollisionInfo _collisionInfo;
	
	//Vals array should already be in counter-clockwise order
	public PolygonShape(Vec2f loc, Vec2f[] ccverts) {
		super(loc, new Vec2f(0, 0));	//dimensions N.A for poly
		_location = loc;
		super.setLocation(loc);
		_vertices = ccverts;
		_collisionInfo = null;
	}
	
	/*Returns a Path2D object representing this Polygon*/
	public Path2D toPath() {
		Path2D.Float path = new Path2D.Float();
		path.moveTo(_vertices[0].x, _vertices[0].y);
		for (int i=1; i<_vertices.length; i++) {
			Vec2f v = _vertices[i];
			path.lineTo(v.x, v.y);
		}
		path.closePath();
		return path;
	}
	
	/*Returns number of vertices stored in Polygon*/
	public int getSize() {
		return _vertices.length;
	}
	
	/*Returns an array containing vertices.*/
	public Vec2f[] vertices() {
		return _vertices;
	}
	
	/*Move reference location and maintain vertices
	 * relative positions by moving same amount.*/
	public void setLocation(Vec2f loc) {
		super.setLocation(loc);
		float deltax = loc.x - _location.x;
		float deltay = loc.y - _location.y;
		//translate: moves vertices, updates arrays
		this.translate(deltax, deltay);	
		_location = loc;
	}
	
	/*Move polygon location/vertices by delta values.*/
	public void translate(float dx, float dy) {
		Vec2f[] copy = new Vec2f[_vertices.length];
		for (int i=0; i<_vertices.length; i++) {
			Vec2f v = _vertices[i];
			copy[i] = new Vec2f(v.x+dx, v.y+dy);
		}
		_vertices = copy;	
		Vec2f newLoc = new Vec2f(_location.x+dx, _location.y+dy);
		_location = newLoc;
		super.setLocation(newLoc);
	}
	
	/*Sets location without updating vertices. 
	 * Polygon does not actually move, only
	 * changes _locations relative position to vertices.*/
	public void setLocationReference(Vec2f ref) {
		super.setLocation(ref);
		_location = ref;
	}
	
	/*Returns the vertex Vec2f that has
	 * the minimum X or Y values.*/
	public Vec2f getMinX() {
		Vec2f min = new Vec2f(100, 100);	
		boolean first = true;
		for (Vec2f v: _vertices) {
			if (first) {
				min = v;
				first = false;
			}
			if (v.x < min.x) {
				min = v;
			}
		}
		return min;
	}
	/*Minimum Y value of vertices*/
	public Vec2f getMinY() {
		Vec2f min = new Vec2f(0, 0);
		boolean first = true;
		for (Vec2f v: _vertices) {
			if (first) {
				min = v;
				first = false;
			}
			if (v.y < min.y) {
				min = v;
			}
		}
		return min;
	}	
	/*Returns the vertex Vec2f that has
	 * the Maximum X or Y values.*/
	public Vec2f getMaxX() {
		Vec2f max = new Vec2f(100, 100);		
		boolean first = true;
		for (Vec2f v: _vertices) {
			if (first) {
				max = v;
				first = false;
			}
			if (v.x > max.x) {
				max = v;
			}
		}
		return max;
	}
	/*Maximum Y value of vertices*/
	public Vec2f getMaxY() {
		Vec2f max = new Vec2f(0, 0);
		boolean first = true;
		for (Vec2f v: _vertices) {
			if (first) {
				max = v;
				first = false;
			}
			if (v.y > max.y) {
				max = v;
			}
		}
		return max;
	}
	
	/*Returns smallest rect parallel to
	 * coordinate axes containing polygon.*/
	public Rectangle2D getBounds() {
		return this.toPath().getBounds2D();
	}
	
	/*Finds polygon vertex closest to point pt.*/
	public Vec2f closestVertex(Vec2f pt) {
		Vec2f closestVert = this.vertices()[0];			
		//Find closest vertex to circle's center
		for (int i=0; i < this.vertices().length; i++) {
			Vec2f vert = this.vertices()[i];		
			if (pt.dist2(vert) < pt.dist2(closestVert)) {
				closestVert = vert;
			}
		}	
		return closestVert;
	}

	/*Returns whether specified point is contained in Polygon. 
	 * Uses cross products to check point's orientation to 
	 * the vector constructed along any edge. Points in Polygon
	 * must be stored in counterclockwise order.
	 * Also, this is only for a Polygon path that has
	 * straight edges. No quad curves are accepted,
	 * because currentSegment() would return more than 
	 * 2 values and this method would throw ArryOutofBoundsException*/
	public boolean contains(Vec2f v) {
		PathIterator iter = this.toPath().getPathIterator(null);
		boolean first = true;
		Vec2f src = new Vec2f(0, 0);
		Vec2f dst = new Vec2f(0, 0);
		Vec2f start = new Vec2f(0, 0);
		while (iter.isDone() == false) {
			if (first) {
				float[] vert = new float[2];
				iter.currentSegment(vert);
				src = new Vec2f(vert[0], vert[1]);
				first = false;
				//Store first endpoint for last vector
				start = src;			
			}
			else {
				float[] vert = new float[2];
				iter.currentSegment(vert);
				dst = new Vec2f(vert[0], vert[1]);
				//If at last vertex, get vector back to start vertex
				if (iter.currentSegment(vert) == PathIterator.SEG_CLOSE) {
					dst = start;
				}
				//Get vector from one end point to next
				Vec2f pnt = new Vec2f(dst.y-src.y, dst.x-src.x);
				Vec2f end = new Vec2f(v.y-src.y, v.x-src.x);
				//If ANY cross-product is less than 0, false
				float cross = pnt.cross(end);
				if (cross < 0) {
					return false;
				}
				//Set new start point to end point, for next vector
				src = dst;
			}		
			iter.next();
		}
		return true;
	}
	
	/*Creates a Path2D of the same values/vertices
	 * as this PolygonShape, and passes it to superclass
	 * so it is drawn.*/
	@Override
	public void draw(Graphics2D g) {
		Path2D path = this.toPath();		
		super.setShape(path);
		super.draw(g);		
//////		
/*		if (_testPath != null) {
			g.setColor(Color.RED);
			g.draw(_testPath);
		}*/
///^^^^
	}
	
	/*ShapeCollisionDetection implementation of interface.*/

	@Override
	public boolean collides(Shape s) {
		return s.collidesPolygon(this);
	}

	/* Checks collision between Polygon and Circle. Uses
	 * separating axis from circle center to closest Polygon
	 * vertex, and uses axes of all normals of Polygon edges.
	 * Also checks if Polygon contains the center of the circle,
	 * or if the circle contains the closest vertex on the Polygon,
	 * to save computation if obviously true.*/
	@Override
	public boolean collidesCircle(CircleShape c) {			
		Vec2f[] allAxes = new Vec2f[this.vertices().length+1];
		Float minMag = Float.POSITIVE_INFINITY;
		Vec2f mtv = null;
		//Populated allAxes with Polygon edge normals
		Vec2f[] verts = this.vertices();	
		Vec2f start = null;
		Vec2f end = null;		
		for (int i=0; i<verts.length; i++) {	
			if (i < verts.length-1) {
				start = new Vec2f(verts[i].x, verts[i].y);
				end = new Vec2f(verts[i+1].x, verts[i+1].y);
			}
			else {
				start = new Vec2f(verts[i].x, verts[i].y);
				end = new Vec2f(verts[0].x, verts[0].y);
			}				
			Vec2f edge = end.minus(start).normalized();
			Vec2f normal = new Vec2f(-edge.y, edge.x).normalized();	
			allAxes[i] = normal;
		}		
		//Finish allAxes with circle-poly separating axis
		Vec2f closestVert = this.closestVertex(c.getCenter());				
		allAxes[allAxes.length-1] = closestVert.minus(c.getCenter()).normalized();	
		//Get minimum MTV and populate CollisionInfo attributes
		for (int i=0; i<allAxes.length; i++) {
			Vec2f axis = allAxes[i];
			SeparatingAxis sepAxis = new SeparatingAxis(axis);
			Float mtv1d = sepAxis.intervalMTV(sepAxis.project(c), sepAxis.project(this));
			if (mtv1d == null) {
				return false;
			}
			if (Math.abs(mtv1d) < minMag) {
				minMag = Math.abs(mtv1d);
				mtv = axis.smult(mtv1d);
			}
		}
		this.setCollisionInfo(new CollisionInfo(this, c, mtv.sdiv(-1)));
		c.setCollisionInfo(new CollisionInfo(c, this, mtv));	
		return true;
	}
	

	/*This method reuses the code from collidesPolygon by
	 * creating PolygonShape with same four vertices as
	 * AARect. Not the most computationally efficient, because
	 * all four edges don't have to be iterated over, but the
	 * most efficient in man-hours, and get to reuse code.*/
	@Override
	public boolean collidesAAB(AARectShape aab) {
		PolygonShape p = aab.rectToPoly();	
		//Populate collision info
		boolean collision = p.collidesPolygon(this);		
		//Get the collision info generated from collidesPolygon
		aab.setCollisionInfo(p.getCollisionInfo());
		//Return the boolean collision detection result
		return collision;
	}

	/*Reuses code from its component shapes' methods. Checks instance
	 * of component, and calls corresponding collides procedure. If any component
	 * shape reports collision, collision detected.*/
	@Override
	public boolean collidesCompound(CompoundShape c) {
		for (Shape s: c.getShapes()) {
			if (s instanceof AARectShape) {
				if (this.collidesAAB((AARectShape) s)) {
					return true;
				}
			}
			else if (s instanceof CircleShape) {
				if (this.collidesCircle((CircleShape) s)) {
					return true;
				}
			}
			else if (s instanceof PolygonShape) {
				if (this.collidesPolygon((PolygonShape) s)) {
					return true;
				}
			}
			//if nested CompoundShapes; recursive
			else if (s instanceof CompoundShape) {
				if (this.collidesCompound((CompoundShape) s)) {
					return true;
				}
			}
			else System.out.println("Error: Huh? Shape not recognized! (collidesCompound.PolygonShape)");
		}
		return false;
	}
	
	/*Checks for collisions between two covnex polygons using
	 * the Separating Axis Theorem. Iterates over edges of
	 * both polygons consecutively, projecting both shapes
	 * onto the axis formed by the normal of each edge and
	 * then checking for overlap. If a single axis shows
	 * overlap between the projections, no collision is detected.*/
	@Override
	public boolean collidesPolygon(PolygonShape p) {	
		//Concatenate both lists of vertices
		Vec2f[][] both = new Vec2f[2][Math.max(p.vertices().length, this.vertices().length)];	
		both[0] = p.vertices();
		both[1] = this.vertices();	
		Vec2f start = null;
		Vec2f end = null;
///
		Float minMag = Float.POSITIVE_INFINITY;
		Vec2f mtv = null;
		for (Vec2f[] iter: both) {
////
//			System.out.println("Start loop");
			for (int i=0; i<iter.length; i++) {	
////
//				System.out.println(i + ". ");
				if (i < iter.length-1) {
					start = new Vec2f(iter[i].x, iter[i].y);
					end = new Vec2f(iter[i+1].x, iter[i+1].y);
				}
				else {
					start = new Vec2f(iter[i].x, iter[i].y);
					end = new Vec2f(iter[0].x, iter[0].y);
				}				
				//Construct vector along edge, normalize for length
				Vec2f edge = end.minus(start).normalized();
				//Find axis by vector perpendicular to edge vector:
				Vec2f normal = new Vec2f(-edge.y, edge.x);				
				//Make separating axis for each edge
				SeparatingAxis sepAxis = new SeparatingAxis(normal);
				//Get min and max of one Polygon
				Vec2f arange = sepAxis.project(p);
				//Get min and max of other Polygon
				Vec2f brange = sepAxis.project(this);	

				//If ranges don't overlap on every axis, collision NOT detected
				if (sepAxis.isOverlapping(arange, brange)==false) {
					return false;
				}
				
				//Calculate MTV of all axes and store one with min magnitude		
				Float mtv1d = sepAxis.intervalMTV(arange, brange);
				if (mtv1d != null) {
////
//						System.out.println("Polygon mtv1d: " + mtv1d);					
					if (Math.abs(mtv1d) < minMag) {
						minMag = Math.abs(mtv1d);
						
						mtv = normal.smult(mtv1d);
					}
				}
			}
		}	
		//Store this Shape, other Shape and mtv in CollisionInfo cache
		_collisionInfo = new CollisionInfo(this, p, mtv.sdiv(-1));
		p.setCollisionInfo(new CollisionInfo(p, this, mtv));	
		return true;
	}
/////
	@Override
	public CollisionInfo getCollisionInfo() {
		return _collisionInfo;
	}
	@Override
	public void setCollisionInfo(CollisionInfo info) {
		_collisionInfo = info;
	}
/////^^^

	@Override
	public Vec2f projectOnto(SeparatingAxis sep) {
		return sep.project(this);
	}

	@Override
	public boolean collidesCurve(BezierCurve c) {
		// TODO Auto-generated method stub
		return false;
	}
}
