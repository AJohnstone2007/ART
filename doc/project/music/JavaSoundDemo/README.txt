The classes for the Java Sound demo are contained in the JavaSoundDemo.jar 
file.  Running the demo requires the Java 2 SDK, Standard Edition 1.3.
To run the Java Sound demo :

% java -jar JavaSoundDemo.jar

Although it's not necessary to unpack the JavaSoundDemo.jar file to run 
the demo, you may want to extract its contents if you plan to modify
any of the demo source code. To extract the contents of JavaSoundDemo.jar, 
run this command from the JavaSound directory :

    jar xvf JavaSoundDemo.jar


This JavaSound demo consists of a set of demos housed in one GUI 
framework that uses a JTabbedPane.  You can access different groups of 
demos by clicking the tabs at the top of the pane. There are demo 
groups for Juke Box, Capture & Playback, Midi Synthesizer and Rhythm
Groove Box.

Juke Box :

    A Juke Box for sampled (au, wav, aif) and midi (rmf, midi) sound 
    files.  Features duration progress, seek slider, pan and volume 
    controls.  


Capture & Playback :

    A Capture/Playback sample.  Record audio in different formats
    and then playback the recorded audio.  The captured audio can 
    be saved either as a WAVE, AU or AIFF.  Or load an audio file
    for streaming playback.


Midi Synthesizer :

    Illustrates general MIDI melody instruments and MIDI controllers.
    A piano keyboard represents notes on and off.  Features capture
    and playback of note on and off events. 


Rhythm Groove Box :

    Illustrates how to build a track of short events.  Features
    a tempo dial to increase or descrease the beats per minute.


You can run anyone of the samples in stand-alone mode by issuing a 
commands like this from the JavaSound directory:

        java -cp JavaSoundDemo.jar Juke 
        java -cp JavaSoundDemo.jar CapturePlayback
        java -cp JavaSoundDemo.jar MidiSynth
        java -cp JavaSoundDemo.jar Groove

