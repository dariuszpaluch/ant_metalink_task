package org.ant.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MetalinkTask extends Task {
	private String url;
	private FileSet fileSet;
	private String file;
	private List<File> files = new ArrayList<>();

	public void addFileSet(FileSet fileset){
		this.fileSet = fileset;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public FileSet getFileSet() {
		return fileSet;
	}

	public void setFileSet(FileSet fileSet) {
		this.fileSet = fileSet;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	private void initTask() {
		this.checkUrl();
		this.readAllFiles();
	}
	public void checkUrl() {
		if(this.url == null) {
			this.setUrl(getProject().getProperty("server.files.url"));
		}
		System.out.println("URL: "+ this.url);
	}

	private void addFile(String filePath) {
		File file = new File(filePath);
		this.files.add(file);
	}

	private void readAllFiles() {
		System.out.println("Read all files");
		DirectoryScanner ds = this.fileSet.getDirectoryScanner(getProject());
		String[] includedFiles = ds.getIncludedFiles();
		for(String item : includedFiles) {
			this.addFile(item);
			String filename = item.replace('\\','/');

			System.out.println(filename);
		}
	}

	private void writeDocumentToXmlFile(Document doc) throws TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(this.file));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);
		System.out.println("File saved!");
	}

	private String generateMd5HashFile(File file) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(Files.readAllBytes(Paths.get(file.getPath())));
		return String.format("%032x", new BigInteger(1, md.digest()));
	}

	private String createFileUrl(File file) {
		return this.url + file.getPath().replace('\\','/');
	}

	private Element createFileVerification(File file, Document doc) throws IOException, NoSuchAlgorithmException {
		Element verificationElement = doc.createElement("verification");

		Element hashElement = doc.createElement("hash");
		hashElement.setAttribute("type", "md5");
		hashElement.appendChild(doc.createTextNode(this.generateMd5HashFile(file)));
		verificationElement.appendChild(hashElement);

		return verificationElement;
	}

	private Element createResourcesElement(File file, Document doc) {
		Element resourcesElement = doc.createElement("resources");

		Element urlElement = doc.createElement("url");
		urlElement.appendChild(doc.createTextNode(createFileUrl(file)));
		resourcesElement.appendChild(urlElement);

		return resourcesElement;
	}
	private Element createFileElement(File file, Document doc) throws IOException, NoSuchAlgorithmException {
		Element fileElement = doc.createElement("file");
		fileElement.setAttribute("name", file.getName());

		Element sizeElement = doc.createElement("size");
		sizeElement.appendChild(doc.createTextNode(Long.toString(file.length())));
		fileElement.appendChild(sizeElement);

		fileElement.appendChild(this.createFileVerification(file, doc));


		fileElement.appendChild(this.createResourcesElement(file, doc));

		return fileElement;
	}

	private String getCreatedDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		return dateFormat.format(calendar.getTime());
	}
	private void createMetalinkDoc() throws ParserConfigurationException, TransformerException, IOException, NoSuchAlgorithmException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("metalink");
		rootElement.setAttribute("version", "3.0");
		rootElement.setAttribute("xmlns", "http://metalinker.org");
		doc.appendChild(rootElement);

		Element publishedElement = doc.createElement("published");
		publishedElement.appendChild(doc.createTextNode(this.getCreatedDate()));
		rootElement.appendChild(publishedElement);

		Element filesElement = doc.createElement("files");
		rootElement.appendChild(filesElement);

		for(File file: this.files) {
			filesElement.appendChild(this.createFileElement(file, doc));
		}

		this.writeDocumentToXmlFile(doc);
	}

	public void execute() throws BuildException {
		System.out.println("Start metalink task");
		this.initTask();
		try {
			this.createMetalinkDoc();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
