package miweinst.gravidog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import miweinst.engine.gfx.sprite.Sprite;
import miweinst.engine.shape.AARectShape;
import cs195n.Vec2f;

/* Just threw this together, we could probably use it for
 * the level menu, and can store info in here about whether
 * the player has beaten it and how many stars it has. And
 * could even have it hold a sprite, or
 * text, or whatever.
 * 
 * Acts like object that holds information about level, and stores in box.*/

public class LevelBox {
	//Spacing between cells
	public final static float CELL_SPACING = 15f;
	//Size of each cells, square
	public final static float SIDE_LENGTH = 200f;
	//Default width of border
	public final static float BORDER_WIDTH = 10f;

	//Store level number
	public final int level_num;
	//Box public for customization per lvl
	public AARectShape box;
	//Level path to .nlf file in /resources folder, as String
	public String level_path; 
	//Level attrs
	private boolean _open;
	private int _score;

	//"Frame" image for each level on Menu
	private BufferedImage _frame;
	//Booleans for interaction
	private boolean _frameVisible;
	//UI elts
	private Color _color;

	public LevelBox(int num) {	

		/* Loads level data */

		String file_path;	
		switch (num) {
		case 1:
			file_path = new String("level_one.nlf");	
			break;
		case 2: 
			file_path = new String("level_two.nlf");
			break;
		case 3:
			file_path = new String("level_three.nlf");
			break;
		case 4:
			file_path = new String("level_four.nlf");
			break;
		default:	//Just default to Level One
			file_path = new String("level_one.nlf");
			break;
		}		
		level_path = new String("src/miweinst/resources/" + file_path);

		/* Loads frame image */

		File f;
		switch(num) {
		case 1:
			f = new File("src/miweinst/resources/frame_one.jpg");
			break;
		case 2:
			f = new File("src/miweinst/resources/frame_two.jpg");
			break;
		case 3:
			f = new File("src/miweinst/resources/frame_three.jpg");
			break;
		default:
			//System.out.println("Default " + num);
			f = new File("src/miweinst/resources/frame_one.jpg");
			break;
		}

		try {
			_frame = ImageIO.read(f);
		} catch(IOException e) {
			//Prints the level frame which is invalide
			System.err.println("Must load new file for image frame for : " + level_path);
			e.printStackTrace();
		}	

		/* Level attributes */

		level_num = num;
		_open = true;	
		_frameVisible = _open? true: false;
		_score = 0;		//default val

		/* Menu UI */

		//Location of box in horizontal linear layout
		Vec2f loc = new Vec2f(CELL_SPACING*num + SIDE_LENGTH*(num-1), CELL_SPACING);
		Vec2f dim = new Vec2f(SIDE_LENGTH, SIDE_LENGTH);	//Dim remains constant

		box = new AARectShape(loc, dim);		
		box.setBorderWidth(BORDER_WIDTH);		
		box.setBorderColor(Color.BLACK);	

		if (_open) 
			_color = new Color(70, 168, 242);

		else
			_color = Color.LIGHT_GRAY;
	}	

	public boolean contains(Vec2f pt) {
		return box.contains(pt);
	}
	public void setFrameVisible(boolean v) {
		_frameVisible = v;
	}
	public void setColor(Color col) {
		_color = col;
	}

	/*Interactivity*/

	public void onMouseOver() {
		if (isLevelOpen()) 	{
			box.setBorderWidth(BORDER_WIDTH+CELL_SPACING/4);
			box.setBorderColor(_color);
			//This condition makes Sprite disappear when onMouseOver true
			/*			if (_open)
				_frameVisible = false;*/
		}
	}
	/* Called when the cursor is NOT over level box.*/
	public void onMouseOut() {
		if(_open) {
			box.setBorderWidth(BORDER_WIDTH);
			box.setBorderColor(Color.BLACK);
			//			_frameVisible = true;
		}
	}

	/*Level data (might need for visualization.*/

	public boolean isLevelOpen() {
		return _open;
	}
	public void setLevelOpen(boolean playable) {
		_open = playable;
	}

	public int getLevelScore() {
		return _score;
	}
	public void setLevelScore(int stars) {
		_score = stars;
	}

	public void draw(Graphics2D g) {	
		box.setColor(_color);
		box.draw(g);		
		if (_frameVisible) {
			Sprite frame = new Sprite(box.getDimensions(),_frame);
			frame.draw(g, box.getLocation(), 1);
		}
	}
}
