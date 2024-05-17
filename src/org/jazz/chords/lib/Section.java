package org.jazz.chords.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Adriano Raposo
 *
 */
public class Section {

	private String label;
	private String key;
	private String repeats;
	private int barsCount = 0;
	private String mainSegment;
	private String chordsPerBarPattern;
	private ArrayList<String> endings = new ArrayList<String>();
	
	/**
	 * 
	 * @param label
	 * @param repeats
	 * @param mainSegment
	 */
	public Section(String label, String repeats, String chordsPerBarPattern, String mainSegment) {
		
		this.label = label;
		this.setRepeats(repeats);
		this.chordsPerBarPattern = chordsPerBarPattern;
		this.mainSegment = mainSegment.replaceAll("\\(.*\\)", ""); // remove alternative chords
		extractBarsInfo(mainSegment);
	}
	
	/**
	 * 
	 * @param label
	 * @param repeats
	 * @param mainSegment
	 */
	public Section(String label, String repeats, String mainSegment) {
		
		this.label = label;
		this.setRepeats(repeats);
		this.mainSegment = mainSegment.replaceAll("\\(.*\\)", ""); // remove alternative chords
		extractBarsInfo(mainSegment);
	}
	
	/**
	 * 
	 * @param label
	 * @param mainSegment
	 */
	public Section(String label, String mainSegment) {
		
		this.label = label;
		this.setRepeats("");
		this.mainSegment = mainSegment.replaceAll("\\(.*\\)", ""); // remove alternative chords
	}
	
	/**
	 * 
	 */
	private void extractBarsInfo(String mainSegment) {
		
		String[] bars = mainSegment.split("\\|");
		
		chordsPerBarPattern = "";
		
		for (String bar : bars) {
			String[] chords = bar.split(",");
			chordsPerBarPattern += String.valueOf( chords.length );
		}

		this.barsCount = bars.length;
	}
	
	/**
	 * 
	 */
	public String getChordsPerBarPattern() {
		return chordsPerBarPattern;
	}
	
	/**
	 * 
	 */
	public String getFirstChord() {
		return mainSegment.split("\\|")[0].split(",")[0]; 	// if we just want the first chord of the first bar
		//return mainSegment.split("\\|")[0];				// if we want the entire first bar
	}
	
	/**
	 * 
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * 
	 */
	public String getMainSegment() {
		return mainSegment;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> getEndings() {
		return Collections.unmodifiableList(endings);
	}
	
	/**
	 * 
	 */
	public void addEnding(String ending) {
		String e = ending.replaceAll("\\(.*\\)", ""); // remove alternative chords
		endings.add(e); 
		if (endings.isEmpty())
			this.barsCount += e.split("\\|").length;
	}
	
	/**
	 * 
	 */
	public int getBarsCount() {
		return barsCount;
	}

	@Override
	public String toString() {
		return getLabel() + " (" + getKey() + ") : " + 
				getMainSegment() /*+ 
				"[" + chordsPerBarPattern + "]" +
				+ getBarsCount()*/ + "\n";
	}

	public String getRepeats() {
		return repeats;
	}

	public void setRepeats(String repeats) {
		this.repeats = repeats;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}