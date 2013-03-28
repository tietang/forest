package fengfei.forest.slice.server.pool;

import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.server.ServerResource;

public class PoolableServerResource<D> extends ServerResource {

	private PooledSource<D> source;

	public PoolableServerResource(SliceResource resource) {
		super(resource);
	}

	void setSource(PooledSource<D> source) {
		this.source = source;
	}

	public PooledSource<D> getSource() {
		return source;
	}

	@Override
	public String toString() {
		return "PoolableServerResource [sliceId=" + sliceId + ", alias=" + alias + ", function=" + function + ", resource=" + resource + "]";
	}
}
