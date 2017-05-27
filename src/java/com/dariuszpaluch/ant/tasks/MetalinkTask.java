
package org.ant.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class MetalinkTask extends Task {
	private String url;
	private FileSet fileSet;
	private String file;

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

	public void checkUrl() {
		if(this.url == null) {
			this.setUrl(getProject().getProperty("server.files.url"));
		}
		System.out.println("URL: ", this.url);
	}

	public void execute() throws BuildException {
		this.checkUrl();
		System.out.println("DARIUSZ PALUCH122");
	}
}
