package miweinst.gravidog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import miweinst.engine.App;
import miweinst.engine.gfx.Text;
import miweinst.engine.shape.PolygonShape;
import miweinst.engine.shape.Shape;
import cs195n.Vec2f;
import cs195n.Vec2i;

public class MainMenuScreen extends GravidogScreen {
	
	private PolygonShape _background;
//	private PolygonShape _decor;
	
	private Text _textTitle1;
	private Text _textTitle2;
	private Text _textTitle3;
	
	private Text _textPlay;
	private Text _textReset;
	private Text _textQuit;
	private Shape _buttonPlay;
	private Shape _buttonReset;
	private Shape _buttonQuit;

	public MainMenuScreen(App a) {
		super(a);
		Vec2f d = a.getDimensions();
		
		Vec2f[] verts = {new Vec2f(d.x, d.y), new Vec2f(d.x, 0), 
				new Vec2f(0, 0), new Vec2f(0, d.y)};
		_background = new PolygonShape(verts);
		_background.setColor(new Color(185, 185, 185));
	
		//Buttons!
		float xLoc = d.x/10;
		_textPlay = new Text(_background, "Play", new Vec2f(xLoc, d.y*3/5));
		_textReset = new Text(_background, "Reset", new Vec2f(_textPlay.getLocation().x, _textPlay.getLocation().y + Text.getApproximateBounds(_textPlay).getDimensions().y*5));
		_textQuit = new Text(_background, "Quit", new Vec2f(_textReset.getLocation().x, _textReset.getLocation().y + Text.getApproximateBounds(_textReset).getDimensions().y*5));
		_textPlay.setVisible(true);
		_textReset.setVisible(true);
		_textQuit.setVisible(true);
		int buttonFontSize = 55;
		_textPlay.setFontSize(buttonFontSize);
		_textReset.setFontSize(buttonFontSize);
		_textQuit.setFontSize(buttonFontSize);

		_buttonPlay = Text.getApproximateBounds(_textPlay);
		_buttonReset = Text.getApproximateBounds(_textReset);
		_buttonQuit = Text.getApproximateBounds(_textQuit);
		_buttonPlay.setOutline(Color.BLACK, 1.5f);
		_buttonReset.setOutline(Color.BLACK, 1.5f);
		_buttonQuit.setOutline(Color.BLACK, 1.5f);
////^^^^		
		String s = "GRAVI-DOG";
		Vec2f titleLoc = new Vec2f(d.x/4f, d.y/3);
		_textTitle1 = new Text(_background, s, titleLoc);
		_textTitle2 = new Text(_background, s, _textTitle1.getLocation().plus(55, -55));
		_textTitle3 = new Text(_background, s, _textTitle1.getLocation().plus(-55, 55));
		_textTitle1.setVisible(true);
		_textTitle2.setVisible(true);
		_textTitle3.setVisible(true);
		//FONTS!
		_textTitle1.setFont(new Font("Walshes", Font.ROMAN_BASELINE, 100), false);
		_textTitle2.setFont(new Font("Walshes", Font.ROMAN_BASELINE, 100), false);
		_textTitle3.setFont(new Font("Walshes", Font.ROMAN_BASELINE, 100), false);
		//FONTS SIZE!
		_textTitle1.setFontSize(100);
		_textTitle2.setFontSize(100);
		_textTitle3.setFontSize(100);
		//FONTS COLORS!
		_textTitle3.setColor(new Color(47, 125, 147, 255));
		_textTitle1.setColor(new Color(47, 125, 147, 130));
		_textTitle2.setColor(new Color(47, 125, 147, 74));

/*		Vec2f[] decVerts = {new Vec2f(0, 0), new Vec2f(0, d.y), 
				new Vec2f(d.x, 0), new Vec2f(d.x, d.y)};	
		_decor = new PolygonShape(decVerts);
		_decor.setColor(Color.WHITE);*/
	}

/*	@Override
	public void onTick(long nanosSincePreviousTick) {
		//TODO 
	}*/

	@Override
	public void onDraw(Graphics2D g) {
		_background.draw(g);
		_textPlay.draw(g);
		_textReset.draw(g);
		_textQuit.draw(g);
//		_decor.draw(g);
		_textTitle3.draw(g);
		_textTitle1.draw(g);
		_textTitle2.draw(g);
		//Visualize bounding boxes used to watch for click
/*		_buttonPlay.draw(g);
		_buttonReset.draw(g);
		_buttonQuit.draw(g);*/
	}
/*	@Override
	public void onKeyPressed(KeyEvent e) {
		super.onKeyPressed(e);

	}*/

	@Override
	public void onMousePressed(MouseEvent e) {
		Vec2f loc = new Vec2f(e.getX(), e.getY());
		if (_buttonPlay.contains(loc))
			app.setScreen(new LevelMenuScreen(app));
		if (_buttonReset.contains(loc)) {
			LevelMenuScreen.clear();
			app.setScreen(new LevelMenuScreen(app));
		}
		if (_buttonQuit.contains(loc)) 
			System.exit(0);
	}
	@Override
	public void onResize(Vec2i newSize) {
		//TODO 	
	}
}
