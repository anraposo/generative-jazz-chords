package org.jazz.chords.gui;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.jazz.chords.utils.Chord;

/**
 * 
 * @author Adriano Raposo
 *
 */
public class HarmonyRenderer {
	
	private final int TICKS_PER_BEAT = 24;
	private final int TICKS_PER_BAR  = TICKS_PER_BEAT * 4;
	private final int VELOCITY = 75;
	
	private Sequence sequence;
	private Track track;

	/**
	 * @throws InvalidMidiDataException 
	 * 
	 */
	public HarmonyRenderer()  {
		
	}
	
	/**
	 * 
	 */
	public Sequence getMidiSequence( String harmony ) {
		
		try {
			sequence = new Sequence(Sequence.PPQ, TICKS_PER_BEAT);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		
		track = sequence.createTrack();
		
		String[] bars = harmony.split("\\|");
		
		int currentTick = 0;
		
		for ( String bar : bars ) {
			
			String[] chords = bar.split(",");
			int countChords = chords.length;
			
			for ( String chord : chords ) {
				
				for ( Integer note : Chord.getNotes(chord) ) {
					try {
						track.add(createNoteOnEvent(note, VELOCITY, currentTick));
				        track.add(createNoteOffEvent(note, 0, currentTick + TICKS_PER_BAR / countChords));
					} catch (InvalidMidiDataException e) {
						e.printStackTrace();
					} 
				}
		        currentTick += (TICKS_PER_BAR / countChords);
			}
		}
		
		return sequence;
	}
    
    // Helper method to create a MIDI Note On event
    private static MidiEvent createNoteOnEvent(int note, int velocity, long tick) throws InvalidMidiDataException {
        ShortMessage msg = new ShortMessage();
        msg.setMessage(ShortMessage.NOTE_ON, 0, note, velocity);
        return new MidiEvent(msg, tick);
    }
    
    // Helper method to create a MIDI Note Off event
    private static MidiEvent createNoteOffEvent(int note, long tick, int duration) throws InvalidMidiDataException {
        ShortMessage msg = new ShortMessage();
        msg.setMessage(ShortMessage.NOTE_OFF, 0, note, 0); // velocity 0
        return new MidiEvent(msg, tick + duration);
    }
}
