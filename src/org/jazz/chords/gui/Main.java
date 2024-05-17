package org.jazz.chords.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jazz.chords.lib.HarmonyGenerator;
import org.jazz.chords.lib.Section;
import org.jazz.chords.lib.Song;
import org.json.simple.parser.ParseException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.imageio.ImageIO;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JSlider;
import java.awt.Color;
import java.awt.FlowLayout;

/**
 * 
 * @author Adriano Raposo
 *
 */
public class Main extends JFrame {
	
	// the music generator
	private static HarmonyGenerator generator = new HarmonyGenerator();
	
	// the generated song
	Song song;
	
	// the main panel chords labels
	ArrayList<JLabel> chordsLabels = new ArrayList<JLabel>();
	
	// variables used to highlight the chords playing
	PlayChordListener chordListener = new PlayChordListener();
	int currentChordLabel = 0;
	
	// the midi manager
	MidiManager midiManager = new MidiManager();

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		
		generator.loadCorpus();
		generator.train();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {

        InputStream inputStream = getClass().getResourceAsStream("icon.png");
		try {
			Image icon = new ImageIcon(ImageIO.read(inputStream)).getImage();
	        setIconImage(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		setResizable(false);
		setTitle("Jazz Harmony Generator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setForeground(Color.DARK_GRAY);
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel labelOutput = new JLabel("Output");
		labelOutput.setForeground(Color.WHITE);
		labelOutput.setFont(new Font("Verdana", Font.PLAIN, 11));
		labelOutput.setHorizontalAlignment(SwingConstants.RIGHT);
		labelOutput.setBounds(419, 417, 51, 14);
		contentPane.add(labelOutput);
		
		JComboBox<String> comboBoxOutput = new JComboBox<String>(MidiManager.getMidiOutPorts());
		comboBoxOutput.setFont(new Font("Verdana", Font.PLAIN, 11));
		comboBoxOutput.setBounds(481, 413, 293, 22);
		comboBoxOutput.addActionListener(e ->
			midiManager.setDevice( comboBoxOutput.getSelectedIndex() )
        );
		contentPane.add(comboBoxOutput);
		
		JLabel labelKey = new JLabel("Key");
		labelKey.setForeground(Color.WHITE);
		labelKey.setFont(new Font("Verdana", Font.PLAIN, 11));
		labelKey.setHorizontalAlignment(SwingConstants.LEFT);
		labelKey.setBounds(294, 11, 46, 14);
		contentPane.add(labelKey);

		String[] keyOptions = new String[generator.getExistingKeys().size()];
		keyOptions[0] = "Random";
		for (int i = 1; i < generator.getExistingKeys().size(); i++ ) {
			keyOptions[i] = generator.getExistingKeys().get(i);
		}
		JComboBox<String> comboBoxKey = new JComboBox<String>(keyOptions);
		comboBoxKey.setFont(new Font("Verdana", Font.PLAIN, 11));
		comboBoxKey.setBounds(294, 27, 168, 22);
		contentPane.add(comboBoxKey);
		
		JLabel labelStructure = new JLabel("Structure");
		labelStructure.setForeground(Color.WHITE);
		labelStructure.setFont(new Font("Verdana", Font.PLAIN, 11));
		labelStructure.setHorizontalAlignment(SwingConstants.LEFT);
		labelStructure.setBounds(472, 11, 146, 14);
		contentPane.add(labelStructure);
		
		String[] structureOptions = new String[generator.getExistingStructures().size()];
		structureOptions[0] = "Random";
		for (int i = 1; i < generator.getExistingStructures().size(); i++ ) {
			structureOptions[i] = generator.getExistingStructures().get(i);
		}
		JComboBox<String> comboBoxStructure = new JComboBox<String>(structureOptions);
		comboBoxStructure.setFont(new Font("Verdana", Font.PLAIN, 11));
		comboBoxStructure.setBounds(472, 27, 146, 22);
		contentPane.add(comboBoxStructure);
		
		JLabel labelSongKey = new JLabel("");
		labelSongKey.setForeground(Color.WHITE);
		labelSongKey.setHorizontalAlignment(SwingConstants.LEFT);
		labelSongKey.setFont(new Font("Arial Black", Font.PLAIN, 16));
		labelSongKey.setBounds(69, 11, 67, 22);
		contentPane.add(labelSongKey);

		JLabel labelSongStructure = new JLabel((String) null);
		labelSongStructure.setHorizontalAlignment(SwingConstants.LEFT);
		labelSongStructure.setForeground(Color.WHITE);
		labelSongStructure.setFont(new Font("Arial Black", Font.PLAIN, 12));
		labelSongStructure.setBounds(69, 32, 67, 22);
		contentPane.add(labelSongStructure);
		
		JTextArea textAreaSections = new JTextArea();
		textAreaSections.setForeground(Color.DARK_GRAY);
		textAreaSections.setBackground(Color.LIGHT_GRAY);
		textAreaSections.setEditable(false);
		textAreaSections.setWrapStyleWord(true);
		textAreaSections.setFont(new Font("Verdana", Font.PLAIN, 10));
		textAreaSections.setBounds(10, 60, 764, 60);
		contentPane.add(textAreaSections);
		
		JPanel panelChords = new JPanel();
		panelChords.setForeground(Color.WHITE);
		panelChords.setBackground(Color.DARK_GRAY);
		panelChords.setBounds(10, 125, 764, 240);
		contentPane.add(panelChords);
		
		JButton buttonGenerate = new JButton("Generate Harmony");
		buttonGenerate.setForeground(Color.DARK_GRAY);
		buttonGenerate.setBackground(Color.LIGHT_GRAY);
		buttonGenerate.setFont(new Font("Verdana", Font.PLAIN, 11));
		buttonGenerate.setBounds(628, 27, 146, 22);
		buttonGenerate.addActionListener( e ->
		{
			midiManager.stopSequencer();
			if ( comboBoxKey.getSelectedItem().toString().equals("Random") && 
					comboBoxStructure.getSelectedItem().toString().equals("Random")  ) {
				// random key + random structure
				song = generator.generateRandomSong(); 
			} else if ( comboBoxKey.getSelectedItem().toString().equals("Random") ) {
				// random key
				song = generator.generateSongWithStructure(comboBoxStructure.getSelectedItem().toString()); 
			} else if ( comboBoxStructure.getSelectedItem().toString().equals("Random") ) {
				// random structure
				song = generator.generateSongInKey(comboBoxKey.getSelectedItem().toString()); 
			} else {
				// specific key and structure
				song = generator.generateSong( comboBoxKey.getSelectedItem().toString(),
						comboBoxStructure.getSelectedItem().toString());
			}
			labelSongKey.setText( song.getKey() );
			labelSongStructure.setText( song.getStructure() );
			textAreaSections.setText("");
			for ( Section s : song.getSections() ) {
				textAreaSections.append( s.toString() );
			}
            updateCompleteHarmonyPanel(panelChords, chordsLabels); 
		}
		);
		contentPane.add(buttonGenerate);

		JLabel labelTempo = new JLabel( String.valueOf(MidiManager.BPM_DEFAULT) + "bpm" );
		labelTempo.setForeground(Color.WHITE);
		labelTempo.setHorizontalAlignment(SwingConstants.RIGHT);
		labelTempo.setFont(new Font("Verdana", Font.PLAIN, 11));
		labelTempo.setBounds(409, 376, 62, 22);
		contentPane.add(labelTempo);
		
		JSlider slider = new JSlider(MidiManager.BPM_MIN, MidiManager.BPM_MAX, MidiManager.BPM_DEFAULT);
		slider.setBackground(Color.GRAY);
		slider.setForeground(Color.LIGHT_GRAY);
		slider.setBounds(481, 376, 123, 26);
		slider.addChangeListener(e ->
		{
			midiManager.setTempoBPM(slider.getValue());
			labelTempo.setText( String.valueOf(slider.getValue()) + "bpm" );
		});
		contentPane.add(slider);
		
		JButton buttonPlay = new JButton("Play");
		buttonPlay.setForeground(Color.DARK_GRAY);
		buttonPlay.setBackground(Color.LIGHT_GRAY);
		buttonPlay.setFont(new Font("Verdana", Font.PLAIN, 11));
		buttonPlay.setBounds(614, 376, 75, 23);
		buttonPlay.addActionListener( e ->
		{
			// to highlight the chord currently playing
			updateCompleteHarmonyPanel(panelChords, chordsLabels); 
	        try {
				Transmitter transmitter = midiManager.getSequencer().getTransmitter();
				transmitter.setReceiver(chordListener);
			} catch (MidiUnavailableException e1) {
				e1.printStackTrace();
			}
	        // play the generated song
			if ( song != null ) {
				midiManager.setHarmony( song.getCompleteHarmony() );
				midiManager.play();
			}
		});
		contentPane.add(buttonPlay);
		
		JButton buttonCopy = new JButton("Copy to clipboard");
		buttonCopy.setForeground(Color.DARK_GRAY);
		buttonCopy.setBackground(Color.LIGHT_GRAY);
		buttonCopy.setFont(new Font("Verdana", Font.PLAIN, 11));
		buttonCopy.setBounds(10, 376, 168, 23);
		buttonCopy.addActionListener( e ->
		{
			if ( song != null ) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection( song.getCompleteHarmony() ), null);
				JOptionPane.showMessageDialog(null, "Harmony copied to the clipboard", "Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		contentPane.add(buttonCopy);
		
		JButton buttonStop = new JButton("Stop");
		buttonStop.setForeground(Color.DARK_GRAY);
		buttonStop.setBackground(Color.LIGHT_GRAY);
		buttonStop.setFont(new Font("Verdana", Font.PLAIN, 11));
		buttonStop.setBounds(699, 376, 75, 23);
		buttonStop.addActionListener( e ->
			midiManager.stopSequencer()
		);
		contentPane.add(buttonStop);
		
		JButton buttonExport = new JButton("Export to MusicXML ...");
		buttonExport.setForeground(Color.DARK_GRAY);
		buttonExport.setFont(new Font("Verdana", Font.PLAIN, 11));
		buttonExport.setBackground(Color.LIGHT_GRAY);
		buttonExport.setBounds(10, 413, 168, 23);
		buttonExport.addActionListener( e ->
		{
			if ( song != null ) {
				JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setDialogTitle("Export to MusicXML");
		        int userSelection = fileChooser.showSaveDialog(this);
		        if ( userSelection == JFileChooser.APPROVE_OPTION) {
			        File fileToSave = fileChooser.getSelectedFile();
					String filePath = fileToSave.getAbsolutePath();
                    if (!filePath.toLowerCase().endsWith(".musicxml")) {
                        filePath += ".musicxml";
                    }
					song.exportToMusicXML(filePath);
					JOptionPane.showMessageDialog(null, "Harmony exported to MusicXML file", "Info", JOptionPane.INFORMATION_MESSAGE);		
		        }
			}
		});
		contentPane.add(buttonExport);
		
		JLabel labelKey_1 = new JLabel("Key");
		labelKey_1.setHorizontalAlignment(SwingConstants.LEFT);
		labelKey_1.setForeground(Color.WHITE);
		labelKey_1.setFont(new Font("Verdana", Font.PLAIN, 11));
		labelKey_1.setBounds(10, 12, 46, 14);
		contentPane.add(labelKey_1);
		
		JLabel labelStructure_1 = new JLabel("Structure");
		labelStructure_1.setHorizontalAlignment(SwingConstants.LEFT);
		labelStructure_1.setForeground(Color.WHITE);
		labelStructure_1.setFont(new Font("Verdana", Font.PLAIN, 11));
		labelStructure_1.setBounds(10, 35, 146, 14);
		contentPane.add(labelStructure_1);
	}
	
	/**
	 * 
	 */
	private void updateCompleteHarmonyPanel(JPanel panelChords, ArrayList<JLabel> chordsLabels) {
		panelChords.removeAll();
		chordsLabels.clear();
		String[] bars = song.getCompleteHarmony().split("\\|");
		for (int i=0; i<bars.length; i++) {
			panelChords.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
			String[] chords = bars[i].split(",");
			for (int j=0; j<chords.length; j++) {
				JLabel label = new JLabel(chords[j]);
				label.setFont(label.getFont().deriveFont(label.getFont().getSize() + 5.0f));
				label.setForeground(Color.LIGHT_GRAY);
				chordsLabels.add(label);
				panelChords.add(label);
			}	
			if ( i < bars.length-1 ) {
				JLabel separator = new JLabel("|");
				separator.setForeground(Color.LIGHT_GRAY);
				panelChords.add(separator);
			}
		}
		panelChords.revalidate();
		panelChords.repaint();
		currentChordLabel = 0;
	}
	
	/**
	 * Inner class used to highlight the current chord playing
	 */
	class PlayChordListener implements Receiver  {
	    private static final long TIME_WINDOW = 10;
	    private long lastTimestamp = -10;
	    @Override
	    public void send(MidiMessage message, long timeStamp) {
	    	long currentTime = System.currentTimeMillis();
	        if (lastTimestamp != 0) {
	            long timeDifference = currentTime - lastTimestamp;
	            if (timeDifference > TIME_WINDOW && currentChordLabel < chordsLabels.size()) {
	            	if ( currentChordLabel > 0 ) 
	            		chordsLabels.get( currentChordLabel - 1 ).setForeground(Color.LIGHT_GRAY);
	            	chordsLabels.get( currentChordLabel ).setForeground(Color.CYAN);
	                currentChordLabel++;
	            }
	        }
	        lastTimestamp = currentTime;
	    }
		@Override
		public void close() {	
		}
	}
}
