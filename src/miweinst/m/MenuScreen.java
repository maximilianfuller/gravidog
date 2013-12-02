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
import miweinst.engine.shape.AARectShape;

public class MenuScreen extends MScreen {
	private App _app;
	private AARectShape _background;
	private Text _botText;
	private int _blinkTimer;

	public MenuScreen(App a) {
		super(a);
		_app = a;
		_background = super.getBackground();
		
		Font mFont = new Font("VDub", Font.CENTER_BASELINE, 12);
		String s = "Press enter";
		Vec2f superLoc = super.getBackgroundTextLocation();
		_botText = new Text(_background, s, new Vec2f(superLoc.x, _background.getHeight()-_background.getHeight()/20));
		_botText.setColor(new Color(20, 20, 20));
		_botText.setFont(mFont, false);
		_botText.setFontSize(65);
		_botText.centerTextHorizontal();
		
////SYSTEM FONTS
/*		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (int i=0; i<fonts.length; i++) {
			System.out.println(fonts[i]);
		}*/	
	}

	@Override
	public void onTick(long nanosSincePreviousTick) {
		//Should hold on visible flick little longer than invisible flick
		if (_botText.isVisible() == false) _blinkTimer += nanosSincePreviousTick/1000000;
		else _blinkTimer += nanosSincePreviousTick/2000500;
		//Ternary op toggles visibility of font at bottom
		if (_blinkTimer > 150) {
			_botText.setVisible(_botText.isVisible()? false: true);
			_blinkTimer = 0;
		}
	}

	@Override
	public void onDraw(Graphics2D g) {
		super.onDraw(g);
		_botText.draw(g);
	}

	@Override
	public void onKeyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
		super.onKeyPressed(e);
		if (e.getKeyCode() == 10) {
			_app.setScreen(new PlayScreen(_app));
		}
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
		super.onResize(newSize);
		_botText.setLocation(new Vec2f(_botText.getLocation().x, _background.getHeight()-_background.getHeight()/20));
		_botText.centerTextHorizontal();
	}

}
