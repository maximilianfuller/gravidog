package miweinst.gravidog;

import java.awt.Color;

/* The Color values in this class are the actual values set. You should 
 * be able to put any colors into the Level editor (might want to so it's easier to visualizer
 * while building the level), but it should NOT affect the real colors in the game.
 * Set all colors within setProperties so it will override any shape color properties
 * set in the level editor. */

public class Constants {
	
	public static final Color BG_COL = new Color(205, 205, 205); //light gray
	public static final Color GRAVITATIONAL_COL = Color.WHITE;
	public static final Color NONGRAVITATIONAL_COL = Color.GRAY;
	public static final Color DOOR_CLOSED_COL = Color.GRAY;
	public static final Color DOOR_OPEN_COL = Color.GREEN;
	public static final Color BLOCK_COL = new Color(108,168,217);	//pastel blue
	public static final Color BOULDER_COL = new Color(255, 160, 160);	//light red
	
	//Only necessary if we don't have sprites
	public static final Color LEVELBOX_OPEN_COL = Color.YELLOW;
	public static final Color LEVELBOX_CLOSED_COL = Color.GRAY;
		
//	public static final Color SPRING_ENTITY = new Color(0, 0, 0);
//	public static final Color PIN_ENTITY = new Color(0, 0, 0);
	
	/*Not necessary because of Sprites right?*/
	public static final Color STAR_COL = new Color(255, 255, 107);
	public static final Color PLAYER_COL = Color.BLUE;
	
	//Also any BORDER_COLORS or BORDER_WIDTHS or other design constants?
	
	/*Maybe we should also have like a width1, width2, etc... for each kind of
	 * object to keep it consistent. Like a thin, medium and thick widths that
	 * we stick to using?*/
}
