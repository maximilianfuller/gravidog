package miweinst.engine.beziercurve;

import java.util.Map;

import miweinst.engine.shape.AARectShape;
import miweinst.engine.shape.CircleShape;
import miweinst.engine.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;

public class BezierCurveEntity extends PhysicsEntity {
	
	private CubicBezierCurve _curve = new CubicBezierCurve();
	private boolean[] _points = {false, false, false, false};
	
/////
//	private BezierPath _path;

	public BezierCurveEntity(GameWorld world) {
		super(world);		
		this.setStatic(true);
		this.setInteractive(true);
		super.setShape(_curve);
////
		//If want to use path
/*		_path = new BezierPath();
		super.setShape(_path);*/
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
/*		_path.addPoint(s.getLocation());
		super.setShape(_path);*/
		
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
				_curve.start = s.getCentroid();
				_points[0] = true;
			}
			else {
				if (_curve.start.x > s.getCentroid().x) {
					_curve.end = _curve.start;
					_curve.start = s.getCentroid();
				}
				else {
					_curve.end = s.getCentroid();
				}
				_points[3] = true;
			}
		}
		_curve.updatePointArr();
	}

}
