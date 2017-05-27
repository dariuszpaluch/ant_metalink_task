package org.ant.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.util.ArrayList;
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
	private void createMetalinkDoc() throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("company");
		doc.appendChild(rootElement);


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
		}
	}
}
