package fengfei.forest.slice.config;

import java.io.InputStream;

import fengfei.forest.slice.exception.ErrorResourceConfigException;

public interface SliceReader<T> {

    T read(String path) throws ErrorResourceConfigException;

    T read(InputStream in) throws ErrorResourceConfigException;

//    Config readConfig(String path) throws ErrorResourceConfigException;
}