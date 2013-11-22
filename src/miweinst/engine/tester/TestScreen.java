package miweinst.engine.tester;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import miweinst.engine.App;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.screen.Viewport;
import miweinst.m.MScreen;
import cs195n.Vec2f;
import cs195n.Vec2i;

public class TestScreen extends MScreen {
	
	private App _app;
	private Viewport _viewport;
	private TestWorld _testWorld;
	private Vec2f _lastMouse;
	public TestScreen(App a) {
		super(a);
		_app = a;

		this.setBackgroundTextColor(new Color(60, 60, 60, 45));
		//Bounds of Viewport window on screen, in pixels
		Vec2f windowDim = new Vec2f(a.getDimensions().x-50, a.getDimensions().y-75);
		AARectShape window = new AARectShape(new Vec2f(25, 25), windowDim);		

		//Instantiate world and viewport; pass reference both ways
		_viewport = new Viewport(window);
		
		//Semi-transparent background; overrides any properties from level editor 
		_viewport.getScreen().setOutline(new Color(15, 15, 15, 15), 4);	

		_viewport.setScreenInGameLoc(new Vec2f(0, 25));
				
		_testWorld = new TestWorld(a,_viewport);
		_viewport.setWorld(_testWorld);
		_viewport.setMathCoordinateSystem(true);
	}
	
	@Override
	public void onTick(long nanosSincePreviousTick) {
		_testWorld.onTick(nanosSincePreviousTick);
	}
	
	@Override
	public void onDraw(Graphics2D g) {
		super.onDraw(g);
		_viewport.draw(g);
	}

	@Override
	public void onKeyTyped(KeyEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
		if (e.getKeyChar() == 'q') {
			_testWorld.quitReset();
			System.exit(0);
		}
		if (e.getKeyChar() == 'r') _app.setScreen(new TestScreen(_app));
		_testWorld.onKeyPressed(e);
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
		_testWorld.onKeyReleased(e);	
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		if (!e.isAltDown())
			_testWorld.onMousePressed(e);		
		_lastMouse = new Vec2f(e.getX(), e.getY());
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onMouseDragged(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			if (_lastMouse != null) {
				float dx = _lastMouse.x - e.getX();
				float dy = _lastMouse.y - e.getY();
				_viewport.pan(dx, dy);
			}
		}
		_testWorld.onMouseDragged(e);	
		_lastMouse = new Vec2f(e.getX(), e.getY());
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		_testWorld.onMouseMoved(e);	
	}

	@Override
	public void onMouseWheelMoved(MouseWheelEvent e) {
		float newScale = _viewport.getScale() + e.getWheelRotation();
		if (newScale > 0) {
			_viewport.zoom(newScale);
		}
	}

	@Override
	public void onResize(Vec2i newSize) {
		super.onResize(newSize);
	}
}
