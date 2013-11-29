package miweinst.gravidog;

import java.awt.Graphics2D;

import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.PolygonShape;
import cs195n.Vec2f;

/* Just threw this together, we could probably use it for
 * the level menu, and can store info in here about whether
 * the player has beaten it and how many stars it has. And
 * could even have it hold a sprite, or
 * text, or whatever.*/

public class LevelBox extends PolygonShape {
	
	private PolygonShape _box;
	private boolean _levelOpen;
	private int _levelNumber;
	
	/*Yay constructors*/
	public LevelBox() {
		super(new Vec2f[1]);
		_levelNumber = 0;
		this.setLocation(new Vec2f(0, 0));
		this.setDimensions(new Vec2f(25, 25));
		init();
	}
	public LevelBox(Vec2f loc, Vec2f dim) {
		super(new Vec2f[1]);
		_levelNumber = 0;
		this.setLocation(loc);
		this.setDimensions(dim);
		init();
	}
	public LevelBox(Vec2f loc, Vec2f dim, int levelNum) {
		super(new Vec2f[1]);
		_levelNumber = levelNum;
		this.setLocation(loc);
		this.setDimensions(dim);
		init();
	}
	public LevelBox(int levelNum) {
		super(new Vec2f[1]);
		_levelNumber = levelNum;
		this.setLocation(new Vec2f(0, 0));
		this.setDimensions(new Vec2f(25, 25));
		init();
	}
	/*Factor out some code from multiple constructors*/
	private void init() {
		//Only first level available at first.
		_levelOpen = (_levelNumber==1)? true: false;		
		_box = new AARectShape(this.getLocation(), this.getDimensions()).rectToPoly();
	}
	
	/* This is not a comment because my code is
	 * so clear and does not need comments.*/
	public int getLevelNumber() {
		return _levelNumber;
	}
	public void setLevelNumber(int num) {
		_levelNumber = num;
	}
	public boolean isLevelOpen() {
		return _levelOpen;
	}
	public void setLevelOpen(boolean playable) {
		_levelOpen = playable;
	}
	
	public void draw(Graphics2D g) {
		_box = new AARectShape(this.getLocation(), this.getDimensions()).rectToPoly();
		_box.draw(g);
	}
}
