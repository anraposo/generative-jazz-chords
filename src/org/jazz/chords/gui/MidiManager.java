package org.jazz.chords.gui;

import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;

/**
 * 
 * @author Adriano Raposo
 *
 */
public class MidiManager {

	public static final int BPM_MIN 	= 60;
	public static final int BPM_DEFAULT = 120;
	public static final int BPM_MAX 	= 240;
	
	private Info[] midiDeviceInfo;
	
	// the MIDI device
	private MidiDevice device;
	// the MIDI sequencer
	private Sequencer sequencer;
	// the MIDI transmitter
	private Transmitter transmitter;
	// generated harmony
	private String harmony;
	// tempo in beats per minute
	private int bpm = BPM_DEFAULT;
	// string to midi harmony renderer
	private HarmonyRenderer renderer = new HarmonyRenderer();
	
	// the midi player thread inner class
	class MidiPlayer extends Thread {
		
		/**
		 * 
		 */
		public void run() {
			
			if ( sequencer.isRunning() ) 
				return;
			
			try {
				sequencer.open();
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
			
	        Sequence sequence = renderer.getMidiSequence(harmony);
			
			try {			
				sequencer.setTempoInBPM(bpm);
				sequencer.setSequence(sequence);	
		        sequencer.start();
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
	        
	        while ( sequencer.isRunning() ) {
	        	try {
	    			Thread.sleep(1000);
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}
	        }
	        sequencer.stop();
		}
	}
	
	// the midi player
	private MidiPlayer player;
	
	/**
	 * 
	 */
	public MidiManager() {
		
		try {
			
			sequencer = MidiSystem.getSequencer();
			sequencer.getTransmitters().get(0).close();
			
			midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
			
			device = MidiSystem.getMidiDevice( midiDeviceInfo[0] );
		    device.open();

			transmitter = sequencer.getTransmitter();
		    transmitter.setReceiver(device.getReceiver());
			
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void setHarmony(String harmony) {
		this.harmony = harmony;
	}
	
	/**
	 * @throws MidiUnavailableException 
	 * @throws InterruptedException 
	 * 
	 */
	public void play() {
		player = new MidiPlayer();
		player.start();
	}
	
	/**
	 * 
	 */
	public void stopSequencer() {
        if (sequencer.isRunning()) {
            sequencer.stop();
        }
	}
	
	/**
	 * 
	 */
	public void setTempoBPM( int bpm ) {
		sequencer.setTempoInBPM(bpm);
		this.bpm = bpm;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String[] getMidiOutPorts() {
		
		MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
		String[] devices = new String[midiDeviceInfo.length];
		for( int i = 0; i < midiDeviceInfo.length; i++ ) {
			devices[i] = midiDeviceInfo[i].getName() + " (" + midiDeviceInfo[i].getDescription() + ")";
		}
		return devices;
	}
	
	/**
	 * 
	 * @param i index of the device
	 */
	public void setDevice(int i) {
		try {
			device = MidiSystem.getMidiDevice( midiDeviceInfo[i] );
		    device.open();
		    transmitter.setReceiver(device.getReceiver());
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public Sequencer getSequencer() {
		return sequencer;
	}
}