package miweinst.m;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import miweinst.engine.App;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.screen.Viewport;
import cs195n.Vec2f;
import cs195n.Vec2i;

public class PlayScreen extends MScreen {

	private App _app;
	private Viewport _viewport;
	private MWorld _gameWorld;
	private Vec2f _lastMouse;
	private float _cameraRadius;
	public PlayScreen(App a) {
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

		_viewport.setPortCenterInGameUnits(new Vec2f(0, 25));

		_gameWorld = new MWorld(a,_viewport);
		_viewport.setWorld(_gameWorld);
		Vec2f cameraDim = _viewport.getScreenSize();
		_cameraRadius = Math.min(cameraDim.x, cameraDim.y)/4;
	}

	@Override
	public void onTick(long nanosSincePreviousTick) {
		_gameWorld.onTick(nanosSincePreviousTick);
		//player panning
		
		Vec2f playerLocOnScreen = _viewport.gamePointToScreen(_gameWorld.getPlayer().getLocation());
		Vec2f playerOffsetFromCenter = playerLocOnScreen.minus(_viewport.getCenterOfScreen());
		Vec2f offsetNorm = playerOffsetFromCenter.normalized();
		Vec2f screenCameraOffset = offsetNorm.smult(_cameraRadius);
		if(playerOffsetFromCenter.mag() > screenCameraOffset.mag()) {	
			Vec2f panOffset = playerOffsetFromCenter.minus(screenCameraOffset);
			_viewport.panInPixels(panOffset);
		}
		
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
			_gameWorld.quitReset();
			System.exit(0);
		}
		if (e.getKeyChar() == 'r') _app.setScreen(new MenuScreen(_app));
		_gameWorld.onKeyPressed(e);
		
		/* testing for viewport */
		if(e.getKeyChar() == 'a') {
			_viewport.panInPixels(new Vec2f(-5f, 0f));
		}
		if(e.getKeyChar() == 'd') {
			_viewport.panInPixels(new Vec2f(5f, 0f));
		}
		if(e.getKeyChar() == 'w') {
			_viewport.panInPixels(new Vec2f(0f, 5f));
		}
		if(e.getKeyChar() == 's') {
			_viewport.panInPixels(new Vec2f(0f, -5f));
		}
		if(e.getKeyChar() == 'z') {
			_viewport.rotate(.05f);
		}
		if(e.getKeyChar() == 'x') {
			_viewport.rotate(-.05f);
		}
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
		_gameWorld.onKeyReleased(e);	
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		if (!e.isAltDown())
			_gameWorld.onMousePressed(e);		
		_lastMouse = new Vec2f(e.getX(), e.getY());
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onMouseDragged(MouseEvent e) {
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		_gameWorld.onMouseMoved(e);	
	}

	@Override
	public void onMouseWheelMoved(MouseWheelEvent e) {
		double rot = e.getWheelRotation();
		float zoom = rot < 0 ? 1.0f/1.1f : 1.1f;
		_viewport.zoom(zoom*_viewport.getScale());
	}

	@Override
	public void onResize(Vec2i newSize) {
		super.onResize(newSize);
	}
}
