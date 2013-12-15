package miweinst.gravidog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
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
	/* Static values can change for resizing */
	//Spacing between cells
	public final static float CELL_SPACING =30f;
	//Size of each cells, square
	public final static float SIDE_LENGTH = 200f;
	//Default width of border
	public final static float BORDER_WIDTH = 10f;
	//Size of star Sprite image
	public static Vec2f STAR_SIZE = new Vec2f(30, 30);
	private static File star_file = new File("src/miweinst/resources/star_small.png");
	private static int COLS = 4;

	//Store level number
	public final int level_num;
	//Box public for customization per lvl
	public AARectShape box;
	//Level path to .nlf file in /resources folder, as String
	public String level_path; 
	//Level attrs
	private boolean _open;
	private int _starsEarned;
//	private int _maxStars;

	private boolean _imgExists = true;
	//"Frame" image for each level on Menu
	private BufferedImage _frame;
	//UI elts
	private Color _color;
	//Star
	private Sprite[] _stars;
	
	public LevelBox(int num) {	
		
		/* Level attributes */
		level_num = num;
		_open = false;	
		//1, 2 or 3
		_starsEarned = 0;		//default val
		//Highest score is saved
//		_maxStars = 0;
		_stars = new Sprite[3];

		/* Loads level data */
		String file_path = null;	
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
		case 5:
			file_path = new String("level_five.nlf");
			break;
		case 6:
			file_path = new String("level_seven.nlf");
			break;
		case 7:
//			file_path = new String("level_seven.nlf");
			file_path = new String("level_eight.nlf");
			break;
		default:	//Just default to Level One
			file_path = new String("level_one.nlf");
			break;
		}		
		if (file_path != null) 
			level_path = new String("src/miweinst/resources/" + file_path);

//////Should load all frames in GravidogResources as a BufferedImage[] in the cache. Once we have all frames.
		/* Loads images */
		File f = null;
		switch(num) {
		case 1:
//			f = new File("src/miweinst/resources/frame_one.jpg");
			_imgExists = false;
			break;
		case 2:
//			f = new File("src/miweinst/resources/frame_one.jpg");
			_imgExists = false;
			break;
		case 3:
//			f = new File("src/miweinst/resources/frame_three.jpg");
			_imgExists = false;
			break;
		case 4:
//			f = new File("src/miweinst/resources/frame_four.jpg");
			_imgExists = false;
			break;
		case 5:
			_imgExists = false;
			break;
		case 6:
			_imgExists = false;
			break;
		case 7:
			_imgExists = false;
			break;
		default:
			//System.out.println("Default " + num);
			f = new File("src/miweinst/resources/frame_one.jpg");
			_imgExists = false;
			break;
		}
		try {
			updateStars();
			//Level frame
			if (f != null) {
				_frame = ImageIO.read(f);	
			}
		} catch(IOException e) {
			//Prints the level frame which is invalide
			System.err.println("Must load new file for image frame for : " + level_path);
			e.printStackTrace();
		}	
///////^^^^

		/* Menu UI */

		//Location of box in horizontal linear layout
		int row = (num-1)/COLS;
		int col = (num-1)%COLS;
		Vec2f loc = new Vec2f(CELL_SPACING*(col+1) + SIDE_LENGTH*(col), CELL_SPACING*(row+1) + SIDE_LENGTH*(row));
		Vec2f dim = new Vec2f(SIDE_LENGTH, SIDE_LENGTH);	//Dim remains constant

		box = new AARectShape(loc, dim);		
		box.setBorderWidth(BORDER_WIDTH);		
		box.setBorderColor(Color.BLACK);	

		_color = Color.LIGHT_GRAY;
	}	
	
	/** Number of stars */
	public int getStars() {
		return _starsEarned;
	}
	/** Set number of stars earned */
	public void setStars(int stars) {
		_starsEarned = stars;
/*		if (stars > _maxStars) {
			_maxStars = stars;
		}*/			
		updateStars();
	}
	
	private void updateStars() {
		//Color stars
		BufferedImage colStar;
		try {
			colStar = ImageIO.read(star_file);
			//Grayscale stars
			ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			BufferedImage bwStar = ImageIO.read(star_file);
			op.filter(bwStar, bwStar);
			for (int i=0; i<_stars.length; i++) {
				if (i < _starsEarned) {
					_stars[i] = new Sprite(STAR_SIZE, colStar);	
				}
				else {
					_stars[i] = new Sprite(STAR_SIZE, bwStar);
				}
			}
		} catch (IOException e) {
			System.out.println("STAR FILE WAS NOT FOUND");
			e.printStackTrace();
		}
//////
		//Reset _starsEarned
		_starsEarned = 0;
	}

	public boolean contains(Vec2f pt) {
		return box.contains(pt);
	}
	public void setColor(Color col) {
		_color = col;
	}

	/*Interactivity*/
	public void onMouseOver() {
		if (isLevelOpen()) 	{
			box.setBorderWidth(BORDER_WIDTH+CELL_SPACING/4);
			box.setBorderColor(new Color(70, 168, 242));
		}
	}
	/* Called when the cursor is NOT over level box.*/
	public void onMouseOut() {
		if(_open) {
			box.setBorderWidth(BORDER_WIDTH);
			box.setBorderColor(Color.BLACK);
		}
	}

	/*Level data (might need for visualization.*/

	/**Whether player can play level.*/
	public boolean isLevelOpen() {
		return _open;
	}
	/**Set level availability to player.*/
	public void setLevelOpen(boolean playable) {
		if (level_path != null)
			_open = playable;
	}

	/** Draws box outline, and Sprites for frame and stars (or fill color if no frame)*/
	public void draw(Graphics2D g) {	
		box.setColor(_color);
		box.draw(g);		
		if (_open) {
			if (_imgExists) {
				Sprite frame = new Sprite(box.getDimensions(),_frame);
				frame.draw(g, box.getLocation(), 1);
			}
			else {
				_color = Color.YELLOW;
			}
			Vec2f drawLoc = new Vec2f(box.getX(), box.getY() + box.getHeight() - CELL_SPACING/2);
			for (int i=0; i<3; i++) {
				if (i == 0)
					_stars[i].draw(g, drawLoc, 1);
				else {
					drawLoc = drawLoc.plus(new Vec2f(STAR_SIZE.x, 0));
					_stars[i].draw(g, drawLoc, 1);
				}
			}
		}
	}
}
