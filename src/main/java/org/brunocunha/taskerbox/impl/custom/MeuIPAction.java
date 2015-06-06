package org.brunocunha.taskerbox.impl.custom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.log4j.Log4j;

import org.brunocunha.inutils4j.MyStringUtils;
import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;
import org.jsoup.nodes.Document;

@Log4j
public class MeuIPAction extends DefaultTaskerboxAction<Document> {

	private String id;

	private File outputFile;
	
	@Override
	public void action(final Document entry) {
		
		String body = entry.html();
		//System.out.println(body);
		String ip = MyStringUtils.regexFindFirst("getElementById\\(\"div_reverso\"\\).innerHTML = \"(.*?)\"", body);
		logInfo(log, "IP Address Found: " + ip);
		
		if (outputFile != null) {
		FileWriter out;
			try {
				out = new FileWriter(outputFile, true);
				out.write(getTimestamp() + " " + InetAddress.getLocalHost().getHostName() + " - " + ip + "\r\n");
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "[".concat(sdf.format(new Date())).concat("]");
    }

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

    
	
}
