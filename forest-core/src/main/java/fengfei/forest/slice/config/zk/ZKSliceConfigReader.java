package fengfei.forest.slice.config.zk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.config.SliceConfigReader;

public class ZKSliceConfigReader extends SliceConfigReader {

	static Logger log = LoggerFactory.getLogger(ZKSliceConfigReader.class);

	public ZKSliceConfigReader() {
		this(null);
	}

	public ZKSliceConfigReader(String zkProperties) {
		super();
		source = new ZKConfigSource(zkProperties);
		namespace = source.getNamespace();
	}

	public static void main(String[] args) {
		ZKSliceConfigReader reader = new ZKSliceConfigReader();
		Config config = reader.read("/main");
		System.out.println(config);
	}

}
