package org.jazz.chords.lib;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author Adriano Raposo
 *
 */
public class Corpus {

	private static ArrayList<Song> songs = new ArrayList<Song>();

	/**
	 * 
	 */
	public Corpus() {
		
	}
	
	/**
	 * 
	 */
	public int getSize() {
		return songs.size();
	}
	
	/**
	 * 
	 */
	public Song getSong(int i) {
		return songs.get(i);
	}
	
	/**
	 * 
	 */
	public List<Song> getSongs() {
		return Collections.unmodifiableList(songs);
	}
	
	/**
	 * 
	 */
	public List<Song> getSongsInKey( String key ) {
		return songs.stream().filter(s -> s.getKey().equals(key)).toList();
	}
	
	/**
	 * 
	 */
	public List<String> getExistingKeys() {
		return songs.stream().map(Song::getKey).distinct().sorted().
				filter(i -> !i.isEmpty()).
				collect(Collectors.toList());
	}
	
	/**
	 * 
	 */
	public List<String> getExistingStructures() {
		return songs.stream()
				.map(Song::getStructure)
	            .filter(s -> !s.isEmpty()) // Filter out empty strings
	            .filter(s -> !s.contains("?")) // Filter out strings containing a question mark
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList());
	}
	
	/**
	 * 
	 */
	public List<String> getExistingChords() {
		return songs.stream()                                  
                .flatMap(song -> song.getChords().stream()) 
                .distinct()                       
                .sorted()         
                .collect(Collectors.toList());
	}
	
	/**
	 * 
	 */
	public List<String> getExistingFirstChordsInKey( String key ) {
		return getSongsInKey(key).stream().map(Song::getFirstChord).distinct().sorted().
				collect(Collectors.toList());
	}
	
	/**
	 * 
	 */
	public Map<String,Integer> getDistinctChordPairsCountInKey( String key ) {
		
		List<Song> songsInKey = getSongsInKey(key);
		
		HashMap<String,Integer> pairsInKey = new HashMap<String,Integer>();
		
		for (Song s : songsInKey) {
			
			List<String> songChords = s.getChords();
			
			for ( int i=0; i<songChords.size()-1; i++ ) {
				
				String cp = songChords.get(i) + ">" + songChords.get(i+1);
				
				if ( pairsInKey.containsKey( cp )) {
					pairsInKey.merge( cp, 1 , Integer::sum );
				} else {
					pairsInKey.put( cp, 1 );
				}
			}
		}
		
		HashMap<String, Integer> sortedPairsInKey = pairsInKey.entrySet()
              .stream()
              .sorted((i1, i2)
                          -> i1.getKey().compareTo(
                              i2.getKey()))
              .collect(Collectors.toMap(
                  Map.Entry::getKey,
                  Map.Entry::getValue,
                  (e1, e2) -> e1, LinkedHashMap::new));
		
		return Collections.unmodifiableMap( sortedPairsInKey );
	}
	
	/**
	 * 
	 */
	public List<Song> getSongsWithSection( String sectionLabel ) {
		return songs.stream().filter(s -> s.getChordsByBarPatterns().containsKey(sectionLabel)).toList();
	}
	
	/**
	 * 
	 */
	public List<String> getDistinctSectionLabels() {
		
		return songs.stream()
		                // Extract sections from each song
		                .flatMap(song -> song.getSections().stream())
		                // Extract labels from each section
		                .map(Section::getLabel)
		                // Filter out duplicate labels
		                .distinct()
		                // Collect unique labels into a List
		                .collect(Collectors.toList());
	}
	
	/**
	 * 
	 */
	public HashMap<String,Integer> getDistinctPatternsCount() {
		
		HashMap<String,Integer> patternsCount = new HashMap<String,Integer>();
		
		for ( Song song : songs ) {
			for ( Section s : song.getSections() ) {
				if ( patternsCount.containsKey( s.getChordsPerBarPattern()) ) {
					patternsCount.merge( s.getChordsPerBarPattern(), 1 , Integer::sum );
				} else {
					patternsCount.put( s.getChordsPerBarPattern(), 1 );
				}
			}
		}
		return patternsCount;
	}
	
	/**
	 * 
	 */
	public Map<String,Integer> getDistinctPatternsCountByLabel( String label ) {
		
		HashMap<String,Integer> patternsCount = new HashMap<String,Integer>();
		
		for ( Song song : songs ) {
			for ( Section s : song.getSections().stream().filter(s -> s.getLabel().equals(label)).toList() ) {
				if ( patternsCount.containsKey( s.getChordsPerBarPattern()) ) {
					patternsCount.merge( s.getChordsPerBarPattern(), 1 , Integer::sum );
				} else {
					patternsCount.put( s.getChordsPerBarPattern(), 1 );
				}
			}
		}
		return Collections.unmodifiableMap( patternsCount );
	}
	
	@Override
	public String toString() {
		return "Corpus: \n " + songs;
	}
	
	/**
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 */
	public void loadJSONFromFile(String file) throws FileNotFoundException, IOException, ParseException {
		
		InputStream inputStream = this.getClass().getResourceAsStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		JSONParser parser = new JSONParser();
		JSONArray a = (JSONArray) parser.parse(reader);

		for (Object o : a) {
			
			JSONObject songData = (JSONObject) o;
		    
			Song song = new Song(
		    		songData.get("Title") != null ? songData.get("Title").toString() : "",
		    		songData.get("Composer") != null ? songData.get("Composer").toString() : "",
		    		songData.get("Key") != null ? songData.get("Key").toString() : "C",
		    		songData.get("Rhythm") != null ? songData.get("Rhythm").toString() : "",
		    		songData.get("TimeSignature") != null ? songData.get("TimeSignature").toString() : "4/4"
		    );
		    
			// getting sections 
	        JSONArray aSections = (JSONArray) songData.get("Sections"); 
	        
	        for (Object oSection : aSections) {
	        	
	        	JSONObject sectionData = (JSONObject) oSection;
	        	
	        	Object oMainSegment = (JSONObject) sectionData.get("MainSegment");
	        	JSONObject mainSegmentData = (JSONObject) oMainSegment;
	        	
	        	Section section = new Section (
	        			sectionData.get("Label") != null ? sectionData.get("Label").toString() : "A",
	    	        	sectionData.get("Repeats") != null ? sectionData.get("Repeats").toString() : "0",
	    	        	mainSegmentData.get("Chords") != null ? mainSegmentData.get("Chords").toString() : ""
	        	);
	        	
	        	// getting endings 
	        	JSONArray aEndings = (JSONArray) sectionData.get("Endings");
	        	
	        	if ( aEndings != null ) {
		        	for (Object oEnding : aEndings) {
		        		song.addToStructure( sectionData.get("Label") != null ? sectionData.get("Label").toString() : "A" );
		        		JSONObject endingData = (JSONObject) oEnding;
		        		section.addEnding( endingData.get("Chords") != null ? endingData.get("Chords").toString() : "A" );
		        	}
	        	} else {
	        		song.addToStructure( sectionData.get("Label") != null ? sectionData.get("Label").toString() : "A" );
		        	if ( sectionData.get("Repeats") != null ) {
		        		song.addToStructure( sectionData.get("Label") != null ? sectionData.get("Label").toString() : "A" );
		        	}
	        	}
	        	
	        	song.addSection(section);
	        }

	        songs.add(song);
		}
	}
	
}
