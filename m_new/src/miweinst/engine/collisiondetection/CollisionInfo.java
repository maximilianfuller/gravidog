package miweinst.engine.collisiondetection;

import cs195n.Vec2f;
import miweinst.engine.gfx.shape.Shape;

public class CollisionInfo {
	private final Vec2f mtv;
	private final Shape curr;
	private final Shape other;

	public CollisionInfo(Shape thisShape, Shape otherShape, Vec2f mtvec) {
		mtv = mtvec;
		curr = thisShape;
		other = otherShape;
	}

	public Shape getShape() {
		return curr;
	}
	public Shape getOther() {
		return other;
	}
	public Vec2f getMTV() {
		return mtv;
	}
}
