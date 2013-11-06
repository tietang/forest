package fengfei.forest.slice;

import java.util.List;

/**
 * @Date: 13-11-6
 * @Time: 上午9:37
 */
public class SliceResourceGroup<Key> {
    private List<Key> keys;
    private SliceResource sliceResource;

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    public SliceResource getSliceResource() {
        return sliceResource;
    }

    public void setSliceResource(SliceResource sliceResource) {
        this.sliceResource = sliceResource;
    }
}
