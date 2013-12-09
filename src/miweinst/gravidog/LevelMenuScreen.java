package miweinst.gravidog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import miweinst.engine.App;
import cs195n.Vec2f;

public class LevelMenuScreen extends GravidogScreen {	
	public static int CURRENT_LEVEL = 1;
	private static HashMap<Integer, Integer> star_map = new HashMap<Integer, Integer>();
	private static HashMap<Integer, Integer> max_star_map = new HashMap<Integer, Integer>();
	
	private ArrayList<LevelBox> _boxes;
		
	public LevelMenuScreen(App a) {
		super(a);
		setBackgroundColor(Color.WHITE);
		_boxes = new ArrayList<LevelBox>();
			
		//Open
		LevelBox first = new LevelBox(1);
		first.box.setColor(Color.PINK);
		first.setLevelOpen(true);
		//Closed
		LevelBox second = new LevelBox(2);
		second.box.setColor(Color.LIGHT_GRAY);
		second.setLevelOpen(true);
		//Closed
		LevelBox third = new LevelBox(3);
		third.box.setColor(Color.LIGHT_GRAY);
		third.setLevelOpen(true);
		//Closed
		LevelBox fourth = new LevelBox(4);
		fourth.box.setColor(Color.LIGHT_GRAY);
		fourth.setLevelOpen(true);
		
		_boxes.add(first);
		_boxes.add(second);
		_boxes.add(third);
		_boxes.add(fourth);
	}
	
	/** GO! */
	private void startLevel(String lvlPath) {
		if (lvlPath != null)
			app.setScreen(new PlayScreen(app, new File(lvlPath)));
	}
	
	/* Information on level menu, draws updated menu. */
	/**Sets the box for the specified level
	 * number to be open. Sets frame visible. */
	public void openLevel(int boxNumber) {
		//Adjust for zero-indexing for ArrayList
		_boxes.get(boxNumber-1).setLevelOpen(true);
	}
	
	/* Star methods */
	/**Increments by one star on current level.*/	
	public static void addStar() {
		if (star_map.containsKey(CURRENT_LEVEL-1)) {
			//Zero indexing
			star_map.put(CURRENT_LEVEL-1, star_map.get(CURRENT_LEVEL-1) + 1);
		}
		else {
			star_map.put(CURRENT_LEVEL-1, 1);
		}
	}
	/** Returns 0 if lvl not contained in HashMap*/
	public static int getStars() {
		int lvl = CURRENT_LEVEL;
		if (star_map.containsKey(lvl))
			return star_map.get(lvl);
		else
			return 0;
	}
	/**Returns star_map earned at specified level number*/
	public static int getStarsFor(int level) {
		return star_map.get(level);
	}
	
	/**Sets stars of LevelBox to current stars if
	 * the score is higher than max score, else to max
	 * score again.*/
	public void updateStars() {
		for (int i=0; i<_boxes.size(); i++) {
			if (star_map.containsKey(i)) {
				int stars = star_map.get(i);
				if (max_star_map.containsKey(i)) {
					if (stars > max_star_map.get(i)) {
						max_star_map.put(i, stars);
					}				
				}
				else {
					max_star_map.put(i, stars);
				}
			}
			//Levels not yet played through
			else {
				_boxes.get(i).setStars(0);
				if (!max_star_map.containsKey(i))
					max_star_map.put(i, 0);
			}
			_boxes.get(i).setStars(max_star_map.get(i));
		}
		clearStars();
	}
	/**Clears current level's star information, but retains
	 * high score star information for all levels.*/
	public void clearStars() {
		star_map.clear();
	}
	
	/* User Input methods */
	@Override 
	public void onMousePressed(MouseEvent e) {
		super.onMousePressed(e);
		for (LevelBox box: _boxes) { 
			if (box.contains(new Vec2f(e.getX(), e.getY()))) {
				if (box.isLevelOpen()) {
					CURRENT_LEVEL = box.level_num;
					this.startLevel(box.level_path);
				}
			}	
		}
	}
	
	@Override
	public void onMouseMoved(MouseEvent e) {
		super.onMouseMoved(e);
		Vec2f mouseLoc = new Vec2f(e.getX(), e.getY());
		for (LevelBox box: _boxes) {
			if (box.contains(mouseLoc)) 
				box.onMouseOver();
			else
				box.onMouseOut();
		}
	}
	
	
	@Override
	public void onDraw(Graphics2D g) {
		super.onDraw(g);
		for (LevelBox box: _boxes) {
			box.draw(g);
		}
	}
}
