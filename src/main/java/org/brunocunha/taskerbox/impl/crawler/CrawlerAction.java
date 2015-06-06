package org.brunocunha.taskerbox.impl.crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocunha.taskerbox.core.utils.TaskerboxFileUtils;
import org.jsoup.nodes.Document;

@Log4j
public abstract class CrawlerAction extends DefaultTaskerboxAction<Document> {

	private File output;

	@Getter @Setter
	private List<String> filters;

	@Getter @Setter
	private List<String> ignored;

	@Getter @Setter
	private List<String> patterns;

	@Getter @Setter
	private List<Pattern> compiledPatterns = new ArrayList<Pattern>();

	@Getter @Setter
	private Set<String> alreadyAct = new TreeSet<String>();

	private int count;
	
	@Override
	public void setup() {
		try {
			logInfo(log, "Starting Crawler " + getId());
			logInfo(log, "Filters: " + this.getFilters());
			logInfo(log, "Ignores: " + this.getIgnored());
			
			if (patterns != null) {
				for (String pattern : patterns) {
					compiledPatterns.add(Pattern.compile(pattern));
				}
			}

			alreadyAct = (Set<String>) TaskerboxFileUtils
					.deserializeMemory(this);

		} catch (Exception e) {
			logWarn(log, "Error occurred while deserializing data for "
					+ this.getId() + ": " + e.getMessage());
		}
	}

	public boolean isBounded(String content) {
		String lowerContent = content.toLowerCase();
		for (String filter : filters) {
			if (lowerContent.contains(filter.toLowerCase())) {
				return true;
			}
		}

		for (Pattern pattern : compiledPatterns) {
			if (pattern.matcher(lowerContent).find()) {
				return true;
			}
		}

		return false;
	}

	public boolean isIgnored(String content) {
		String lowerContent = content.toLowerCase();
		for (String find : ignored) {
			if (lowerContent.contains(find.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	public boolean canAct(String key) {
		return !getAlreadyAct().contains(key);
	}

	public void addAct(String key) {
		getAlreadyAct().add(key);
	}

	public void serializeAlreadyAct() {
		if (++count > 5) {
			count = 0;
			
			try {
				TaskerboxFileUtils.serializeMemory(this, getAlreadyAct());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
	}

	public boolean isConsiderable(String id, String content) {
		if (content.length() < 5) {
			return false;
		}
		
		return true;
	}
	
	public boolean isValid(String id, String content) {
		if (isIgnored(content)) {
			return false;
		}
		
		return isBounded(content);
	}
	
	public void doValid(String id, String content) throws IOException {
		FileWriter out = new FileWriter(new File(getOutput(), id + ".txt"));
		out.write(content.replaceAll("\r?\n", "\r\n"));
		out.close();
	}

	public void doInvalid(String id, String content) throws IOException {
		File dir = new File(getOutput(), "not");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		FileWriter out = new FileWriter(new File(dir,
				id.replace("\\", "").replace("/", "") + ".txt"));
		out.write(content.replaceAll("\r?\n", "\r\n"));
		out.close();
	}

	public File getOutput() {
		if (!output.exists()) {
			output.mkdirs();
		}
		
		return output;
	}

	public void setOutput(File output) {
		if (!output.exists()) {
			output.mkdirs();
		}
		this.output = output;
	}

	public void sleep(long interval) {
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
