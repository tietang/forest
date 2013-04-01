package fengfei.forest.slice.server.pool;

import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.server.ServerResource;

public class PoolableServerResource<D> extends ServerResource implements
		PooledSource<D> {

	private PooledSource<D> pooledSource;

	public PoolableServerResource(SliceResource resource) {
		super(resource);
	}

	void setPooledSource(PooledSource<D> source) {
		this.pooledSource = source;
	}

	public PooledSource<D> getPooledSource() {
		return pooledSource;
	}

 
	@Override
	public D getSource() throws PoolableException {

		return pooledSource.getSource();
	}

	@Override
	public void close(D d) throws PoolableException {
		pooledSource.close(d);

	}

	@Override
	public void close() throws PoolableException {
		pooledSource.close();
	}

	@Override
	public String toString() {
		return "PoolableServerResource [sliceId=" + sliceId + ", alias="
				+ alias + ", function=" + function + ", resource=" + resource
				+ "]";
	}

}
