package miweinst.gravidog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import miweinst.engine.App;
import miweinst.engine.gfx.Text;
import miweinst.engine.shape.PolygonShape;
import cs195n.Vec2f;
import cs195n.Vec2i;

public class MainMenuScreen extends GravidogScreen {
	
	private PolygonShape _background;
//	private PolygonShape _decor;
	
	private Text _textOne;
	private Text _textTwo;
	private Text _textThree;

	public MainMenuScreen(App a) {
		super(a);
		Vec2f d = a.getDimensions();
		
		Vec2f[] verts = {new Vec2f(d.x, d.y), new Vec2f(d.x, 0), 
				new Vec2f(0, 0), new Vec2f(0, d.y)};
		_background = new PolygonShape(verts);
		_background.setColor(new Color(185, 185, 185));

		String s = "GRAVI-DOG";
		_textOne = new Text(_background, s, new Vec2f(d.x/4f, d.y*3/5));
		_textTwo = new Text(_background, s, _textOne.getLocation().plus(55, -55));
		_textThree = new Text(_background, s, _textOne.getLocation().plus(-55, 55));
		_textOne.setVisible(true);
		_textTwo.setVisible(true);
		_textThree.setVisible(true);
		//FONTS!
		_textOne.setFont(new Font("Walshes", Font.ROMAN_BASELINE, 100), false);
		_textTwo.setFont(new Font("Walshes", Font.ROMAN_BASELINE, 100), false);
		_textThree.setFont(new Font("Walshes", Font.ROMAN_BASELINE, 100), false);
		//FONTS SIZE!
		_textOne.setFontSize(100);
		_textTwo.setFontSize(100);
		_textThree.setFontSize(100);
		//FONTS COLORS!
		_textThree.setColor(new Color(47, 125, 147, 255));
		_textOne.setColor(new Color(47, 125, 147, 130));
		_textTwo.setColor(new Color(47, 125, 147, 74));

/*		Vec2f[] decVerts = {new Vec2f(0, 0), new Vec2f(0, d.y), 
				new Vec2f(d.x, 0), new Vec2f(d.x, d.y)};	
		_decor = new PolygonShape(decVerts);
		_decor.setColor(Color.WHITE);*/
	}

	@Override
	public void onTick(long nanosSincePreviousTick) {
		//TODO 
	}

	@Override
	public void onDraw(Graphics2D g) {
		_background.draw(g);
//		_decor.draw(g);
		_textThree.draw(g);
		_textOne.draw(g);
		_textTwo.draw(g);
	}
	@Override
	public void onKeyPressed(KeyEvent e) {
		super.onKeyPressed(e);
		//TODO
	}

	@Override
	public void onMousePressed(MouseEvent e) {
//		app.setScreen(new PlayScreen(app));
		app.setScreen(new LevelMenuScreen(app));
	}
	@Override
	public void onResize(Vec2i newSize) {
		//TODO 	
	}
}
