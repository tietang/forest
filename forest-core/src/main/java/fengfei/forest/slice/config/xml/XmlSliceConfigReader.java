package fengfei.forest.slice.config.xml;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.berain.client.BerainEntry;
import fengfei.forest.slice.config.Config;
import fengfei.forest.slice.config.SliceConfigReader;

public class XmlSliceConfigReader extends SliceConfigReader {
	public static final String SliceIdKey = "id";
	static Logger log = LoggerFactory.getLogger(XmlSliceConfigReader.class);

	public XmlSliceConfigReader(String xmlFile) {
		super();
		source = new XmlConfigSource(xmlFile);
		namespace = source.getNamespace();
	}

	public XmlSliceConfigReader(InputStream in) {
		source = new XmlConfigSource(in);
		namespace = source.getNamespace();
	}

	protected long getSliceId(BerainEntry sliceEntry,
			Map<String, String> children) {

		return Long.parseLong(children.get(SliceIdKey));
	}

	public static void main(String[] args) {
		XmlSliceConfigReader reader = new XmlSliceConfigReader("cp:config.xml");

		Config config = reader.read("/root/main");
		System.out.println(config);
	}

}
