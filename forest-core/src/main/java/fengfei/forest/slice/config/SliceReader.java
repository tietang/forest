package fengfei.forest.slice.config;

import fengfei.forest.slice.exception.ConfigException;

public interface SliceReader<T> {

	T read(String path) throws ConfigException;

}