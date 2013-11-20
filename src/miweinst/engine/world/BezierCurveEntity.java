package miweinst.engine.world;

import java.util.Map;

import miweinst.engine.beziercurve.CubicBezierCurve;
import miweinst.engine.gfx.shape.AARectShape;
import miweinst.engine.gfx.shape.CircleShape;
import miweinst.engine.gfx.shape.Shape;

public class BezierCurveEntity extends PhysicsEntity {
	
	private CubicBezierCurve _curve = new CubicBezierCurve();
	private boolean[] _points = {false, false, false, false};

	public BezierCurveEntity(GameWorld world) {
		super(world);		
		this.setStatic(true);
		super.setShape(_curve);
////		
//		this.setRotatable(false);
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
		super.setShape(_curve);
		if (s instanceof AARectShape) {
			if (!_points[1]) {
				_curve.ctrl_one = s.getLocation();
				_points[1] = true;
			}
			else if (!_points[2]) {
				_curve.ctrl_two = s.getLocation();
				_points[2] = true;
			}
		}
		if (s instanceof CircleShape) {
			if (!_points[0]) {
				_curve.start = s.getLocation();
				_points[0] = true;
			}
			else {
				if (_curve.start.x > s.getLocation().x) {
					_curve.end = _curve.start;
					_curve.start = s.getLocation();
				}
				else {
					_curve.end = s.getLocation();
				}
				_points[3] = true;
			}
		}
		_curve.updatePointArr();
	}
}
