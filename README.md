# generative-jazz-chords
Markovian Generative AI Jazz Harmonies

Executable GUI:
1. Download jazz-chords-lib+gui-1.0.0.jar
2. Run in command line: java -jar .\jazz-chords-lib+gui-1.0.0.jar

![C_AABA](https://github.com/anraposo/generative-jazz-chords/assets/117098145/d922b378-76cb-4379-ae22-9016dae749fc)

Basic instructions:
1. Choose a Key
2. Chose a song Structure
3. Hit the "Generate Harmony" button
4. Choose the "Output" (if other that default internal piano sound)
5. Hit the "Play" button
6. Enjoy!

Other instructions:
- Hit the "Stop" button to stop playing
- Hit the "Generate Button" at anytime to generate a new harmony
- Hit the "Export to MusicXML" to get a MuseScore file

Connect to virtual synth:
1. Install loopMIDI
2. Create a MIDI virtual cable
3. Select the virtual cable as the input in the virtual synth
4. Run the JAR: java -jar .\jazz-chords-lib+gui-1.0.0.jar
5. Choose the MIDI virtual cable in the output select box
5. Hit the "Play" button
6. Enjoy!
