package miweinst.m;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import cs195n.Vec2f;
import cs195n.Vec2i;
import miweinst.engine.App;
import miweinst.engine.gfx.Text;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.screen.Screen;

public class MScreen extends Screen {
	private App _app;
	private AARectShape _background;
	private Text _mText;

	public MScreen(App a) {
		super(a);
		_app = a;
		_background = new AARectShape(new Vec2f(0, 0), a.getDimensions());
		_background.setColor(new Color(108, 123, 139));
		
		String s = "M";
		_mText = new Text(_background, s, new Vec2f(_background.getWidth()/4, _background.getHeight()*3/4));
		_mText.setVisible(true);
		Font mFont = new Font("VDub", Font.CENTER_BASELINE, 12);
		_mText.setColor(new Color(20, 20, 20));
		_mText.setFont(mFont, false);
		_mText.setFontSize(400);
		_mText.centerTextHorizontal();
		
////SYSTEM FONTS
/*		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (int i=0; i<fonts.length; i++) {
			System.out.println(fonts[i]);
		}*/	
	}
	
	/*Sets bgcolor of all screens in M*/
	public void setBackgroundColor(Color col) {
		_background.setColor(col);
	}
	
	/*Sets color of the big "M" in the middle of screen, for subclasses.*/
	protected void setBackgroundTextColor(Color col) {
		_mText.setColor(col);
	}
	
	protected Vec2f getBackgroundTextLocation() {
		return _mText.getLocation();
	}
	
	/*Returns Rectangle forming background of screen.*/
	protected AARectShape getBackground() {
		return _background;
	}

	@Override
	public void onTick(long nanosSincePreviousTick) {
	}

	@Override
	public void onDraw(Graphics2D g) {
		_background.draw(g);
		_mText.draw(g);
	}

	@Override
	public void onKeyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
		if (e.getKeyChar() == 'q') System.exit(0);
		if (e.getKeyChar() == 'r') _app.setScreen(new MenuScreen(_app));
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResize(Vec2i newSize) {
		_background.setDimensions(new Vec2f(newSize));
		_mText.setLocation(new Vec2f(_background.getWidth()/4, _background.getHeight()*3/4));
		_mText.centerTextHorizontal();
	}

}
