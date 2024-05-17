package org.jazz.chords.lib;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.parser.ParseException;

/**
 * 
 * @author Adriano Raposo
 *
 */
public class HarmonyGenerator {
	
	private static final String CORPUS_JSON_FILENAME = "corpus.json";
	
	// Songs corpus
	Corpus corpus = new Corpus();
	
	// Initial key probabilities vector
	private HashMap<String,Double> keysWithProbabilities = 
			new HashMap<String,Double>();
	
	// Initial structure probabilities vector
	private HashMap<String,Double> structuresWithProbabilities = 
			new HashMap<String,Double>();
	
	// Initial time signature probabilities vector
	private HashMap<String,Double> timeSignaturesWithProbabilities = 
			new HashMap<String,Double>();

	// Probabilities for the first bar per key 
	private HashMap<String,HashMap<String,Double>> firstChordWithProbabilities = 
			new HashMap<String,HashMap<String,Double>>();

	// Probabilities of patterns of chords by label
	private HashMap<String,HashMap<String,Double>> chordsPatternProbabilities = 
			new HashMap<String,HashMap<String,Double>>();

	// Probabilities of chord pair transitions per key 
	private HashMap<String,HashMap<String,Double>> chordPairsTransitionsProbabilities = 
			new HashMap<String,HashMap<String,Double>>();
	
	/**
	 * 
	 */
	public HarmonyGenerator() {
		
	}
	
	/**
	 * 
	 */
	public Song generateRandomSong() {
		return generateSong( generateKey(), generateStructure() );
	}
	
	/**
	 * 
	 */
	public Song generateSongInKey(String key) {
		return generateSong( key, generateStructure() );
	}
	
	/**
	 * 
	 */
	public Song generateSongWithStructure(String structure) {
		return generateSong( generateKey(), structure );
	}
	
	/**
	 * 
	 */
	public Song generateSong(String key, String structure) {
		
		String timeSignature = generateTimeSignature();
		
		Song s = new Song("New song", "Generator", key, "(no rhythm)", timeSignature, structure);
		
		HashMap<String,Section> sections = new HashMap<String,Section>();
		for (char sectionLabel : structure.toCharArray()) {
			if ( !sections.containsKey( String.valueOf(sectionLabel)) ) {
				String chordsPattern = generatePatternByLabel(sectionLabel);
				String sectionKey = key;
				if ( sectionLabel != 'A' && sectionLabel != 'i') {
					sectionKey = generateKey(); 
				}
				String harmony = generateHarmonyInKey(sectionKey, chordsPattern);
				Section section = new Section(String.valueOf(sectionLabel),"0",chordsPattern,harmony);
				section.setKey(sectionKey);
				sections.put(String.valueOf(sectionLabel), section);
			}
		}
		for (Map.Entry<String, Section> section : sections.entrySet()) {
			s.addSection(section.getValue());
		}
		return s;
	}
	
	/**
	 * 
	 */
	public String generateStructure() {
		
		return stochasticChoose( structuresWithProbabilities );
	}
	
	/**
	 * 
	 */
	public String generateTimeSignature() {
		
		return stochasticChoose( timeSignaturesWithProbabilities );
	}
	
	/**
	 * 
	 */
	public String generateKey() {
		
		return stochasticChoose( keysWithProbabilities );
	}
	
	/**
	 * 
	 */
	public String generateFirstChord( String key ) {
		
		return stochasticChoose( firstChordWithProbabilities.get(key) );
	}
	
	/**
	 * 
	 */
	public String generatePatternByLabel( char sectionLabel ) {
		
		HashMap<String,Double> patternsProbabilitiesByLabel = chordsPatternProbabilities.get( String.valueOf(sectionLabel) );
		
		return stochasticChoose( patternsProbabilitiesByLabel );
	}
	
	/**
	 * 
	 */
	public String generateNextChord(String key, String presentChord) {
		
		// TODO to prevent presentChord is null exception
		if ( presentChord == null )
			return "";
		
		HashMap<String,Double> chordPairsProbabilities = chordPairsTransitionsProbabilities.get(key);
		HashMap<String,Double> presentChordProbabilities = new HashMap<String,Double>();
		
		for (Map.Entry<String, Double> chordPairProbability : chordPairsProbabilities.entrySet()) {
			String[] chordsInPair = chordPairProbability.getKey().split(">");
			if ( presentChord.equals(chordsInPair[0]) ) {
				presentChordProbabilities.put(chordsInPair[1], chordPairProbability.getValue());
			}
		}
		return stochasticChoose( presentChordProbabilities );
	}
	
	/**
	 * 
	 * @param key
	 * @param chordsPattern
	 * @return
	 */
	public String generateHarmonyInKey(String key, String chordsPattern) {
		
		String harmony = "";
		String chord = generateFirstChord(key);
		
		for ( int i=0; i < chordsPattern.length(); i++ ) {
			harmony += chord;
			int chordsCount =  Character.getNumericValue(chordsPattern.charAt(i));
			for (int j=1; j<chordsCount; j++) {
				harmony += ",";
				chord = generateNextChord(key, chord);
				harmony += chord;
			}
			if ( i < chordsPattern.length() - 1 ) 
				harmony += "|";
			chord = generateNextChord(key, chord);
		}
		
		return harmony;
	}
	
	/**
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 */
	public void loadCorpus() throws FileNotFoundException, IOException, ParseException {
		
		// load songs corpus from JSON file
		corpus.loadJSONFromFile( CORPUS_JSON_FILENAME );
		//System.out.println( "Loaded standards : " + corpus.getSize() );
	}
	
	/**
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 */
	public void train() {
		
		List<Song> songs = corpus.getSongs();
		
		// calculate probabilities of keys and first chord
		calculateKeysProbabilities( songs );
		//System.out.println( "Keys probabilities: ");
		//System.out.println( keysWithProbabilities );
		
		// calculate probabilities of time signatures
		calculateTimeSignaturesProbabilities( songs );
		//System.out.println( "Time signatures probabilities: ");
		//System.out.println( timeSignaturesProbabilities );
		
		// calculate probabilities of structures
		calculateStructuresProbabilities( songs );
/*		System.out.println( "Structures probabilities: ");
		DecimalFormat df = new DecimalFormat("#.###");
		Map<String, Double> sortedMap = structuresWithProbabilities.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, // Merge function to handle duplicates (if any)
                        LinkedHashMap::new // Keep the order of insertion
                ));
        //sortedMap.forEach((key, value) -> System.out.println(key + " & " + df.format(value) + " \\\\"));
        // Generate LaTeX table
        StringBuilder latexTable = new StringBuilder();
        latexTable.append("\\begin{tabular}{|l|l|}\n");
        latexTable.append("\\hline\n");
        latexTable.append("Key & Value \\\\ \n");
        latexTable.append("\\hline\n");
        int rowCount = 0;
        for (Entry<String, Double> entry : sortedMap.entrySet()) {
            latexTable.append(entry.getKey()).append(" & ").append(df.format(entry.getValue())).append(" \\\\ \n");
            rowCount++;
            // If 55 rows reached, start a new column
            if (rowCount % 55 == 0) {
                if (rowCount > 0) {
                    // Close the previous tabular environment
                    latexTable.append("\\end{tabular}\n");
                    latexTable.append("\\hspace{1cm}\n"); // Add space between columns
                }
                // Start a new tabular environment for the next 50 rows
                latexTable.append("\\begin{tabular}{|l|l|}\n");
                latexTable.append("\\hline\n");
                latexTable.append("Key & Value \\\\ \n");
                latexTable.append("\\hline\n");
            }
            latexTable.append(entry.getKey()).append(" & ").append(df.format(entry.getValue())).append(" \\\\ \n");
            rowCount++;
        }
        // Close the last tabular environment
        latexTable.append("\\end{tabular}\n");
        // Print the LaTeX table
        System.out.println(latexTable.toString());
*/		
		// calculate probabilities of chords per bar patterns
		calculateChordsPatternsProbabilties( songs );
		//System.out.println( "Chords patterns probabilities: ");
		//System.out.println( chordsPatternProbabilities );
/*
        // Sort the filtered map by value in descending order
        Map<String, Double> sortedMap = chordsPatternProbabilities.entrySet().stream()
                .filter(entry -> entry.getKey().equals("v"))
                .flatMap(entry -> entry.getValue().entrySet().stream())
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        // Print the LaTeX table
        System.out.println("\\begin{table}[h]");
        System.out.println("\\centering");
        System.out.println("\\begin{tabular}{|c|c|}");
        System.out.println("\\hline");
        System.out.println("Pattern & Probability \\\\");
        System.out.println("\\hline");
        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            System.out.printf("%s & %.3f \\\\\n", entry.getKey(), entry.getValue());
        }
        System.out.println("\\hline");
        System.out.println("\\end{tabular}");
        System.out.println("\\caption{Section \"A\" most common patterns probabilities}");
        System.out.println("\\label{table:A_patterns_probabilities}");
        System.out.println("\\end{table}");
*/     
		// calculate probabilities of fisrt chord by key
		calculateFirstChordProbabilities( songs );
/*
		        // Sort the filtered map by value in descending order
		        Map<String, Double> sortedMap = firstChordWithProbabilities.entrySet().stream()
		                .filter(entry -> entry.getKey().equals("G#min"))
		                .flatMap(entry -> entry.getValue().entrySet().stream())
		                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
		                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		        // Print the LaTeX table
		        System.out.println("\\begin{table}[h]");
		        System.out.println("\\centering");
		        System.out.println("\\begin{tabular}{|c|c|}");
		        System.out.println("\\hline");
		        System.out.println("Chord & Probability \\\\");
		        System.out.println("\\hline");
		        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
		            System.out.printf("%s & %.3f \\\\\n", entry.getKey(), entry.getValue());
		        }
		        System.out.println("\\hline");
		        System.out.println("\\end{tabular}");
		        System.out.println("\\caption{First chord probabilities in an \textbf{F} key context");
		        System.out.println("\\label{table:A_patterns_probabilities}");
		        System.out.println("\\end{table}");
		
*/		
		// calculate probabilities of chords transitions
		calculateChordsTransitionProbabilties( songs );
/*		
		// Sort the filtered map by value in descending order
        Map<String, Double> sortedMap = chordPairsTransitionsProbabilities.entrySet().stream()
                .filter(entry -> entry.getKey().equals("F"))
                .flatMap(entry -> entry.getValue().entrySet().stream())
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Map<String,Double> filteredMap = sortedMap.entrySet().stream()
        		.filter(e -> e.getKey().contains("GM7>"))
        		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        // Print the LaTeX table
        System.out.println("\\begin{table}[h]");
        System.out.println("\\centering");
        System.out.println("\\begin{tabular}{|c|c|}");
        System.out.println("\\hline");
        System.out.println("Chord & Probability \\\\");
        System.out.println("\\hline");
        for (Map.Entry<String, Double> entry : filteredMap.entrySet()) {
            System.out.printf("%s & %.3f \\\\\n", entry.getKey(), entry.getValue());
        }
        System.out.println("\\hline");
        System.out.println("\\end{tabular}");
        System.out.println("\\caption{First chord probabilities in an \textbf{F} key context");
        System.out.println("\\label{table:A_patterns_probabilities}");
        System.out.println("\\end{table}");
        System.out.println(filteredMap.size());
        System.out.println(filteredMap.entrySet().stream().collect(Collectors.summingDouble(Map.Entry::getValue)));
*/		   
		//System.out.println( corpus.getSongs() );
	}
	
	/**
	 * 
	 */
	private void calculateKeysProbabilities( List<Song> songs ) {
		
		List<String> existingKeys = corpus.getExistingKeys();
		
		for ( String key : existingKeys ) {
			
			double songsInKey = songs.stream().filter((Song s) -> {
	            return s.getKey().equals(key);
	        }).count();

			double keyProbability = songsInKey / songs.size();
			
			keysWithProbabilities.put(key, keyProbability);
		}
	}
	
	/**
	 * 
	 */
	private void calculateTimeSignaturesProbabilities( List<Song> songs ) {
		
		List<String> existingSignatures = songs.stream().map(Song::getTimeSignature).distinct().sorted().
				filter(i -> !i.isEmpty()).
				collect(Collectors.toList());
		
		for ( String timeSignature : existingSignatures ) {
			
			double songsWithSignature = songs.stream().filter((Song s) -> {
	            return s.getTimeSignature().equals(timeSignature);
	        }).count();
			double probability = songsWithSignature / songs.size();
			
			timeSignaturesWithProbabilities.put(timeSignature, probability);
		}
	}
	
	/**
	 * 
	 */
	private void calculateStructuresProbabilities( List<Song> songs ) {
		
		List<String> existingStructures = songs.stream().map(Song::getStructure).distinct().sorted().
				filter(i -> !i.isEmpty()).
				collect(Collectors.toList());
		
		for ( String structure : existingStructures ) {
			
			double songsWithStructure = songs.stream().filter((Song s) -> {
	            return s.getStructure().equals(structure);
	        }).count();
			double probability = songsWithStructure / songs.size();
			
			structuresWithProbabilities.put(structure, probability);
		}
	}
	
	/**
	 * 
	 */
	private void calculateChordsPatternsProbabilties( List<Song> songs ) {
		
		for ( String label : corpus.getDistinctSectionLabels() ) {
			
			HashMap<String,Double> probabilities = new HashMap<String,Double>();
			Map<String,Integer> patternsCount = corpus.getDistinctPatternsCountByLabel(label);
					
			double totalOccurences = 0;
			for ( Map.Entry<String,Integer> pattern : patternsCount.entrySet() ) {
				totalOccurences += pattern.getValue();
			}
			
			for ( Map.Entry<String,Integer> pattern : patternsCount.entrySet() ) {
				probabilities.put(pattern.getKey(), pattern.getValue() / totalOccurences);
			}
			
			chordsPatternProbabilities.put(label, probabilities);
		}
	}
	
	/**
	 * 
	 */
	private void calculateFirstChordProbabilities( List<Song> songs ) {
		
		List<String> existingKeys = corpus.getExistingKeys();
		
		for ( String key : existingKeys ) {
			
			List<Song> songsInKey = songs.stream().filter((Song s) -> {
	            return s.getKey().equals(key);
	        }).toList();
			
			List<String> existingFirstBars = corpus.getExistingFirstChordsInKey( key );
			
			HashMap<String,Double> barsProbabilities = new HashMap<String,Double>(); 
			
			for ( String bar : existingFirstBars ) {
				
				double songsInKeyWithFirstBar = songsInKey.stream().filter((Song s) -> {
		            return s.getFirstChord().equals(bar);
		        }).count();
				
				double probability = songsInKeyWithFirstBar / songsInKey.size();
				barsProbabilities.put(bar, probability);
			}
			
			firstChordWithProbabilities.put(key, barsProbabilities);
		}
	}
	
	/**
	 * 
	 */
	private void calculateChordsTransitionProbabilties( List<Song> songs ) {
		
		List<String> allKeys = corpus.getExistingKeys();
		
		for (String key : allKeys ) {
		
			Map<String,Integer> chordPairsCounting = corpus.getDistinctChordPairsCountInKey(key);
			HashMap<String,Integer> firstChordCount = new HashMap<String,Integer>();
			HashMap<String,Double> chordPairsProbabilities = new HashMap<String,Double>();
			
			// calculate total occurrences of first chord
			for (Map.Entry<String, Integer> chordPairsCount : chordPairsCounting.entrySet() ) {
				String[] chordPairSplit = chordPairsCount.getKey().split(">");
				if ( firstChordCount.containsKey(chordPairSplit[0]) ) {
					firstChordCount.merge( chordPairSplit[0], chordPairsCount.getValue() , Integer::sum );
				} else {
					firstChordCount.put( chordPairSplit[0], chordPairsCount.getValue());
				}
			}
			
			// calculate probabilities of chord pair
			for (Map.Entry<String, Integer> chordPairsCount : chordPairsCounting.entrySet() ) {
				String[] chordPairSplit = chordPairsCount.getKey().split(">");
				chordPairsProbabilities.put(chordPairsCount.getKey(), 
						chordPairsCount.getValue() / firstChordCount.get(chordPairSplit[0]).doubleValue() );
			}

			// add probabilities map to the key entry
			chordPairsTransitionsProbabilities.put(key, chordPairsProbabilities);
		}
	}
	
	/**
	 * 
	 */
	private static String stochasticChoose( HashMap<String,Double> map ) {
		
		double p = Math.random();
		
		double cumulativeProbability  = 0.0;
		for ( Map.Entry<String, Double> entry : map.entrySet()) {
			cumulativeProbability += entry.getValue();
			if ( p <= cumulativeProbability )
				return entry.getKey().replace("?", "A");
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> getExistingKeys() {
		return corpus.getExistingKeys();
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> getExistingStructures() {
		return corpus.getExistingStructures();
	}
}
