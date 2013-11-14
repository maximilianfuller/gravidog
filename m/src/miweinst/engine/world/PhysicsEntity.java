package miweinst.engine.world;

import java.util.Map;

import miweinst.engine.collisiondetection.CollisionInfo;
import miweinst.engine.entityIO.Input;
import miweinst.engine.entityIO.Output;
import miweinst.engine.gfx.shape.Shape;
import cs195n.Vec2f;

public class PhysicsEntity extends MovingEntity {
	public final String string = "PhysicsEntity";

	//Gravity acts on all objects of GameWorld equally
	public static float GRAVITY = -75f;
	
	//Self-explanatory attributes of physical object
	private float _mass;
	private Vec2f _vel, _pos;
	private Vec2f _force, _impulse;
	private float _restitution;
	
	//Whether unaffected by impulses/forces
	private boolean _isStatic;
	//Can decorate PhysicsEntity when iterating to avoid repeats
	private boolean _isVisited;
	//Whether has any collision response
	private boolean _isInteractive;
	//Most recent MTV
	private Vec2f _lastMTV;
	
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
		_pos = this.getLocation();
		_mass = 1;
		_restitution = .8f;		
		_force = new Vec2f(0, 0);
		_impulse = new Vec2f(0, 0);		
		_isStatic = false;		
		_isVisited = false;		
		_isInteractive = true;
		//Moves on set dx/dy without having to call move() manually
		this.setFreeMoving(true);
	}
	
	 /*Sets gravitational force applied to Entity
	  * on every tick. Modifies static var! Gravity force
	  * should act on every PhysicsEntity the same.*/
	public static void setGravity(float g) {
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
		_pos = this.getLocation();
		//Applies gravitational force down as Y-component
		Vec2f g = new Vec2f(0, GRAVITY);		
		this.applyForce(g.smult(_mass));						
		//Update vel, pos; reset force, impulse
		this.symplecticUpdate(nanosSincePreviousTick);
		this.setLocation(_pos);
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
		_pos = _pos.plus(_vel.smult(time));
		//Reset force and impulse
		_force = _impulse = new Vec2f(0, 0);
	}
	
	/*Accumulates force. Called to achieve
	 * acceleration over time.
	 * (ex: start moving)*/
	public void applyForce(Vec2f f) {
		if (_isStatic == false) {
			_force = _force.plus(f);
		}
	}
	/*Accumulates impulse. Called for 
	 * instantaneous acceleration. 
	 * (ex: jumping, collision response)*/
	public void applyImpulse(Vec2f i) {
		if (_isStatic == false) {
			_impulse = _impulse.plus(i);
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
		if (Math.abs(_vel.x) < Math.abs(vx)) {		
			Vec2f F = new Vec2f(dV*2.5f, 0);
			this.applyForce(F);
		}
	}
	public void goalVelocityY(float vy) {		
		float dV = vy - _vel.y;
		if (Math.abs(_vel.y) < Math.abs(vy)) {		
			Vec2f F = new Vec2f(0, dV*2.5f);
			this.applyForce(F);
		}
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
		_pos = pos;
	}
	/*Accessor for PhysicsEntity position.
	 *For mutator, call getLocation() in super.*/
	public Vec2f getPosition() {
		return _pos;
	}
	
	/*Stores MTV of last collision with this Entity. 
	 * Null if no collision, updated on every tick..*/
	public Vec2f getLastMTV() {
		return _lastMTV;
	}
	public void setLastMTV(Vec2f mtv) {
		_lastMTV = mtv;
	}

	/* Adds collision response to the collision detection of
	 * MovingEntity. Response based on attributes of
	 * PhysicsEntity. 
	 * This does not override super's method, b/c takes
	 * PhysicsEntity. Super's method called if MovingEntity
	 * of different subclass is passed in, w/o collision response.*/
	public boolean collides(PhysicsEntity s) {
		boolean collision = super.collides(s);	
		if (_isInteractive && s.isInteractive())
			if (collision) 
				this.collisionResponse(s);	
		return collision;
	}
	
	/* Handles response if collision between entities is 
	 * detected. Moves each Entity away from each other
	 * by mtv/2, according to their CollisionInfo object.*/
	public void collisionResponse(PhysicsEntity s) {
		//Get CollisionInfo information cache, s
		CollisionInfo otherData = s.getShape().getCollisionInfo();
		//containing obj is 'other' b/c double dispatch
		CollisionInfo thisData = this.getShape().getCollisionInfo();
		
		if (otherData != null && thisData != null) {

			//Get MTVs and locations for each Entity
			Vec2f otherMTV = otherData.getMTV();	
			Vec2f thisMTV = thisData.getMTV();			
			Vec2f otherNewLoc = s.getLocation();
			Vec2f thisNewLoc = this.getLocation();		
			//If other shape is static, move full MTV. Else each move MTV/2
			if (!s.isStatic()) {
				int mult = 2;
				if(this.isStatic())
					mult = 1;
				otherNewLoc = s.getLocation().plus(otherMTV.sdiv(mult));
			}
			if (!this.isStatic()) {
				int mult = 2;
				if(s.isStatic())
					mult = 1;
				thisNewLoc = this.getLocation().plus(thisMTV.sdiv(mult));
			}		
			Vec2f[] imps = calculateImpulse(s);

			s.setLocation(otherNewLoc);
			s.applyImpulse(imps[0]);
			this.setLocation(thisNewLoc);
			this.applyImpulse(imps[1]);
			
			//Updates reference to most recent MTV
			s.setLastMTV(otherMTV);
			this.setLastMTV(thisMTV);
		}
		//Void condition because collisionResponse only called if collision detected
		else {
			if (otherData == null) {
				s.setLastMTV(null);
			}
			if (thisData == null) {
				this.setLastMTV(null);
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
		Vec2f i_a = (u_b.minus(u_a)).smult((m_a*m_b*(1+cor)) / (m_a + m_b));
		Vec2f i_b = (u_a.minus(u_b)).smult((m_a*m_b*(1+cor)) / (m_a + m_b));		
		if (this.isStatic()){
			i_a = u_b.minus(u_a).smult(m_b*(1+cor));
			i_b = u_a.minus(u_b).smult(m_b*(1+cor));
		}
		if (other.isStatic()) {
			i_a = u_b.minus(u_a).smult(m_a*(1+cor));
			i_b = u_a.minus(u_b).smult(m_a*(1+cor));
		}
		imps[0] = i_b;
		imps[1] = i_a;
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
