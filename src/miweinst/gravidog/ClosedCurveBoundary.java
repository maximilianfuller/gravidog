package miweinst.gravidog;

import java.util.ArrayList;
import java.util.Map;

import miweinst.engine.beziercurve.BezierPath;
import miweinst.engine.gfx.shape.Shape;
import miweinst.engine.world.GameWorld;
import miweinst.engine.world.PhysicsEntity;
import cs195n.Vec2f;

public class ClosedCurveBoundary extends PhysicsEntity {
	
	private BezierPath _path;
	private ArrayList<Vec2f> _knots;
	private ArrayList<Vec2f> _actrls;
	private ArrayList<Vec2f> _bctrls;
	
	public ClosedCurveBoundary(GameWorld world) {
		super(world);
		_knots = new ArrayList<Vec2f>();
		_path = new BezierPath();
		init();
	}
	public ClosedCurveBoundary(GameWorld world, ArrayList<Vec2f> points) {
		super(world);
		_knots = points;
		_path = BezierPath.generateClosedCurve(_knots.toArray(new Vec2f[_knots.size()]), _actrls, _bctrls);			
		init();
	}
	private void init() {
		_actrls = new ArrayList<Vec2f>();
		_bctrls = new ArrayList<Vec2f>();
		super.setShape(_path);		
		this.setMass(10000);
		this.setStatic(true);
		this.setVisible(true);
	}
	
	@Override
	public void setShape(Shape s) {
		_actrls.clear();
		_bctrls.clear();
		_knots.add(s.getLocation());
		_path = BezierPath.generateClosedCurve(_knots.toArray(new Vec2f[_knots.size()]), _actrls, _bctrls);		
		super.setShape(_path);
	}
	
	@Override
	public void setProperties(Map<String, String> props) {
		super.setProperties(props);
	}
}
