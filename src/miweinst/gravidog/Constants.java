package miweinst.gravidog;

import java.awt.Color;

public class Constants {
	
	public static final Color BACKGROUND_COL = new Color(205, 205, 205); //light gray
	public static final Color GRAVITATIONAL_COL = Color.WHITE;
	public static final Color NONGRAVITATIONAL_COL = Color.GRAY;
	public static final Color DOOR_CLOSED_COL = Color.GRAY;
	public static final Color DOOR_OPEN_COL = Color.GREEN;
	public static final Color BLOCK_COL = new Color(108,168,217);	//pastel blue
	public static final Color BOULDER_COL = new Color(255, 160, 160);	//light red
	
	/*Not necessary because of Sprites right?*/
	public static final Color STAR_COL = new Color(255, 255, 107);
	public static final Color PLAYER_COL = Color.BLUE;
	
//	public static final Color SPRING_ENTITY = new Color(0, 0, 0);
//	public static final Color PIN_ENTITY = new Color(0, 0, 0);
	
	//Also any BORDER_COLORS or BORDER_WIDTHS or other design constants?
	
	/*Maybe we should also have like a width1, width2, etc... for each kind of
	 * object to keep it consistent. Like a thin, medium and thick widths that
	 * we stick to using?*/
}
