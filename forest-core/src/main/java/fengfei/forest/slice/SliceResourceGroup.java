package fengfei.forest.slice;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 13-11-6
 * @Time: 上午9:37
 */
public class SliceResourceGroup<Key, R extends SliceResource> {
    private Long sliceId;
    private List<Key> keys = new ArrayList<>();
    private R sliceResource;

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    public void addKey(Key key) {
        this.keys.add(key);
    }

    public R getSliceResource() {
        return sliceResource;
    }

    public void setSliceResource(R sliceResource) {
        this.sliceResource = sliceResource;
    }

    @Override
    public String toString() {
        return "SliceResourceGroup{" +
                "sliceId=" + sliceId +
                ", keys=" + keys +
                ", sliceResource=" + sliceResource +
                '}';
    }
}
