/*
 * Example music plugin
 *
 * Ideas for music operations:
 *
 * see https://docs.oracle.com/javase/tutorial/sound/overview-MIDI.html and https://docs.oracle.com/javase/tutorial/sound/accessing-MIDI.html and follow links
 *
*/

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import uk.ac.rhul.cs.csle.art.term.AbstractValuePlugin;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTValuePluginMusic extends AbstractValuePlugin {
  @Override
  public String description() {
    return "Adrian's example music plugin";
  }

  @Override
  public Object plugin(Object... args) {
    switch ((String) args[0]) {
    case "init":
      initialise();
      break;

    case "play":
      for (int i = 1; i < args.length; i++)
        playChord((String) args[i], Chord.MINOR7);
      break;

    default:
      Util.fatal("Unknown plugin operation: " + args[0]);
    }

    return __done;
  }

  /* ADrian's mini music player support below this line */
  private Synthesizer synthesizer;
  private MidiChannel[] channels;
  private int defaultOctave = 5;
  private int defaultVelocity = 50;
  private int bpm;
  private double bps;
  private double beatPeriod;
  private double beatRatio = 0.9;
  private int beatSoundDelay = (int) (1000.0 * beatRatio / bps);
  private int beatSilenceDelay = (int) (1000.0 * (1.0 - beatRatio) / bps);

  void initialise() {
    try {
      System.out.print(MidiSystem.getMidiDeviceInfo());
      synthesizer = MidiSystem.getSynthesizer();
      synthesizer.open();
      channels = synthesizer.getChannels();
    } catch (Exception e) {
      Util.fatal("Java MIDI exception: " + e.getMessage());
    }

    setBeatRatio(0.9);
    setBpm(100);
    setDefaultVelocity(50);
    rest(2);
  }

  public int getDefaultOctave() {
    return defaultOctave;
  }

  public void setDefaultOctave(int defaultOctave) {
    this.defaultOctave = defaultOctave;
  }

  public int getDefaultVelocity() {
    return defaultVelocity;
  }

  public void setDefaultVelocity(int defaultVelocity) {
    this.defaultVelocity = defaultVelocity;
  }

  public int getBpm() {
    return bpm;
  }

  public void setBpm(int bpm) {
    this.bpm = bpm;
    bps = bpm / 60.0;
    beatPeriod = 1000.0 / bps;
    beatSoundDelay = (int) (beatRatio * beatPeriod);
    beatSilenceDelay = (int) ((1.0 - beatRatio) * beatPeriod);
  }

  public void setBeatRatio(double beatRatio) {
    this.beatRatio = beatRatio;
    beatSoundDelay = (int) (beatRatio * beatPeriod);
    beatSilenceDelay = (int) ((1.0 - beatRatio) * beatPeriod);
  }

  int noteNameToMidiKey(String n, int octave) {
 // @formatter:off
 int key = octave * 12 +
        ( n.equals("C") ? 0
        : n.equals("C#") ? 1
        : n.equals("Db") ? 1
        : n.equals("D") ? 2
        : n.equals("D#") ? 3
        : n.equals("Eb") ? 3
        : n.equals("E") ? 4
        : n.equals("F") ? 5
        : n.equals("F#") ? 6
        : n.equals("Gb") ? 6
        : n.equals("G") ? 7
        : n.equals("G#") ? 8
        : n.equals("Ab") ? 8
        : n.equals("A") ? 9
        : n.equals("A#") ? 10
        : n.equals("Bb") ? 10
        : n.equals("B") ? 11
        : -1);
 // @formatter:on

    if (key < 0 || key > 127) {
      System.err.println("miniMusicPlayer exception: attempt to access out of range MIDI key " + n + octave);
      System.exit(1);
    }
    return key;
  }

  // Silence
  public void rest(int beats) {
    try {
      Thread.sleep((long) (beats * beatPeriod));
    } catch (InterruptedException e) {
      /* ignore interruptedException */ }
  }

  // Single notes
  void play(int k) {
    try {
      channels[0].noteOn(k, defaultVelocity);
      Thread.sleep(beatSoundDelay);
      channels[0].noteOn(k, 0);
      Thread.sleep(beatSilenceDelay);
    } catch (InterruptedException e) {
      /* ignore interruptedException */ }
  }

  void play(String n) {
    play(noteNameToMidiKey(n, defaultOctave));
  }

  void play(String n, int octave) {
    play(noteNameToMidiKey(n, octave));
  }

  // Arrays of notes
  void play(int[] k) {
    try {
      for (int i = 0; i < k.length; i++)
        channels[1].noteOn(k[i], defaultVelocity);
      Thread.sleep(beatSoundDelay);
      for (int i = 0; i < k.length; i++)
        channels[1].noteOn(k[i], 0);
      Thread.sleep(beatSilenceDelay);
    } catch (InterruptedException e) {
      /* ignore interruptedException */ }
  }

  private void playSequentially(int[] k) {
    try {
      for (int i = 0; i < k.length; i++) {
        channels[i].noteOn(k[i], defaultVelocity);
        Thread.sleep(beatSoundDelay);
        channels[i].noteOn(k[i], 0);
        Thread.sleep(beatSilenceDelay);
      }
    } catch (InterruptedException e) {
      /* ignore interruptedException */ }
  }

  // Scales
  void playScale(String n, Scale s) {
    playScale(noteNameToMidiKey(n, defaultOctave), s);
  }

  void playScale(String n, int octave, Scale s) {
    playScale(noteNameToMidiKey(n, octave), s);
  }

  void playScale(int k, Scale s) {
    int[] keys;
    switch (s) {
    case CHROMATIC:
      keys = new int[] { k, k + 1, k + 2, k + 3, k + 4, k + 5, k + 6, k + 7, k + 8, k + 9, k + 10, k + 11, k + 12 };
      break;

    case MAJOR: // TTSTTTS
      keys = new int[] { k, k + 2, k + 4, k + 5, k + 7, k + 9, k + 11, k + 12 };
      break;

    case MINOR_NATURAL: // TSTTSTT
      keys = new int[] { k, k + 2, k + 3, k + 5, k + 7, k + 8, k + 10, k + 12 };
      break;
    case MINOR_HARMONIC: // TSTTS3S
      keys = new int[] { k, k + 2, k + 3, k + 5, k + 7, k + 8, k + 11, k + 12 };
      break;
    case MINOR_MELODIC_ASCENDING: // TSTTS3S - harmonic with with sixth sharpened
      keys = new int[] { k, k + 2, k + 3, k + 5, k + 7, k + 9, k + 11, k + 12 };
      break;
    case MINOR_MELODIC_DESCENDING: // TSTTS3S - harmonic with seventh flattened making it the same as the natural minor
      keys = new int[] { k + 12, k + 10, k + 8, k + 7, k + 5, k + 3, k + 2, k };
      break;

    default:
      keys = new int[] { k };
      break;
    }
    playSequentially(keys);
  }

  // Programmed chords
  void playChord(String n, Chord type) {
    playChord(noteNameToMidiKey(n, defaultOctave), type);
  }

  void playChord(String n, int octave, Chord type) {
    playChord(noteNameToMidiKey(n, octave), type);
  }

  private void playChord(int k, Chord type) {
    int[] keys;
    switch (type) {
    case MAJOR:
      keys = new int[] { k, k + 4, k + 7 };
      break;
    case MAJOR7:
      keys = new int[] { k, k + 4, k + 7, k + 11 };
      break;
    case MINOR:
      keys = new int[] { k, k + 3, k + 7 };
      break;
    case MINOR7:
      keys = new int[] { k, k + 4, k + 7 };
      break;
    default:
      keys = new int[] { k };
      break;
    }
    play(keys);
  }

  public void tune() {
    int base = 47;
    play(base + 14);
    play(base + 12);
    play(base + 11);
    play(base + 7);
    play(base + 5);
    play(base + 7);
    play(base + 2);
    rest(2);
  }

  public void tuneChordMajor() {
    int base = noteNameToMidiKey("C", 5);
    playChord(base + 14, Chord.MAJOR);
    playChord(base + 12, Chord.MAJOR);
    playChord(base + 11, Chord.MAJOR);
    playChord(base + 7, Chord.MAJOR);
    playChord(base + 5, Chord.MAJOR);
    playChord(base + 7, Chord.MAJOR);
    playChord(base + 2, Chord.MAJOR);
  }

  public void tuneChordMinor() {
    int base = noteNameToMidiKey("C", 5);
    playChord(base + 14, Chord.MINOR);
    playChord(base + 12, Chord.MINOR);
    playChord(base + 11, Chord.MINOR);
    playChord(base + 7, Chord.MINOR);
    playChord(base + 5, Chord.MINOR);
    playChord(base + 7, Chord.MINOR);
    playChord(base + 2, Chord.MINOR);
  }

  void close() {
    rest(3);
    synthesizer.close();
  }

  public enum Scale {
    CHROMATIC, MAJOR, MINOR_NATURAL, MINOR_HARMONIC, MINOR_MELODIC_ASCENDING, MINOR_MELODIC_DESCENDING
  }

  public enum Chord {
    NONE, MAJOR, MINOR, MAJOR7, MINOR7
  }
}