package miweinst.gravidog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import miweinst.engine.gfx.sprite.Resources;
import miweinst.engine.gfx.sprite.SpriteLoader;
import cs195n.Vec2i;

public class GravidogResources extends Resources {

	//This is the single instance of TacResources in Tac
		//other classes reference it through static method get()
	private static GravidogResources store = new GravidogResources();
	
	private HashMap<String, BufferedImage[]> _cache;	

	public GravidogResources() {		
		//Stores arrays of sprite frames in animations, accessed by string key
		_cache = new HashMap<String, BufferedImage[]>();

		//Load first sheet	
		
		//Loads single sprite, level_one preview frame
		File f = new File("src/miweinst/resources/frame_one.jpg");
		SpriteLoader levelLoader = new SpriteLoader(f, new Vec2i(0, 0), 0, new Vec2i(1, 1), 0);
		
		BufferedImage[][] levelOut = levelLoader.getSprites();
		
		BufferedImage[] levelOneFrame = levelOut[0];
		_cache.put("frame_level_one", levelOneFrame);
				
		//Sets the cache of Resources superclass so that 
		super.setCache(_cache);
	}
	
	/**
	 * Returns the only instance of TacResources,
	 * overwriting contract from superclass Resources
	 * @return
	 */
	public static GravidogResources get() {
		return store;
	}
	
	/**
	 * Returns the Sprite stored in the HashMap at 
	 * the specified key. The key is a string referencing
	 * that specific Sprite. Since the constructor
	 * set the cache of the superclass, that generic
	 * definition still holds for this subclass. This
	 * is pretty much a visual to remind myself that
	 * TacResources inherits this method.
	 * @param key
	 * @return
	 */
	public BufferedImage[] getValue(String key) {
		return super.getValue(key);
	}
	
	/**
	 * Check if there is a resource stored at the
	 * specified key in the cache.
	 * @param key
	 * @return
	 */
	public boolean contains(String key) {
		return _cache.containsKey(key);
	}
}
