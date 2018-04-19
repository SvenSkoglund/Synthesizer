
import com.jsyn.unitgen.FilterStateVariable;
import com.jsyn.unitgen.SquareOscillator;

import jsyn.JSyn;
import jsyn.unitgen.LineOut;

/**
 * Play a tone using a JSyn oscillator.
 * 
 * @author Phil Burk (C) 2010 Mobileer Inc
 */
public class PlayTone {
	com.jsyn.Synthesizer synth;
	FilterStateVariable myFilter;
	SquareOscillator osc;
	LineOut lineOut;

	private void test() {

		// Create a context for the synthesizer.
		synth = JSyn.createSynthesizer();

		// Start synthesizer using default stereo output at 44100 Hz.
		synth.start();

		// Add a tone generator.
		synth.add(osc = new SquareOscillator());
		// Add a stereo audio output unit.
		synth.add(lineOut = new LineOut());
		synth.add( myFilter = new FilterStateVariable() );
		myFilter.lowPass.connect(lineOut.input);
		// Connect the oscillator to both channels of the output.
		osc.output.connect(0, myFilter.input, 0);
//		osc.output.connect(0, myFilter.input, 1);
		myFilter.output.connect( 0, lineOut.input, 0 ); /* Left side */
		myFilter.output.connect( 0, lineOut.input, 1 );
		// Set the frequency and amplitude for the sine wave.
		osc.frequency.set(345.0);
		osc.amplitude.set(0.6);

		// We only need to start the LineOut. It will pull data from the
		// oscillator.
		lineOut.start();

		System.out.println("You should now be hearing a sine wave. ---------");

		// Sleep while the sound is generated in the background.
		try {
			double time = synth.getCurrentTime();
			System.out.println("time = " + time);
			// Sleep for a few seconds.
			double i = 0;
			int counter = 0;
			int lfo = 550;
			int upDown = 1;
			while (true) {
				counter = 50;
				i = 0;
				while (upDown > 0) {

					counter++;
					Thread.sleep(1);
					osc.frequency.set(100 + lfo + i++);
					myFilter.frequency.set(osc.frequency.get());

					if (counter % lfo == 0) {
						upDown = -1;
					}
				}
				
				counter = 50;
				i = 0;
				while (upDown < 0) {

					counter++;
					Thread.sleep(1);
					osc.frequency.set(100 + lfo - i++);
					myFilter.frequency.set(osc.frequency.get());

					if (counter % lfo == 0) {
						upDown = 1;
					}
				}
			} 
		}
		
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Stop playing. -------------------");
		// Stop everything.
		synth.stop();
	}

	public static void main(String[] args) {
		new PlayTone().test();
	}
}