package fi.oulu.tol.group19project.ohap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import android.util.Log;


/**
 * A class for network tasks in home control client, managed by the
 * {@link OHAPBase} protocol object.<p>
 * 
 * OHAP task objects handle the actual communication with the OHAP server. It sends
 * the different OHAP requests as HTTP messages to initiate the OHAP session, and 
 * sends different messages (GET, SET, LISTEN, UNLISTEN)
 * to the control unit. The requests are read from the task queue which has been
 * set up by the {@link OHAPBase} protocol object. OHAPTask stores the results of the
 * request sent to the control unit into the results queue, which is also
 * provided by the protocol. Protocol then reads the results from this queue.<p>
 * 
 * Tasks are provided to the OHAPTaskBase in a blocking queue. This means
 * that if there are no tasks in the queue, the thread blocks in reading
 * the queue. This is as we like it -- the threads do not run doing nothing,
 * wasting battery power and consuming processing power, but block waiting for 
 * objects to be put in the task queue by the {@link OHAPBase} protocol class.
 * 
 * @author Antti Juustila
 *
 * @version $Revision: 1.0 $
 */
public abstract class OHAPTaskBase implements Runnable {

	/** The URL to the central unit. */
	private String serverAddr = null;
	
	/** The session string received from the central unit after connection
	 * has been successfully made. It is static; shared between the OHAPTaskBase objects.
	 * If sessionStr is null, there is no (logical) connection (session) with the server.
	 */
	protected static String sessionStr = null;
	
	/** Is the task running or not. Controls the while loop in the run().
	 */
	private volatile boolean isRunning = false;

	/**
	 * The Thread object, needed to interrupt/stop the running of it.
	 */
	private Thread myThread = null;
	
	/**
	 * The task queue for the network task, provided by the {@link OHAPBase}.
	 */
	private BlockingQueue<TaskData> tasks =  null;

	/**
	 * The queue were the subclasses write their results when communicating with
	 * the central unit. Provided by the protocol class {@link OHAPBase}.
	 */
	private BlockingQueue<String> results = null;
		
	/**
	 * For adding the running counter to the http request per BOSH protocol specs.
	 */
	protected static int urlCounter = 0;

	/** Counter for counting number of tasks. Used only in the thread names, assists debugging. */
	private static int taskCounter = 1;
	
	/** Thread'd name. Convenient to use to see in debugger if thread still exists, also in logging. */
	private String threadName;
	
	/**
	 * Value is true, if the task is currently handling a HTTP 
	 * request with the server (waiting for execute to continue).
	 */
	protected boolean isBusy = false;
	
	////////////
	// PUBLIC //
	////////////
	
	
	/** 
	 * Constructor. Just sets the thread name to ease debugging.  
	 */
	protected OHAPTaskBase() {
		threadName = new String("HCApp-OHAPTask"+OHAPTaskBase.taskCounter++);
	}

	/**
	 * Initializes the network task with necessary objects.
	 * @param serverAddr The address (url) to the server.
	 * @param taskQueue Task queue, things to do.
	 * @param resultQueue The queue where the results are stored.
	 */
	public void initialize(String serverAddr,
			BlockingQueue<TaskData> taskQueue,
			BlockingQueue<String> resultQueue) {
		tasks = taskQueue;
		results = resultQueue;
		this.serverAddr = serverAddr;
	}

	/**
	 * Starts the network task, by creating a new thread and starting it.
	 */
	public void start() {
		doStart();
		isRunning = true;
		if (myThread == null) {
			myThread = new Thread(this, threadName());
			myThread.start();
		}
	}
	
	/**
	 * Stops the network task, by ending the run loop and interrupting.
	 */
	public void stop() {
		doStop();
		isRunning = false;
		if (null != myThread) {
			myThread.interrupt();
			try {
				myThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		myThread = null;
	}
	
	/**
	 * Returns the server URL.
	 * @return The server URL. */
	protected String getServerAddress() {
		return serverAddr;
	}
		
	/**
	 * Is there a session or not?
	 * @return true, if there is a session, otherwise false. */
	public boolean hasSession() {
		return (sessionStr != null);
	}
	
	/**
	 * Use this to check if the task is currently waiting for
	 * http execute to finish.
	 * @return true if we are waiting for execute to finish.
	 */
	public boolean isBusy() {
		synchronized (this) {
			return isBusy;
		}
	}

	
	/////////////
	// PRIVATE //
	/////////////

	/**
	 * The name of the thread. Eases debugging as we can see by the name of the
	 * thread if something goes wrong.
	 * @return The thread name. */
	protected String threadName() {
		return threadName;
	}
	

	/** 
	 * Abstract method to be implemented by a concrete subclass. Here, build the
	 * HTTP request object, execute it and handle the result. Use the base class
	 * methods as instructed to implement this method.
	 * @param task The task to execute.
	 * @throws InterruptedException
	 */
	protected abstract void prepareAndExecuteRequest(TaskData task) throws InterruptedException;
	
	/**
	 *  Provide the concrete implementations a chance to do something when the task initializes.<p>
	 *  Here, create the AndroidHTTPClient and set it's parameters.
	 */
	protected abstract void doStart();
	
	/**
	 * Provide the concrete implementations a chance to do something when the task closes down.<p>
	 * Here, <i>close</i> the AndroidHTTPClient object.
	 */
	protected abstract void doStop();
	
	/**
	 * The thread run loop, where the task base object handles reading the task queue.
	 */
	private void doRun() {
		Log.d(threadName(), "Entering doRun...");
		try {
			// take blocks if there is no tasks in the queue, which is just fine...
			Log.d(threadName(), "Try to get a task from queue...");
			TaskData task = tasks.take();
			Log.d(threadName(), "Got a task, will try to handle it");
			if (serverAddr != null) {
				prepareAndExecuteRequest(task);
			} else {
				Log.d(threadName(), "No server address!");
			}
		} catch (InterruptedException e) {
			String error = "Interrupted Exception!!";
			Log.d(threadName(), error);
			e.printStackTrace();
		}
	}
	
	/**
	 * Use this to get the session string.
	 * @return The session string. */
	protected String getSession() {
		return sessionStr;
	}
	
	/**
	 * Each request to the control unit must have a increasing
	 * integer counting value. See getUrl() for details. 
	 * @return The next counter value. */
	private static int getSessionCounter() {
		return urlCounter++;
	}
	
	/**
	 * Use this method to get the URL when actually sending control
	 * messages to the control unit. It forms the URL according to the
	 * OHAP protocol specs, by combining the server http address, session
	 * string and the session counter. Example of an url is:
	 * <code>http://ohap.opimobi.com:18000/ad2143xb/5/</code>, which consist of
	 * <ul>
	 * <li>Server address: <code>http://ohap.opimobi.com:18000</code></li>
	 * <li>Session id: <code>ad2143xb</code></li>
	 * <li>Session request counter: <code>5</code> which increases with each http request.</li>
	 * </ul>
	 * @return The URL to use when posting requests to the control unit. */
	protected String getUrl() {
		String Url = getServerAddress();
		if (hasSession()) {
			if (!Url.endsWith("/")) {
				Url += "/";	
			}
			Url += getSession() + "/" + getSessionCounter();
		}
		return Url;
	}

	
	/**
	 * Called by the concrete subclass, when a response has been received from
	 * the http post. Reads the response string and handles each line from the response, using
	 * {@link handleString(String)}.
	 * @param is The input stream of the response.
	 * @throws IOException
	 * @throws InterruptedException */
	protected void handleInputStream(InputStream is) throws IOException, InterruptedException {	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    Log.d(threadName(), "BufferedReader toString: " + rd.toString());
	    String line = rd.readLine();
	    while (line != null) { 
	    	handleString(line);
	    	line = rd.readLine();
	    }
	}
	
	/**
	 * Reads a response line, basically just putting it to the results blocking queue.
	 * Protocol thread is reading this queue and gets the result, and sees what needs to be done.
	 * @param line The response line to handle.
	 * @throws InterruptedException */
	protected void handleString(String line) throws InterruptedException {
		// If we are quitting, it's all the same to clear other results waiting
		// to be handled.
		Log.d(threadName(), "Handling string: " + line);
		if (line.equalsIgnoreCase(TaskData.CLOSE_SESSION_CMD)) {
			results.clear();
		}
    	results.put(line); 
	}
	
	/**
	 * The run loop for the network tasks. Basically just calls {@link doRun()} in a while loop.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (isRunning) {
			doRun();
		}
		
	}
}
