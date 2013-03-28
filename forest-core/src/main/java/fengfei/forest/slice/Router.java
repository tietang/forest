package fengfei.forest.slice;

public interface Router<Key>
		extends
		Navigator<Key>,
		SliceRegistry<Key>,
		SliceResourceRegistry {

	void setOverflowType(OverflowType overflowType);

	void setSelectType(SelectType selectType);

	void setEqualizer(Equalizer<Key> equalizer);
}
