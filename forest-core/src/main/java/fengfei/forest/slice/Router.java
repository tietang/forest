package fengfei.forest.slice;

public interface Router<Key, R extends SliceResource> extends Navigator<Key, R>, SliceRegistry<Key>,
        SliceResourceRegistry {

    void setOverflowType(OverflowType overflowType);

    OverflowType getOverflowType();

    public void setPlotter(Plotter plotter);

    void setEqualizer(Equalizer<Key> equalizer);

    Detector getDetector();

    void setDetector(Detector detector);


}
