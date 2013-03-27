package fengfei.forest.slice;

public interface Failover {

	boolean fail(SliceResource resource);

	boolean recover(SliceResource resource);
}
