package fengfei.forest.slice;

public interface Failover {

	boolean fail(Resource resource);

	boolean recover(Resource resource);
}
