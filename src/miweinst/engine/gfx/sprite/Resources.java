package miweinst.engine.gfx.sprite;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * This class is only instantiated once in the project because
 * it stores all the loaded resources, like Sprites, that can then be accessed
 * by the other classes. It works as like a library, so that the
 * sprites only have to be loaded once.
 * 
 * It is abstract because it does not store the specific data, it only
 * builds the architecture of a subclass that will store the specific data.
 * Since this class is generic, it cannot instantiate a SpriteLoader because
 * it does not know the files, or the necessary metadata to read the sprites 
 * from the sprite sheet. This mostly acts as a superclass for the game
 * Resources subclasses, which are the ones that load the actual data and
 * store them for the rest of the game to receive. It is a very important
 * class because this ensures that all of the Sprites are only loaded once.
 * 
 * @author miweinst
 *
 */

public abstract class Resources {
	private static Resources store;
	protected HashMap<String, BufferedImage[]> _cache;
		
	public Resources () {
		_cache = new HashMap<String, BufferedImage[]>();
	}	 
	
	/**
	 * This accessors returns the SINGLE instance
	 * of the Resources subclass as a static var.
	 * @return
	 */
	public static Resources get() {
		return store;
	}
	
	/**
	 * This class allows a subclass with a specific cache
	 * of game data to set the cache in the Superclass,
	 * so generic classes with reference to this abstract 
	 * class will still have access to the game data.
	 * @param cache
	 */
	protected void setCache(HashMap<String, BufferedImage[]> cache) {
		_cache = cache;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public BufferedImage[] getValue(String key) {
		if (_cache.containsKey(key)) {
			return _cache.get(key);
		} else {
			System.out.println("Resources cache does not contain images at that key!");
			return null;
		}
	}
}
