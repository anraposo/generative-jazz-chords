package org.jazz.chords.utils;

import java.util.HashMap;

/**
 * 
 * @author Adriano Raposo
 *
 */
public abstract class Key {

	// circle of fifths keys
	public static final HashMap<String,Integer> fifths = new HashMap<String,Integer>();
	static {
		// major
		fifths.put("Cb",-7);
		fifths.put("Gb",-6);
		fifths.put("Db",-5);
		fifths.put("Ab",-4);
		fifths.put("Eb",-3);
		fifths.put("Bb",-2);
		fifths.put("F",-1);
		fifths.put("C",0);
		fifths.put("G",1);
		fifths.put("D",2);
		fifths.put("A",3);
		fifths.put("E",4);
		fifths.put("B",5);
		fifths.put("F#",6);
		fifths.put("C#",7);
		// minor
		fifths.put("Fmin",-4);
		fifths.put("Cmin",-3);
		fifths.put("Gmin",-2);
		fifths.put("Dmin",-1);
		fifths.put("Amin",0);
		fifths.put("Emin",1);
		fifths.put("Bmin",2);
		fifths.put("F#min",3);
		fifths.put("C#min",4);
		fifths.put("G#min",5);
		fifths.put("D#min",6);
		fifths.put("Ebmin",6);
		fifths.put("Bbmin",7);
	}
	
	/**
	 * 
	 */
	public static Integer getFifths(String key) {
		return fifths.get(key);
	}
	
}
