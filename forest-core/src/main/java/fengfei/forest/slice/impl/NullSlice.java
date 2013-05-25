package fengfei.forest.slice.impl;

import java.util.List;
import java.util.Map;

import fengfei.forest.slice.Failover;
import fengfei.forest.slice.Plotter;
import fengfei.forest.slice.Router;
import fengfei.forest.slice.Slice;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;

public class NullSlice<Key> implements Slice<Key> {

	@Override
	public Router<Key> getChildRouter() {

		return null;
	}

	@Override
	public void setChildRouter(Router<Key> childRouter) {

	}

	@Override
	public void setPlotter(Plotter plotter) {

	}

	@Override
	public void setParams(Map<String, String> extraInfo) {

	}

	@Override
	public void addParams(String key, String value) {

	}

	@Override
	public Map<String, String> getParams() {

		return null;
	}

	@Override
	public void addParams(Map<String, String> extraInfo) {

	}

	@Override
	public void add(SliceResource resource) {

	}

	@Override
	public void remove(SliceResource resource) {

	}

	@Override
	public SliceResource get(long seed, Function function) {

		return null;
	}

	@Override
	public SliceResource getAny(long seed) {

		return null;
	}

	@Override
	public Long getSliceId() {

		return null;
	}

	@Override
	public void setSliceId(Long sliceId) {

	}

	@Override
	public String getAlias() {
		return null;
	}

	@Override
	public void setAlias(String alias) {

	}

	@Override
	public Failover getFailover() {
		return null;
	}

	@Override
	public List<SliceResource> getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceTribe getReadTribe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceTribe getTribe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceTribe getWriteTribe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean fail(SliceResource resource) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean recover(SliceResource resource) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Long getRegisteredId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRegisteredId(Long registeredId) {
		// TODO Auto-generated method stub

	}

}
