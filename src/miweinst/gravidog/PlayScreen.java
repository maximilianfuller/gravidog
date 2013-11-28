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
