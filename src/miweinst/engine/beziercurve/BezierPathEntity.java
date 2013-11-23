package miweinst.engine.beziercurve;

import java.util.Map;

import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;

public class BezierPathEntity extends PhysicsEntity {

	private BezierPath _path;

	public BezierPathEntity(GameWorld world) {
		super(world);		
		this.setStatic(true);
		this.setInteractive(true);

		//If want to use path
		_path = new BezierPath();
		super.setShape(_path);
	}

	@Override
	public void setProperties(Map<String, String> props) {
		super.setProperties(props);
	}	

	/*AAB are control points, and circles are endpoints. The
	 * circle with the lower x-val (to the left) is the start
	 * endpoint, and the other circle is the end endpoint.*/
	@Override
	public void setShape(Shape s) {
		//If want to use path
		_path.addPoint(s.getLocation());
		super.setShape(_path);		
	}
}
