package org.ant.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.util.List;

public class MetalinkTask extends Task {
	private String url;
	private FileSet fileSet;
	private String file;
	private List<File> files;

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

	private void readAllFiles() {
		System.out.println("Read all files");
		DirectoryScanner ds = this.fileSet.getDirectoryScanner(getProject());
		String[] includedFiles = ds.getIncludedFiles();
		for(int i=0; i<includedFiles.length; i++) {
			String filename = includedFiles[i].replace('\\','/');
			System.out.println(filename);
		}
	}

	public void execute() throws BuildException {
		System.out.println("Start metalink task");
		this.initTask();
	}
}
