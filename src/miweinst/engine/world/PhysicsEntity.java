package miweinst.engine.world;

import java.util.ArrayList;
import java.util.Map;

import miweinst.engine.collisiondetection.PhysicsCollisionInfo;
import miweinst.engine.collisiondetection.ShapeCollisionInfo;
import miweinst.engine.entityIO.Input;
import miweinst.engine.entityIO.Output;
import miweinst.engine.shape.Shape;
import cs195n.Vec2f;

public class PhysicsEntity extends MovingEntity {
	public final String string = "PhysicsEntity";

	//Gravity acts on all objects of GameWorld equally
	public static Vec2f GRAVITY = new Vec2f(0, -75f);

	//Self-explanatory attributes of physical object
	private float _mass;
	private Vec2f _vel;
	private Vec2f _force, _impulse;
	private float _restitution;
	private float _angularImpulse, _angularForce;	//radians
	private float _angularVel;

	//Most recent Collision, MTV and other PhysicsEntity
	private ArrayList<PhysicsCollisionInfo> _collisionInfo;

	private boolean _isStatic;
	private boolean _isVisited;
	private boolean _isInteractive;
	private boolean _isRotatable;
	private boolean _isGravitational;

	//Input to change whether interactive/visible; for traps, dynamic mechanics, etc...
	public Input doDisappear = new Input() 
	{
		public void run(Map<String, String> args) {
			if (args.containsKey("interactive")) {
				setInteractive(Boolean.parseBoolean(args.get("interactive")));
			}
			if (args.containsKey("visible")) {
				setVisible(Boolean.parseBoolean(args.get("visible")));
			}
		}
	};

	public PhysicsEntity(GameWorld world) {
		super(world);		
		_vel = new Vec2f(0, 0);
		super.setDx(_vel.x);
		super.setDy(_vel.y);		
		GRAVITY = new Vec2f(0, -75f);
		_mass = 1;
		_restitution = 0f;		
		_force = new Vec2f(0, 0);
		_impulse = new Vec2f(0, 0);		
		_isStatic = false;		
		_isVisited = false;		
		_isInteractive = true;
		_isRotatable = true;
		_isGravitational = true;
		//Moves on set dx/dy without having to call move() manually
		this.setFreeMoving(true);
		
		_collisionInfo = new ArrayList<PhysicsCollisionInfo>();
	}

	/*Sets gravitational force applied to Entity
	 * on every tick. Modifies static var! Gravity force
	 * should act on every PhysicsEntity the same.*/
	public static void setGravity(Vec2f g) {
		GRAVITY = g;
	}

	/*Forward to protected method in 
	 * superclass so that shape of
	 * PhysicsEntity is public. Helpful
	 * for collision detection, so
	 * PhysicsEntity should have it
	 * public while MovingEntity still protected.*/
	public Shape getShape() {
		return super.getShape();
	}
	public void setShape(Shape s) {
		super.setShape(s);
	}

	/*Sets/Gets mass of this Entity.*/
	public float getMass() {
		return _mass;
	}
	public void setMass(float m) {
		_mass = m;
	}
	public Vec2f getCentroid() {
		return super.getShape().getCentroid();
	}
	public float getMomentOfInertia(float mass) {
		return super.getShape().getMomentOfInertia(mass);
	}

	public float getAngle() {
		return super.getShape().getAngle();
	}
	public void setAngle(float angle) {
		super.getShape().setAngle(angle);
	}

	/*Sets/Gets coefficient of restitution.
	 * Range: 1 --> 0, elastic --> inelastic*/
	public float getRestitution() {
		return _restitution;
	}
	public void setRestitution(float cor) {
		_restitution = cor;
	}

	@Override
	public void onTick(long nanosSincePreviousTick) {
		//In MovingEntity, moves by delta(x, y)
		super.onTick(nanosSincePreviousTick);
		//Update reference to current location
		//		_pos = this.getLocation();
		//Applies gravitational force down as Y-component
		this.applyForce(GRAVITY.smult(_mass), getShape().getCentroid());						
		//Update vel, pos; reset force, impulse
		this.symplecticUpdate(nanosSincePreviousTick);
//		this.setLocation(_pos);		
	}

	/* Update Position and Velocity in symplectic order. Use
	 * force/impulse accumulated between tick, then reset
	 * both for next frame.*/
	public void symplecticUpdate(long nanos) {
		//Turn into seconds
		float time = nanos/1000000000.0f;		
		//vel = vel + t*force/m + impulse/m 	(= vel + acc*time)
		_vel = _vel.plus(_force.smult(time).sdiv(_mass).plus(_impulse.sdiv(_mass)));	
		//pos = pos + t*vel		 (= pos + vel*time)
		setLocation(getLocation().plus(_vel.smult(time)));

		//Update rotational stuff
		_angularVel += _angularForce*(time/getMomentOfInertia(_mass));
		_angularVel += _angularImpulse/getMomentOfInertia(_mass);
		//_angle += _angularVel*secondsElapsed;
		getShape().setAngle(getShape().getAngle() + _angularVel*time);

		//Reset force and impulse
		_force = new Vec2f(0f, 0f);
		_impulse = new Vec2f(0f, 0f);
		_angularForce = 0f;
		_angularImpulse = 0f;
	}

	/*Accumulates force. Called to achieve
	 * acceleration over time.
	 * (ex: start moving)*/
	public void applyForce(Vec2f f, Vec2f point) {
		if (_isStatic == false) {
			Vec2f r = point.minus(getCentroid());
			_force = _force.plus(f);
			if(_isRotatable) {
				_angularForce += r.cross(_force);
			}
		}
	}
	/*Accumulates impulse. Called for 
	 * instantaneous acceleration. 
	 * (ex: jumping, collision response)*/
	public void applyImpulse(Vec2f i, Vec2f point) {
		if (_isStatic == false) {
			Vec2f r = point.minus(getShape().getCentroid());
			_impulse = _impulse.plus(i);
			if(_isRotatable) {
				_angularImpulse += r.cross(_impulse);
			}
		}
	}

	/*Applies force until goal velocity is reached.
	 * Force is proportional to difference between 
	 * current x-vel and goal x-vel. So force decreases
	 * as PhysicsEntity gains velocity.
	 * Separate mutators for X- and Y-component of velocity
	 * instead of wrapping in a Vec2f object because
	 * x, y are almost never set together.*/
	public void goalVelocityX(float vx) {		
		float dV = vx - _vel.x;
		Vec2f F = new Vec2f(dV*2.5f, 0);
		this.applyForce(F, getCentroid());
	}
	public void goalVelocityY(float vy) {		
		float dV = vy - _vel.y;
		Vec2f F = new Vec2f(0, dV*2.5f);
		this.applyForce(F, getCentroid());
	}
	
	public void goalVelocity(Vec2f gv) {
		Vec2f dV = gv.minus(_vel);
		Vec2f force = dV.smult(2.5f);
		this.applyForce(force, getCentroid());
	}

	/*Bypass force and impulse to mutate velocity directly.*/
	public void setVelocity(Vec2f vel) {
		_vel = vel;
	}
	/*Accessor for current velocity.*/
	public Vec2f getVelocity() {
		return _vel;
	}

	/*Partial override to update PhysicsEntity
	 * _pos field. So _pos stays up to date.*/
	@Override
	public void setLocation(Vec2f pos) {
		super.setLocation(pos);
		//		_pos = pos;
	}
	/*	Accessor for PhysicsEntity position.
	 *For mutator, call getLocation() in super.
	public Vec2f getPosition() {
		return _pos;
	}*/

	/*Stores MTV of last collision with this Entity. 
	 * Null if no collision, updated on every tick..*/
	public ArrayList<PhysicsCollisionInfo> getCollisionInfo() {
		return _collisionInfo;
	}
	public void setCollisionInfo(PhysicsCollisionInfo info) {
		_collisionInfo.add(info);
	}

	/* Adds collision response to the collision detection of
	 * MovingEntity. Response based on attributes of
	 * PhysicsEntity. 
	 * This does not override super's method, b/c takes
	 * PhysicsEntity. Super's method called if MovingEntity
	 * of different subclass is passed in, w/o collision response.*/
	public boolean collides(PhysicsEntity other) {
		boolean collision = super.collides(other);  
		if (_isInteractive && other.isInteractive()) {
			this.collisionResponse(other);
		}
		return collision;
	}

	/* Handles response if collision between entities is 
	 * detected. Moves each Entity away from each other
	 * by mtv/2, according to their ShapeCollisionInfo object.*/
	public void collisionResponse(PhysicsEntity other) {
		//Get ShapeCollisionInfo information cache, s
		ShapeCollisionInfo otherData = other.getShape().getCollisionInfo();
		//containing obj is 'other' b/c double dispatch
		ShapeCollisionInfo thisData = this.getShape().getCollisionInfo();

		//Avoid null pointer by checking POI exists (there is that weird penetration case)
		if (otherData != null && thisData != null && other.getShape().poi(getShape()) != null) {
			//Get MTVs and locations for each Entity
			Vec2f otherMTV = otherData.getMTV();	
			Vec2f thisMTV = thisData.getMTV();			
			Vec2f otherNewLoc = other.getLocation();
			Vec2f thisNewLoc = this.getLocation();				
			Vec2f poi = this.getShape().poi(other.getShape());

			//Point of intersection should be same when called from either direction
			assert poi == other.getShape().poi(this.getShape());

			if (!other.isStatic()) {
				//s new location
				float mult = _mass/(_mass+other.getMass());
				if(this.isStatic())
					mult = 1;
				otherNewLoc = other.getLocation().plus(otherMTV.smult(mult));
			}
			if (!this.isStatic()) {
				float mult = other.getMass()/(_mass+other.getMass());
				if(other.isStatic())
					mult = 1;
				thisNewLoc = this.getLocation().plus(thisMTV.smult(mult));
			}		
			//1 Calculate impulse before translating
			Vec2f[] imps = calculateImpulse(other);		
			//2 Translate it out
			other.setLocation(otherNewLoc);
			this.setLocation(thisNewLoc);

			//3 Then set the impulse
			other.applyImpulse(imps[0], poi);
			this.applyImpulse(imps[1], poi);
			
			//Updates reference to most recent MTV
			other.setCollisionInfo(new PhysicsCollisionInfo(otherMTV, this));
			this.setCollisionInfo(new PhysicsCollisionInfo(thisMTV, other));
		}
		//Void condition because collisionResponse only called if collision detected
		else {
			if (otherData == null) {
				other.setCollisionInfo(null);
			}
			if (thisData == null) {
				this.setCollisionInfo(null);
			}
		}
	}

	/* Calculate impulse based on Coefficient of Restitution. Finds
	 * correct impulse for collision response between two shapes in 
	 * collisionResponse method.*/
	public Vec2f[] calculateImpulse(PhysicsEntity other) {
		//Impulse array, equal but opposite: [impulseA, impulseB]
		Vec2f[] imps = new Vec2f[2];
		float cor = (float) Math.sqrt(this.getRestitution()*other.getRestitution());

		float m_a = this.getMass();
		float m_b = other.getMass();		
		Vec2f u_a = this.getVelocity().projectOnto(this.getShape().getCollisionInfo().getMTV());
		Vec2f u_b = other.getVelocity().projectOnto(other.getShape().getCollisionInfo().getMTV());	

		Vec2f poi = this.getShape().poi(other.getShape());
		Vec2f n = this.getShape().getCollisionInfo().getMTV().normalized();

		Vec2f numerator = (u_a.minus(u_b)).smult((-1f) * (1 + cor));
		float denominator = 1f/m_a + 1f/m_b;
		//Only non-static entities have non-null getCentroid() and getMomentOfInertia() [i.e. BezierCurveEntity does not]
		if (!this.isStatic()) {
			Vec2f r1Perp = getCentroid().minus(poi).getNormal().normalized();
			denominator += (r1Perp.dot(n) * r1Perp.dot(n)) / this.getMomentOfInertia(_mass);;
		}
		if (!other.isStatic()) {
			Vec2f r2Perp = other.getCentroid().minus(poi).getNormal().normalized();
			denominator += (r2Perp.dot(n) * r2Perp.dot(n)) / other.getMomentOfInertia(other.getMass());;
		}
		imps[1] = (numerator.sdiv(denominator));
		imps[0] = imps[1].invert();

		//Old, non-rotating collision response (flat impulse calculation)
		/*		if (!this.isRotatable() && !other.isRotatable()) {
			//Don't delete yet; impulse calculation for non-rotating entities
			//	Vec2f i_a = (u_b.minus(u_a)).smult((m_a*m_b*(1+cor)) / (m_a + m_b));
			imps[0] = (u_a.minus(u_b)).smult((m_a*m_b*(1+cor)) / (m_a + m_b));		
			if (this.isStatic()) {
				imps[1] = u_b.minus(u_a).smult(m_b*(1+cor));
				imps[0] = u_a.minus(u_b).smult(m_b*(1+cor));
			}
			if (other.isStatic()) {
				imps[1] = u_b.minus(u_a).smult(m_a*(1+cor));
				imps[0] = u_a.minus(u_b).smult(m_a*(1+cor));
			}
		}*/

		return imps;
	}

	//Boolean accessors/mutators

	/*Overrides any force or impulse.*/
	public boolean isStatic() {
		return _isStatic;
	}
	public void setStatic(boolean s) {
		_isStatic = s;
	}	
	/* Basic decorator for many applications,
	 * notably for more efficient collision
	 * detection by avoiding double-checking.*/
	public boolean isVisited() {
		return _isVisited;
	}
	public void setVisited(boolean visited) {
		_isVisited = visited;
	}	
	/* Sets/Gets _isInteractive boolean. If false,
	 * this PhysicsEntity has no collision response.*/
	public boolean isInteractive() {
		return _isInteractive;
	}
	public void setInteractive(boolean r) {
		_isInteractive = r;
	}	
	public void setRotatable(boolean r) {
		_isRotatable = r;
	}
	public boolean isGravitational() {
		return _isGravitational;
	}
	public void setGravitational(boolean g) {
		_isGravitational = g;
	}

	/* Properties of PhysicsEntity, mapped from Strings to
	 * values as Strings.*/
	@Override
	public void setProperties(Map<String, String> props) {
		//restitution
		if (props.containsKey("restitution")) 
			this.setRestitution(Float.parseFloat(props.get("restitution")));
		//mass
		if (props.containsKey("mass")) 
			this.setMass(Float.parseFloat(props.get("mass")));						
		//static
		if (props.containsKey("static")) 
			this.setStatic(Boolean.parseBoolean(props.get("static")));						
		//interactive
		if (props.containsKey("interactive")) 
			this.setInteractive(Boolean.parseBoolean(props.get("interactive")));						
		//visible
		if (props.containsKey("visible")) 
			this.setVisible(Boolean.parseBoolean(props.get("visible")));	
		//changes gravity
		if (props.containsKey("gravitational")) 
			this.setGravitational(Boolean.parseBoolean(props.get("gravitational")));
	}

	/*Returns the IO class mapped to a particular String,
	 * so accessible by level editor reader.*/
	public Input getInput(String s) {
		if (new String("doDisappear").equals(s)) {
			return doDisappear;
		}
		System.out.println("No input found (PhysicsEntity.getInputOf)");
		return null;
	}
	public Output getOutput(String o) {
		return null;
	}
}
