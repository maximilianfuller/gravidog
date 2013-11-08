package miweinst.engine.gfx.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import miweinst.engine.collisiondetection.CollisionInfo;
import miweinst.engine.collisiondetection.SeparatingAxis;
import miweinst.engine.collisiondetection.ShapeCollisionDetection;
import miweinst.engine.world.GameWorld;
import cs195n.Vec2f;

public abstract class Shape implements ShapeCollisionDetection {
	public static final String string = "Shape";

	private java.awt.Shape _shape;
	private Vec2f _dimension;
	private Vec2f _location;	
	private Color _color;	
	private Color _borderColor;
	private float _borderWidth;
	
	public Shape(Vec2f loc, Vec2f dim) {
		
		_dimension = dim;
		_location = loc;
		
		//Initialize values
		_color = Color.WHITE;
		_borderColor = Color.WHITE;
		_borderWidth = 0;
		
		//Initialize default shape to Rectangle
		_shape = new Rectangle2D.Float();
	}
	
	/* Mutator is protected, because any specific
	 * shapes are set in subclasses instantiated
	 * for that shape. ONLY for subclasses.
	 * Accessor is public so vars declared as
	 * superclass Shape can return reference to 
	 * actual shape, set by subclass instantiated later.*/ 
	protected void setShape(java.awt.Shape shape) {
		_shape = shape;
	}
	public java.awt.Shape getShape() {
		return _shape;
	}
	
	/*Mutators for coordinates*/
	public void setX(float x) {
		setLocation(new Vec2f(x, _location.y));
	}
	public void setY(float y) {
		setLocation(new Vec2f(_location.x, y));
	}
	
	/*Accessors for coordinates*/
	public float getX() {
		return _location.x;
	}
	public float getY() {
		return _location.y;
	}
	
	/*Mutator/Accessor for Vec2f Location storage*/
	public void setLocation(Vec2f loc) {
		_location = loc;
	}
	public Vec2f getLocation() {
		return _location;
	}
	
	/*Mutator/Accessor for only width*/
	public void setWidth(float width){
		float currHeight = _dimension.y;		//Store the current height of shape
		_dimension = new Vec2f(width, currHeight);	//Make vector with new width and old height				
	}
	public float getWidth() {
		return _dimension.x;
	}	
	
	/*Mutator/Accessor for only height*/
	public void setHeight(float height){
		float currWidth = _dimension.x;
		_dimension = new Vec2f(currWidth, height);	//Make vector with old width and new height
	}
	public float getHeight() {
		return _dimension.y;
	}
	
	/*Mutator/accessor for Vec2f dimensions storage*/
	public void setDimensions(Vec2f dim) {
		_dimension = dim;
	}
	public Vec2f getDimensions() {
		return _dimension;
	}
	
	/*Mutator/Accessor for shape fill color*/
	public void setColor(Color color){
		_color = color;
	}
	public Color getColor() {
		return _color;
	}
	/*Mutator/Accessor for border color*/
	public void setBorderColor(Color color) {
		_borderColor = color;
	}
	public Color getBorderColor() {
		return _borderColor;
	}
	
	/*Mutator/Accessor for border stroke width*/
	public void setBorderWidth(float strokeWidth){	
		_borderWidth = strokeWidth;
	}	
	public float getBorderWidth() {
		return _borderWidth;
	}
	/*Make shape only an outline, with specified border attrs.*/
	public void setOutline(Color col, float width) {
		_color = new Color(0, 0, 0, 0);
		_borderColor = col;
		_borderWidth = width;
	}
	
	/*Whether a point is contained in shape. Uses
	 * java.awt.Shape's contain method ONLY for delete_EllipseShape.
	 * Only used for point containment functions,
	 * such as selecting a shape; not collisions.*/
	public abstract boolean contains(Vec2f pnt) ;
	
	/*Properties of all Shape subclasses mapped from strings to 
	 * values as strings.*/
	public void setProperties(Map<String, String> shapeProps) {
		//color
		if (shapeProps.containsKey("color")) {
			String col = shapeProps.get("color");
			this.setColor(GameWorld.stringToColor(col));
		}
		//drawmode
		if (shapeProps.containsKey("drawmode")) {
			if (new String("outline").equals(shapeProps.get("drawmode"))) 
				this.setOutline(this.getColor(), .56f);
		}
		//border_width
		if (shapeProps.containsKey("border_width")) {
			this.setBorderWidth(Float.parseFloat(shapeProps.get("border_width")));
		}
		//border_color
		if (shapeProps.containsKey("border_color")) {
			this.setBorderColor(GameWorld.stringToColor(shapeProps.get("border_color")));
		}			
	}
	
	public void draw (Graphics2D brush){
		//Draw outline of shape
		brush.setStroke(new BasicStroke(_borderWidth));
		brush.setColor(_borderColor);
		brush.draw(_shape);		//Draw shape with border color
		//Fill shape
		brush.setColor(_color);
		brush.fill(_shape);				
	}
	
	/*Sort of double dispatch pattern for projection onto an axis*/
	public abstract Vec2f projectOnto(SeparatingAxis sep);
	
/////
	public CollisionInfo getCollisionInfo() {
		return null;
	}
	public void setCollisionInfo(CollisionInfo info) {
		
	}
/////^^^
}
