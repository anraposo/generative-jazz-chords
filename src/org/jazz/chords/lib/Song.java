package org.jazz.chords.lib;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jazz.chords.utils.Chord;
import org.jazz.chords.utils.Key;

/**
 * 
 * @author Adriano Raposo
 *
 */
public class Song {
	
	private String title;
	private String composer;
	private String key;				
	private String rhythm;
	private String timeSignature;	
	private String structure;
	
	private ArrayList<Section> sections = new ArrayList<Section>();

	/**
	 * 
	 */
	public Song(String structure) {
		this.title = "";
		this.composer = "";
		this.key = "";
		this.rhythm = "";
		this.timeSignature = "";
		this.structure = structure;
	}

	/**
	 * 
	 * @param title
	 * @param composer
	 * @param key
	 * @param rhythm
	 * @param timeSignature
	 */
	public Song(String title, String composer, String key, String rhythm, String timeSignature) {
		
		this.title = title;
		this.composer = composer;
		this.key = key;
		this.rhythm = rhythm;
		this.timeSignature = timeSignature;
		this.structure = "";
	}

	/**
	 * 
	 * @param title
	 * @param composer
	 * @param key
	 * @param rhythm
	 * @param timeSignature
	 */
	public Song(String title, String composer, String key, String rhythm, String timeSignature,
			String structure) {
		
		this.title = title;
		this.composer = composer;
		this.key = key;
		this.rhythm = "(no rhythm)";
		this.timeSignature = timeSignature;
		this.structure = structure;
	}
	
	/**
	 * 
	 */
	public Map<String,Integer> getTransitionsCount() {
		
		List<String> chords = getChords();
		HashMap<String,Integer> transitions = new HashMap<String,Integer>();
		
		for ( int i=0; i<chords.size()-1; i++ ) {
			String t = chords.get(i) + ">" + chords.get(i+1);
			if ( transitions.containsKey(t) ) {
				transitions.merge( t, 1, Integer::sum );
			} else {
				transitions.put( t, 1 );
			}
		}
		
		return Collections.unmodifiableMap( transitions );
	}
	
	/**
	 * 
	 */
	public List<String> getChords() {
		
		List<String> chords = new ArrayList<String>();
		
		for ( Section s : sections ) {
			
			// main segment
			String[] sectionBars = s.getMainSegment().split("\\|");
			for ( String bar : sectionBars ) {
				
				String[] barChords = bar.split(",");
				for (String chord : barChords ) {
					String[] noBassChord = chord.split("/");
					String c = noBassChord[0];
					if (!c.equals(""))
						chords.add( c );
				}
			}
		
			// endings
			for ( String e : s.getEndings() ) {
				String[] endingsBars = e.split("\\|");
				for ( String bar : endingsBars ) {
					
					String[] barChords = bar.split(",");
					for (String chord : barChords ) {
						String[] noBassChord = chord.split("/");
						String c = noBassChord[0];
						if (!c.equals(""))
							chords.add( c );
					}
				}
			}
		}
		return chords;
	}
	
	/**
	 * 
	 */
	public String getFirstChord() {
		return sections.get(0).getFirstChord();
	}
	
	/**
	 * 
	 */
	public List<Section> getSections() {
		return Collections.unmodifiableList(sections);
	}
	
	/**
	 * 
	 */
	public Map<String,String> getChordsByBarPatterns() {
		
		HashMap<String,String> patterns = new HashMap<String,String>();
		
		for (Section s : sections) {
			if ( !patterns.containsKey( s.getLabel() )) 
				patterns.put( s.getLabel(), s.getChordsPerBarPattern() );
		}
		return patterns;
	}
	
	/**
	 * 
	 */
	public void addSection(Section section) {
		sections.add(section);
	}
	
	/**
	 * 
	 */
	public void addToStructure(String sectionLabel) {
		structure = structure.concat(sectionLabel);
	}

	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getComposer() {
		return composer;
	}



	public void setComposer(String composer) {
		this.composer = composer;
	}



	public String getKey() {
		return key;
	}



	public void setKey(String key) {
		this.key = key;
	}



	public String getRhythm() {
		return rhythm;
	}



	public void setRhythm(String rhythm) {
		this.rhythm = rhythm;
	}



	public String getStructure() {
		return structure;
	}



	public void setStucture(String structure) {
		this.structure = structure;
	}



	public String getTimeSignature() {
		return timeSignature;
	}



	public void setTimeSignature(String timeSignature) {
		this.timeSignature = timeSignature;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCompleteHarmony() {
		String harmony = "";
		for ( char label : structure.toCharArray() ) {
			harmony += sections.stream()
					.filter(s -> s.getLabel().equals(String.valueOf(label)))
					.map(Section::getMainSegment) // Map to main segment
	                .collect(Collectors.joining());
			harmony += "|";
		}
		if (harmony.length() > 0) {
			harmony = harmony.substring(0, harmony.length() - 1);
        }
		return harmony;
	}

	@Override
	/**
	 * 
	 */
	public String toString() {
		String s = "--- Song ---\n";
		s += "Title: " + this.getTitle() + "\n";
		s += "Composer: " + this.getComposer() + "\n";
		s += "Key: " + this.getKey() + "\n";
		s += "Time signature: " + this.getTimeSignature() + "\n";
		s += "Structure: " + this.getStructure() + "\n";
		for ( Section sec : this.getSections() ) {
			s += sec.toString();
		}
		s += "\n" + this.getCompleteHarmony();
		return s;
	}
	
	/**
	 * 
	 */
	public void exportToMusicXML(String filename) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
            writer.write("<!DOCTYPE score-partwise PUBLIC\r\n"
            		+ "    \"-//Recordare//DTD MusicXML 4.0 Partwise//EN\"\r\n"
            		+ "    \"http://www.musicxml.org/dtds/partwise.dtd\">\n");
            writer.write("<score-partwise version=\"4.0\">\r\n"
            		+ "  <part-list>\r\n"
            		+ "    <score-part id=\"P1\">\r\n"
            		+ "      <part-name>Music</part-name>\r\n"
            		+ "    </score-part>\r\n"
            		+ "  </part-list>\r\n"
            		+ "  <part id=\"P1\">\n");
            
            String[] bars = getCompleteHarmony().split("\\|");
            for ( int i=0; i<bars.length; i++ ) {
                writer.write("<measure number=\"" + (i+1) + "\">\r\n");
                if ( i == 0 ) { 
                	writer.write("<attributes>\r\n"
                			+ "      <key>\r\n"
                			+ "         <fifths>" + Key.getFifths(getKey()) + "</fifths>\r\n"
                			+ "      </key>\r\n"
                			+ "   </attributes>");
                }
                String[] chords = bars[i].split(",");	
                for ( String chord : chords ) {
                	int duration = 4 / chords.length;
                	String type = "half";
                	if ( chords.length == 2) {
                		type = "quarter";
                	} else if  ( chords.length == 4) {
                		type = "eighth";
                	}
                	String root = Chord.getRoot(chord);
	                writer.write("      <harmony>\r\n"
	                		+ "        <root>\r\n"
	                		+ "      		<root-step>" + root + "</root-step>\r\n");
	    	                if ( root.contains("#") )
	    	                	writer.write("<root-alter>1</root-alter>");
	    	                if ( root.contains("b") )
	    	                	writer.write("<root-alter>-1</root-alter>");
	                		writer.write("   	   </root>\r\n"
	                		+ "        <kind>" + Chord.getKind(chord) + "</kind>\n"
	                		+ "      </harmony>\r\n"
	                		+ "      <note>\r\n"
	                    	+ "        <rest/>\r\n"
	                    	+ "        <duration>" + duration + "</duration>\n"
	                    	+ "        <type>" + type + "</type>\n"
	                    	+ "      </note>\r\n");
                }		
                writer.write("</measure>\n");
            }
            
            writer.write("</part>\r\n"
            		+ "</score-partwise>");
            
            writer.close();

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
	}
	
}