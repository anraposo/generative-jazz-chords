package org.jazz.chords.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Adriano Raposo
 *
 */
public abstract class Chord {

	/**
	 * 
	 */
	private static final HashMap<String,Integer> chordRoots = new HashMap<String,Integer>();
	static {
		chordRoots.put("D#"	,51);
		chordRoots.put("Eb"	,51);
		chordRoots.put("E"	,52);
		chordRoots.put("F"	,53);
		chordRoots.put("F#"	,54);
		chordRoots.put("Gb"	,54);
		chordRoots.put("G"	,55);
		chordRoots.put("G#"	,56);
		chordRoots.put("Ab"	,56);
		chordRoots.put("A"	,57);
		chordRoots.put("A#"	,58);
		chordRoots.put("Bb"	,58);
		chordRoots.put("B"	,59);
		chordRoots.put("C"	,60);
		chordRoots.put("C#"	,61);
		chordRoots.put("Db"	,61);
		chordRoots.put("D"	,62);
	}
	
	/**
	 * 
	 */
	private static final HashMap<String,int[]> chordKinds = new HashMap<String,int[]>();
	static {
		chordKinds.put("", new int[] { 0 , 4 , 7 });
		chordKinds.put("m", new int[] { 0 , 3 , 7 });
		chordKinds.put("maj7", new int[]{0, 4, 7, 11});
		chordKinds.put("maj7#11", new int[]{0, 4, 7, 11, 18});
        chordKinds.put("m7", new int[]{0, 3, 7, 10});
        chordKinds.put("0", new int[]{0, 7, 10});
        chordKinds.put("07", new int[]{0, 3, 6, 9});
        chordKinds.put("7", new int[]{0, 4, 7, 10});
        chordKinds.put("7sus", new int[]{0, 5, 7, 10});
        chordKinds.put("7#5#9", new int[]{0, 4, 8, 10});
        chordKinds.put("7#5#9", new int[]{0, 4, 8, 11, 15});
        chordKinds.put("m7b5", new int[]{0, 3, 6, 10});
        chordKinds.put("dim7", new int[]{0, 3, 6, 9});
        chordKinds.put("sus4", new int[]{0, 5, 7});
        chordKinds.put("sus2", new int[]{0, 2, 7});
        chordKinds.put("7sus4", new int[]{0, 5, 7, 10});
        chordKinds.put("9", new int[]{0, 4, 7, 10, 14});
        chordKinds.put("9sus", new int[]{0, 7, 10, 14});
        chordKinds.put("m9", new int[]{0, 3, 7, 10, 14});
        chordKinds.put("maj9", new int[]{0, 4, 7, 11, 14});
        chordKinds.put("11", new int[]{0, 4, 7, 10, 14, 17});
        chordKinds.put("m11", new int[]{0, 3, 7, 10, 14, 17});
        chordKinds.put("13", new int[]{0, 4, 7, 10, 14, 17, 21});
        chordKinds.put("13b9", new int[]{0, 4, 7, 9, 10, 12});
        chordKinds.put("m13", new int[]{0, 3, 7, 10, 14, 17, 21});
        chordKinds.put("maj13", new int[]{0, 4, 7, 11, 14, 17, 21});
        chordKinds.put("6", new int[]{0, 4, 7, 9});
        chordKinds.put("m6", new int[]{0, 3, 7, 9});
        chordKinds.put("mb6", new int[]{0, 3, 7, 8});
        chordKinds.put("add9", new int[]{0, 4, 7, 14});
        chordKinds.put("m(add9)", new int[]{0, 3, 7, 14});
        chordKinds.put("69", new int[]{0, 4, 7, 9, 14});
        chordKinds.put("m69", new int[]{0, 3, 7, 9, 14});
        chordKinds.put("aug", new int[]{0, 4, 8});
        chordKinds.put("aug7", new int[]{0, 4, 8, 10});
        chordKinds.put("aug9", new int[]{0, 4, 8, 10, 14});
        chordKinds.put("dim", new int[]{0, 3, 6});
        chordKinds.put("dim7", new int[]{0, 3, 6, 9});
        chordKinds.put("sus4(b9)", new int[]{0, 5, 7, 10});
        chordKinds.put("7b9", new int[]{0, 4, 7, 10, 13});
        chordKinds.put("7b9sus", new int[]{0, 5, 7, 10, 13});
        chordKinds.put("7#9", new int[]{0, 4, 7, 10, 15});
        chordKinds.put("7#11", new int[]{0, 4, 7, 10, 18});
        chordKinds.put("7b13", new int[]{0, 4, 7, 10, 17, 20});
        chordKinds.put("7#5", new int[]{0, 4, 8, 10});
        chordKinds.put("7b5", new int[]{0, 4, 6, 10});
        chordKinds.put("7b9#5", new int[]{0, 4, 8, 10, 13});
        chordKinds.put("7#9#5", new int[]{0, 4, 8, 10, 15});
        chordKinds.put("7#9b5", new int[]{0, 4, 6, 10, 15});
        chordKinds.put("7alt", new int[]{0, 4, 8, 10, 14, 18});
        chordKinds.put("7sus4(b9)", new int[]{0, 5, 7, 10, 13});
        chordKinds.put("7sus4(#9)", new int[]{0, 5, 7, 10, 15});
        chordKinds.put("7sus4#11", new int[]{0, 5, 7, 10, 18});
        chordKinds.put("7b5(#9)", new int[]{0, 4, 6, 10, 15});
        chordKinds.put("7b5b9", new int[]{0, 4, 6, 10, 13});
        chordKinds.put("7b5b13", new int[]{0, 4, 6, 10, 17, 20});
        chordKinds.put("7b9#5", new int[]{0, 4, 8, 10, 13});
        chordKinds.put("7b9b5", new int[]{0, 4, 6, 10, 13});
        chordKinds.put("7b9b13", new int[]{0, 4, 7, 10, 13, 20});
        chordKinds.put("9#11", new int[]{0, 4, 7, 10, 14, 18});
        chordKinds.put("m7b5(b9)", new int[]{0, 3, 6, 10, 13});
        chordKinds.put("m7b5#9", new int[]{0, 3, 6, 10, 15});
        chordKinds.put("m7b5b9", new int[]{0, 3, 6, 10, 13});
        chordKinds.put("m7b5b13", new int[]{0, 3, 6, 10, 13, 20});
        chordKinds.put("m7b9", new int[]{0, 3, 7, 10, 13});
        chordKinds.put("m7b9#11", new int[]{0, 3, 7, 10, 13, 18});
        chordKinds.put("m7b9b5", new int[]{0, 3, 6, 10, 13});
        chordKinds.put("m7b9b13", new int[]{0, 3, 7, 10, 13, 20});
        chordKinds.put("m7#5", new int[]{0, 3, 8, 10});
        chordKinds.put("m7#9", new int[]{0, 3, 7, 10, 15});
        chordKinds.put("m7#9#11", new int[]{0, 3, 7, 10, 15, 18});
        chordKinds.put("m7#11", new int[]{0, 3, 7, 10, 18});
        chordKinds.put("m7#9b5", new int[]{0, 3, 6, 10, 15});
        chordKinds.put("m7#9#5", new int[]{0, 3, 8, 10, 15});
        chordKinds.put("m7alt", new int[]{0, 3, 7, 10, 14, 18});
        chordKinds.put("m9(#11)", new int[]{0, 3, 7, 10, 14, 18});
        chordKinds.put("m9#5", new int[]{0, 3, 8, 10, 14});
        chordKinds.put("m9b5", new int[]{0, 3, 6, 10, 14});
        chordKinds.put("m9#11", new int[]{0, 3, 7, 10, 14, 18});
        chordKinds.put("m9b5(#11)", new int[]{0, 3, 6, 10, 14, 18});
        chordKinds.put("m9b5#11", new int[]{0, 3, 6, 10, 14, 18});
        chordKinds.put("m9b5b13", new int[]{0, 3, 6, 10, 14, 20});
        chordKinds.put("m9#5b13", new int[]{0, 3, 8, 10, 14, 20});
        chordKinds.put("m11b5", new int[]{0, 3, 6, 10, 14, 17});
        chordKinds.put("m11b9", new int[]{0, 3, 7, 10, 13, 17});
        chordKinds.put("m11#5", new int[]{0, 3, 8, 10, 14, 17});
        chordKinds.put("m11#5b13", new int[]{0, 3, 8, 10, 14, 17, 20});
        chordKinds.put("m13", new int[]{0, 3, 7, 10, 14, 17, 21});
        chordKinds.put("m13#11", new int[]{0, 3, 7, 10, 14, 18, 21});
        chordKinds.put("m13b5", new int[]{0, 3, 6, 10, 14, 17, 21});
        chordKinds.put("m13b9", new int[]{0, 3, 7, 10, 13, 17, 21});
        chordKinds.put("m13b9b5", new int[]{0, 3, 6, 10, 13, 17, 21});
        chordKinds.put("m13#5", new int[]{0, 3, 8, 10, 14, 17, 21});
        chordKinds.put("m13#9", new int[]{0, 3, 7, 10, 15, 17, 21});
        chordKinds.put("m13#5#11", new int[]{0, 3, 8, 10, 14, 18, 21});
        chordKinds.put("m13b9#11", new int[]{0, 3, 7, 10, 13, 18, 21});
	}
	
	/**
	 * 
	 * @return
	 */
	public static List<Integer> getNotes (String chord) {
		
		List<Integer> notes = new ArrayList<Integer>();
		
		int 	root = getRootNote(chord);
		String 	type = getKind(chord);
		
		if ( chordKinds.containsKey(type) ) {
			for ( int degree : chordKinds.get(type) ) {
				notes.add( root + degree );
			}
		} else {
			notes.add( root );
		}
		
		// low the root an octave
		notes.set(0, root - 12);
		
		return notes;
	}
	
	/**
	 * 
	 */
	public static String getRoot( String chord ) {
		
		String root;
		if ( chord.length() > 1 ) {
			root = chord.substring(0,2); // looks for flats b and sharps #
			// flats and sharps
			if ( chordRoots.containsKey(root) ) {
				return root;
			}
		}
		// naturals
		root = chord.substring(0,1);
		if ( chordRoots.containsKey(root) ) {
			return root;
		}
		return "";
	}
	
	/**
	 * 
	 */
	public static int getRootNote( String chord ) {
		
		String root;
		if ( chord.length() > 1 ) {
			root = chord.substring(0,2); // looks for flats b and sharps #
			// flats and sharps
			if ( chordRoots.containsKey(root) ) {
				return chordRoots.get(root);
			}
		}
		// naturals
		root = chord.substring(0,1);
		if ( chordRoots.containsKey(root) ) {
			return chordRoots.get(root);
		}
		return 0;
	}
	
	/**
	 * 
	 */
	public static String getKind( String chord ) {
		
		String root;
		if ( chord.length() > 1 ) {
			root = chord.substring(0,2); // looks for flats b and sharps #
			// flats and sharps
			if ( chordRoots.containsKey(root) ) {
				return chord.replace(root, "");
			}
		}
		// naturals
		root = chord.substring(0,1);
		if ( chordRoots.containsKey(root) ) {
			return chord.replace(root, "");
		}
		return "";
	}
}