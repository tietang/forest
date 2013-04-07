package fengfei.forest.slice;

public interface Router<Key> extends Navigator<Key>, SliceRegistry<Key>,
		SliceResourceRegistry {

	void setOverflowType(OverflowType overflowType);

	OverflowType getOverflowType();

	public void setPlotter(Plotter plotter);

	void setEqualizer(Equalizer<Key> equalizer);

	Detector getDetector();

	void setDetector(Detector detector);

 
}
