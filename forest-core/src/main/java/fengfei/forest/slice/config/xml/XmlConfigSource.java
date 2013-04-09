package fengfei.forest.slice.config.xml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fengfei.berain.client.BerainEntry;
import fengfei.forest.slice.config.ConfigSource;
import fengfei.forest.slice.utils.ResourcesUtils;

public class XmlConfigSource implements ConfigSource {
	static final String AttributeValueKey = "value";
	String namespace;
	Document document = null;
	XPath xpath = null;

	public XmlConfigSource(String xmlFile) {
		try {
			if (xmlFile.startsWith("classpath:") || xmlFile.startsWith("cp:")) {
				xmlFile = xmlFile.replace("classpath:", "").replace("cp:", "");
				InputStream in = ResourcesUtils.getResourceAsStream(xmlFile);
				init(in);
			} else {
				init(new FileInputStream(xmlFile));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public XmlConfigSource(InputStream in) {
		try {
			init(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init(InputStream in) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(in);

		XPathFactory xPathFactory = XPathFactory.newInstance();
		xpath = xPathFactory.newXPath();

	}

	public static void main(String[] args) throws Exception {
		XmlConfigSource source = new XmlConfigSource("cp:test.xml");
		source.get("/inventory/book");
		System.out.println(source.exists("/dfd"));
		System.out.println(source.exists("/"));
		System.out.println(source.listChildren("/inventory"));
		System.out.println(source.listChildren("/"));
		System.out.println(source.children("/"));
		System.out.println(source.children("/inventory"));
		// System.out.println(source.exists(""));
	}

	@Override
	public String get(String path) throws Exception {
		XPathExpression expr = xpath.compile(path);
		Object result = expr.evaluate(document, XPathConstants.NODE);
		Node node = (Node) result;
		if (node == null) {
			return null;
		}
		String value = node.getNodeValue();
		if (value == null) {
			Node attr = node.getAttributes().getNamedItem(AttributeValueKey);
			value = attr == null ? value : attr.getNodeValue();
		}
		// System.out.println(value);
		// System.out.println(nodes.getLength());
		// for (int i = 0; i < nodes.getLength(); i++) {
		// Node node = nodes.item(i);
		// System.out.println(node.getNodeName());
		// System.out.println(node.getAttributes().getNamedItem("year").getNodeValue());
		// // System.out.println(nodes.item(i).getNodeValue());
		// }
		return value;
	}

	public static String getKey(String path) {
		String[] ps = path.split("/");
		return ps[ps.length - 1];
	}

	@Override
	public BerainEntry getFull(String path) throws Exception {
		XPathExpression expr = xpath.compile(path);
		Object result = expr.evaluate(document, XPathConstants.NODE);
		Node node = (Node) result;
		String value = getNodeValue(node);
		if (value == null) {
			return null;
		}
		String key = getKey(path);
		BerainEntry model = new BerainEntry();
		model.key = key;
		model.path = path;
		model.value = value;
		return model;

	}

	@Override
	public boolean exists(String path) throws Exception {
		XPathExpression expr = xpath.compile(path);
		Object result = expr.evaluate(document, XPathConstants.NODE);
		return result != null;
	}

	private String getNodeValue(Node node) {
		String value = node.getNodeValue();
		if (value == null) {
			NamedNodeMap attrs = node.getAttributes();
			if (attrs != null) {
				Node attr = attrs.getNamedItem(AttributeValueKey);
				value = attr == null ? value : attr.getNodeValue();
			}
		}
		return value;
	}

	@Override
	public List<BerainEntry> children(String parentPath) throws Exception {
		XPathExpression expr = xpath.compile(parentPath);
		Object result = expr.evaluate(document, XPathConstants.NODE);
		Node node = (Node) result;
		if (node == null) {
			return new ArrayList<>();
		}
		NodeList nodes = node.getChildNodes();
		System.out.println("len:" + nodes.getLength());
		List<BerainEntry> entries = new ArrayList<>();
		System.out.println(nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			Node nd = nodes.item(i);
			if (nd.getNodeType() == Node.ELEMENT_NODE) {
				String path = ("/".equals(parentPath) ? "" : parentPath) + "/"
						+ nd.getNodeName();
				BerainEntry model = new BerainEntry();
				model.key = nd.getNodeName();
				model.path = path;
				model.value = getNodeValue(node);

				entries.add(model);
			}
		}
		return entries;
	}

	@Override
	public List<String> listChildren(String parentPath) throws Exception {
		XPathExpression expr = xpath.compile(parentPath);
		Object result = expr.evaluate(document, XPathConstants.NODE);
		Node node = (Node) result;
		if (node == null) {
			return new ArrayList<>();
		}
		NodeList nodes = node.getChildNodes();
		List<String> entries = new ArrayList<>();
		System.out.println(nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			Node nd = nodes.item(i);
			if (nd.getNodeType() == Node.ELEMENT_NODE) {
				System.out.printf("node: %s %s \n", nd.getNodeName(),
						nd.getNodeType());
				String path = ("/".equals(parentPath) ? "" : parentPath) + "/"
						+ nd.getNodeName();

				entries.add(path);
			}

		}
		return entries;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Deprecated
	@Override
	public void addMonitor(String path, Monitor monitor) throws Exception {
		throw new UnsupportedOperationException("Don't implements.");
	}

}
