package miweinst.gravidog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import miweinst.engine.App;
import cs195n.Vec2f;

public class LevelMenuScreen extends GravidogScreen {
	
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
		//Closed
		LevelBox third = new LevelBox(3);
		third.box.setColor(Color.LIGHT_GRAY);
		
		_boxes.add(first);
//		_boxes.add(second);
		_boxes.add(third);
	}
	
	/* GO! */
	private void startLevel(String lvlPath) {
		app.setScreen(new PlayScreen(app, new File(lvlPath)));
	}
	
/* PROBABLY GET RID OF THESE METHODS. BUT MIGHT WANT TO OVERRIDE
	IN CASE WE WANT THIS MENU TO DO COOL THINGS LIKE ANIMATION. */
	@Override
	public void onTick(long nanosSincePreviousTick) {
		super.onTick(nanosSincePreviousTick);
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
		super.onKeyPressed(e);
	}
	
	/*Clicks level box*/
	@Override 
	public void onMousePressed(MouseEvent e) {
		super.onMousePressed(e);
		for (LevelBox box: _boxes) { 
			if (box.contains(new Vec2f(e.getX(), e.getY()))) {
				if (box.isLevelOpen()) {
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
