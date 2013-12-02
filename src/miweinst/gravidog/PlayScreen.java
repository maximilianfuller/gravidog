package miweinst.gravidog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import miweinst.engine.App;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.screen.Viewport;
import cs195n.Vec2f;
import cs195n.Vec2i;

public class PlayScreen extends GravidogScreen {
	protected App app;
	private Viewport _viewport;
	private GravidogWorld _gameWorld;
	private Vec2f _lastMouse;
	private float _cameraRadius;
	public PlayScreen(App a) {
		super(a);
		app = a;
		//Bounds of Viewport window on screen, in pixels
		Vec2f windowDim = new Vec2f(a.getDimensions().x, a.getDimensions().y);
		AARectShape window = new AARectShape(new Vec2f(0, 0), windowDim);		

		//Instantiate world and viewport; pass reference both ways
		_viewport = new Viewport(window);
		//Semi-transparent background; overrides any properties from level editor 
		_viewport.getScreen().setOutline(new Color(15, 15, 15, 15), 4);	

		_viewport.setScreenLoc(new Vec2f(0, 0));
				
		_gameWorld = new GravidogWorld(a,_viewport);
		_viewport.setWorld(_gameWorld);

		this.setBackgroundColor(Color.BLACK);
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
		
		
		//player rotation
		Vec2f dir = _gameWorld.getPlayer().GRAVITY.normalized();
		float theta = (float) Math.atan(dir.y/dir.x); //offset from positive x axis
		if(dir.x < 0) {
			theta += Math.PI; //since the range of atan is -pi/2 to pi/2, here we get the full range
		}
		
		theta += Math.PI/2; //convert to offset from negative y axis

		_viewport.setTheta(theta);
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
		if (e.getKeyChar() == 'r') 
			app.setScreen(new PlayScreen(app));
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
		if (SwingUtilities.isRightMouseButton(e)) {
			if (_lastMouse != null) {
				float dx = _lastMouse.x - e.getX();
				float dy = _lastMouse.y - e.getY();
				_viewport.panInPixels(new Vec2f(dx, dy));
			}
		}
		_gameWorld.onMouseDragged(e);	
		_lastMouse = new Vec2f(e.getX(), e.getY());
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		_gameWorld.onMouseMoved(e);	
	}

	@Override
	public void onMouseWheelMoved(MouseWheelEvent e) {
		double rot = e.getPreciseWheelRotation();
        float zoom = rot < 0 ? 1.0f/1.1f : 1.1f;
        _viewport.zoom(zoom*_viewport.getScale());
	}

	@Override
	public void onResize(Vec2i newSize) {
		super.onResize(newSize);
	}
}
