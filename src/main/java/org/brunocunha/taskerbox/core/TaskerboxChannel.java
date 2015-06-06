package org.brunocunha.taskerbox.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.log4j.Logger;
import org.brunocunha.inutils4j.MyStringUtils;
import org.brunocunha.taskerbox.core.utils.TaskerboxFileUtils;
import org.brunocunha.taskerbox.gui.TaskerboxControlFrame;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Abstract Class for all Taskerbox Channels
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 * @param <T>
 */
@Log4j
public abstract class TaskerboxChannel<T> {

	@NotEmpty
	@Getter
	public String id;

	@Getter
	@Setter
	private boolean daemon;

	@Getter
	@Setter
	private ScheduledExecutorService scheduler;

	@Getter
	@Setter
	protected boolean singleItemAction = true;

	@Getter
	@Setter
	protected List<ITaskerboxAction<T>> actions;

	@Getter
	@Setter
	public Set<String> alreadyPerformed = new TreeSet<String>();

	@Getter
	@Setter
	private boolean performActionFirstCheck;

	@Getter
	@Setter
	protected int checkCount;

	@Getter
	@Setter
	private Long every = 60000L;

	@Getter
	@Setter
	private Map<String, String> propertyBag = new LinkedHashMap<String, String>();

	@Getter
	@Setter
	private Map<String, String> storedPropertyBag = new LinkedHashMap<String, String>();
	
	@Getter
	@Setter
	private ScheduledChecker scheduledCheckerThread;

	@Getter
	@Setter
	private boolean paused;

	@Getter
	private boolean forced;
	
	@Getter
	private boolean running;
	
	@Getter
	@Setter
	private long timeout;
	
	@Getter
	@Setter
	private long lastPerformed;
	
	@Getter
	@Setter
	private boolean pendingSerializerThread;
	
	@Getter
	private TaskerboxChannelExecuteThread runningThread;
	
	public boolean alreadyPerformedAction(T entry) {
		if (singleItemAction) {
			log.debug("Checking if already performed action for " + entry.toString());
		}
		// else {
		// log.debug("Not controlling single items.");
		// return false;
		// }

		if (alreadyPerformed.contains(getItemFingerprint(entry).replaceAll("\r?\n", ""))) {
			log.debug("Already performed action for " + getItemFingerprint(entry).replaceAll("\r?\n", ""));
			return true;
		}

		return false;
	}

	/**
	 * Add performed action, using entry fingerprint as key
	 * 
	 * @param entry
	 */
	public void addAlreadyPerformedAction(T entry) {
		lastPerformed = System.currentTimeMillis();
		
		synchronized(alreadyPerformed) {
			alreadyPerformed.add(getItemFingerprint(entry).replaceAll("\r?\n", ""));
		}
		
		if (!pendingSerializerThread) {
			pendingSerializerThread = true;
			ChannelSerializerThread serializerThread = new ChannelSerializerThread(this);
			serializerThread.start();
		}
	}

	/**
	 * Remove entry from already performed
	 * 
	 * @param entry
	 */
	public void removeAlreadyPerformedAction(T entry) {
		synchronized(alreadyPerformed) {
			alreadyPerformed.remove(getItemFingerprint(entry));
		}
	}

	/**
	 * Unique performing controller
	 * 
	 * @param entry
	 */
	public void performUnique(T entry) {
		if (this.isPaused() && !this.isForced()) {
			return;
		}
		
		if (!alreadyPerformedAction(entry)) {
			this.perform(entry);
			addAlreadyPerformedAction(entry);
		}
	}

	/**
	 * Multiple performing controller
	 * 
	 * @param entry
	 */
	public void perform(T entry) {
		if (this.isPaused() && !this.isForced()) {
			return;
		}
		
		for (ITaskerboxAction<T> action : getActions()) {
			try {
				log.debug("Performing action in " + action.getClass());
				action.action(entry);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Propagate exception for actions
	 * 
	 * @param entry
	 */
	public void performException(Throwable entry) {
		if (this.isPaused() && !this.isForced()) {
			return;
		}
		
		for (ITaskerboxAction<T> action : getActions()) {
			try {
				log.debug("Performing exception in " + action.getClass());
				action.exception(entry);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the scheduler thread for the channel
	 * 
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 */
	public void scheduleTask(long initialDelay, long delay, TimeUnit unit) {
		scheduler = Executors.newScheduledThreadPool(1);

		this.scheduledCheckerThread = new ScheduledChecker(this);
		this.scheduledCheckerThread.setName("scheduler-" + getId());
		
		//was scheduleWithFixedDelay
		scheduler.scheduleAtFixedRate(scheduledCheckerThread, initialDelay, delay, unit);
		
	}

	public InputStream getAppResource(String resourceName) {
		
		InputStream is = getClass().getResourceAsStream("/" + resourceName);
		if (is == null) {
			is = getClass().getResourceAsStream(resourceName);
		}
		if (is == null) {
			is = getClass().getResourceAsStream("/" + resourceName);
		}
		if (is == null) {
			is = getClass().getResourceAsStream("/META-INF/resources/" + resourceName);
		}
		if (is == null) {
			is = getClass().getResourceAsStream("/WEB-INF/classes/" + resourceName);
		}
		if (is == null) {
			is = getClass().getResourceAsStream("/resources/" + resourceName);
		}
		
		if (is == null) {
			log.error("Nao foi encontrado resource para " + resourceName + " na aplicacao.");
		} else {
			log.warn("Stream para '" + resourceName + "' foi encontrada!");
		}
		
		
		return is;
	}
	
	public List<String> listFromResource(String resourceName) {
		List<String> items;
		try {
			items = MyStringUtils.getContentListSplit(getAppResource(resourceName),
					"\r\n");
			log.info("Returning " + items.size() + " objects on " + resourceName);
			return items;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Main method of channel
	 * 
	 * @throws Exception
	 */
	protected abstract void execute() throws Exception;

	/**
	 * Runs check without forcing if it is paused
	 * 
	 * @throws Exception
	 */
	public void check() throws Exception {
		this.check(false);
	}

	/**
	 * Runs check even if the channel is paused (used by UI)
	 * 
	 * @param force
	 * @throws Exception
	 */
	public final void check(boolean force) throws Exception {
		if (force || !isPaused()) {
			
			try {
			this.checkCount++;
			this.running = true;
			TaskerboxControlFrame.getInstance().updateChannels();
			
			
			this.forced = force;
			
			this.runningThread = new TaskerboxChannelExecuteThread(this);
			try {
				this.runningThread.start();
				this.runningThread.join();
				
				if (this.runningThread != null) {
					Exception throwExc = this.runningThread.getException();
					boolean success = this.runningThread.isSuccess();
					
					this.runningThread = null;
					
					if (!success) {
						if (throwExc == null) {
							throw new RuntimeException("Thread not ran successfully! " + getId());
						} else {
							throw throwExc;
						}
					}
				}
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			
			} catch(Exception e) {
				throw e;
			} finally {
				this.running = false;
				TaskerboxControlFrame.getInstance().updateChannels();
			}
		}
	}

	/**
	 * Fingerprint implementation - Fingerprint must produce an unique value for
	 * different entries because it is used to "already checked" implementation
	 * 
	 * @param entry
	 * @return
	 */
	protected abstract String getItemFingerprint(T entry);

	/**
	 * Default to log.info displaying the channel id
	 * 
	 * @param logger
	 * @param msg
	 */
	protected void logDebug(Logger logger, String msg) {
		logger.debug("[" + this.getId() + "] - " + msg);
	}
	
	/**
	 * Default to log.info displaying the channel id
	 * 
	 * @param logger
	 * @param msg
	 */
	protected void logInfo(Logger logger, String msg) {
		logger.info("[" + this.getId() + "] - " + msg);
	}
	
	/**
	 * Default to log.warn displaying the channel id
	 * 
	 * @param logger
	 * @param msg
	 */
	protected void logWarn(Logger logger, String msg) {
		logger.warn("[" + this.getId() + "] - " + msg);
	}
	
	/**
	 * Default to log.error displaying the channel id
	 * 
	 * @param logger
	 * @param msg
	 */
	protected void logError(Logger logger, String msg) {
		logger.error("[" + this.getId() + "] - " + msg);
	}

	/**
	 * Default to log.error displaying the channel id
	 * 
	 * @param logger
	 * @param msg
	 */
	protected void logError(Logger logger, String msg, Throwable error) {
		logger.error("[" + this.getId() + "] - " + msg, error);
	}

	/**
	 * Set Unique Action to Channel. It creates the list and add the action to
	 * collection
	 * 
	 * @param action
	 */
	public void setAction(ITaskerboxAction<T> action) {
		if (this.actions == null) {
			this.actions = new ArrayList<ITaskerboxAction<T>>();
		}

		this.actions.add(action);
	}

	/**
	 * When the ID is setted, it is possible to import from repository what was
	 * performed previously in the channel, avoiding duplicates action
	 * performing in case of relaunch.
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;

		try {
			TaskerboxFileUtils.deserializeMemory(this);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Default implementation to Setup. So the endpoint channels are not forced
	 * to implement
	 * 
	 * @throws Exception
	 */
	public void setup() throws Exception {
	}

	/**
	 * Add key/value pair to property bag. It is used to work with generic
	 * properties (not defined as a member of class)
	 * 
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, String value) {
		getPropertyBag().put(key, value);
	}

	/**
	 * Add key/value pair to stored property bag. It is used to work with generic
	 * properties (not defined as a member of class)
	 * 
	 * @param key
	 * @param value
	 */
	public void addStoredProperty(String key, String value) {
		getStoredPropertyBag().put(key, value);
	}
	
	/**
	 * Delegate method to get property from bag
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return getPropertyBag().get(key);
	}

	/**
	 * Delegate method to get property from stored bag
	 * @param key
	 * @return
	 */
	public String getStoredProperty(String key) {
		return getStoredPropertyBag().get(key);
	}
	
	/**
	 * Defaults the display name (name that is shown in UI) to ID
	 * @return
	 */
	public String getDisplayName() {
		return this.getId();
	}

	public String getGroupName() {
		return this.getClass().getSimpleName().replace("Channel", "");
	}
	
}
