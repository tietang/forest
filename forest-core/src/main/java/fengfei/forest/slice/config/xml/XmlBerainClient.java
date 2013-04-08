package fengfei.forest.slice.config.xml;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import fengfei.berain.client.BerainClient;
import fengfei.berain.client.BerainEntry;
import fengfei.berain.client.BerainWatchedEvent;
import fengfei.berain.client.EventType;
import fengfei.berain.client.Wather;

public class XmlBerainClient implements BerainClient {
	Document document = null;
	XPath xpath = null;

	public XmlBerainClient(String xmlFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(xmlFile);
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean update(String path, String value) throws Exception {
		XPathExpression expr = xpath
				.compile("//pre:book[pre:author='Neal Stephenson']/pre:title/text()");
		return false;
	}

	@Override
	public boolean create(String path, String value) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(String path) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean copy(String originalPath, String newPath) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<BerainEntry> nextChildren(String parentPath) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BerainEntry getFull(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String path) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Deprecated
	@Override
	public void removeWatchable(String path, int type) throws Exception {
		throw new UnsupportedOperationException("don't implemtments.");
	}

	@Deprecated
	@Override
	public void addWatchable(String path, EventType type, Wather wather)
			throws Exception {
		throw new UnsupportedOperationException("don't implemtments.");
	}

	@Deprecated
	@Override
	public Map<String, List<BerainWatchedEvent>> listChangedNodes()
			throws Exception {
		throw new UnsupportedOperationException("don't implemtments.");
	}

	@Deprecated
	@Override
	public void removeAllListener() throws Exception {
		throw new UnsupportedOperationException("don't implemtments.");
	}

	@Deprecated
	@Override
	public void addChildrenChangedWatcher(String arg0, Wather arg1)
			throws Exception {
		throw new UnsupportedOperationException("don't implemtments.");

	}

	@Deprecated
	@Override
	public void addNodeChangedWatcher(String arg0, Wather arg1)
			throws Exception {
		throw new UnsupportedOperationException("don't implemtments.");

	}

	@Override
	public void login(String username, String password) throws Exception {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> nextChildrenPath(String parentPath) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
