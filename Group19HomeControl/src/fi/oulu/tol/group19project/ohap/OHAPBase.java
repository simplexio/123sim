package fi.oulu.tol.group19project.ohap;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

/**
 * This class almost fully implements the OHAP protocol as specified at 
 * <a href="http://ohap.opimobi.com">opimobi.com</a>.<p>
 * 
 * <strong>First</strong>, you should fill in the missing parts and implement a concrete protocol
 * sublcass as instructed in the exercises. This class must be inherited and the abstract method implemented.
 * Also, the OHAPTaskBase used by this class, is an abstract class and it must be inherited and implemented.<p>
 *
 * The protocol sets up the connection with the home control unit by using a {@link OHAPTaskBase}.
 * Protocol creates first one OHAPTaskBase derived object and tries to form a session with the server using it.
 * When the sessionId arrives from the server, protocol instantiates another OHAPTaskBase based
 * object. These two task objects then take tasks from the task queue and handle the results from the server.<p>
 * 
 * The result is interpreted by the OHAPTaskBase and put on the results queue. Protocol then reads the
 * results queue, interprets the result and notifies the client of the protocol about what happened.
 * If there are result data (JSON string) protocol passes the result string as a
 * parameter to the client. See the protocol observer interface {@link OHAPListener} for details 
 * on the callback methods the protocol calls and the client must implement.<p>
 * 
 * See the {@link OHAPInterface} interface class on instructions on how the concrete protocol
 * should be implemented and how it is used by the client (HomeControlService).<p>
 * 
 * 
 * @author Antti Juustila
 *
 * @version $Revision: 1.0 $
 * @see OHAPInterface
 * @see OHAPListener
 */
public abstract class OHAPBase implements Runnable {

	/**
	 * The different states of the protocol.
	 * @author Antti Juustila
	 * @version $Revision: 1.0 $
	 */
	enum ProtocolState {
		/** Protocol is uninitialized and cannot do anything. */
		Uninitialized, 
		/** Protocol has been initialized, but has not yet connected the server in any way. */
		Initialized, 
		/** Protocol has now sent the session initialization msg to the server but there is no session yet. */
		Connecting, 
		/** Protocol has now a session with the server and can send and receive actual OHAP commands. */
		Connected
		};
	/**
	 * The observer which is notified of the protocol events.
	 */
	protected OHAPListener observer = null;
	
	/**
	 * Protocol state variable.
	 */
	private ProtocolState state = ProtocolState.Uninitialized;

	/**
	 * The URL to the server, without session id string, and the running
	 * counter (see OHAP protocol specs). Just the plain address, like
	 * "http://ohap.opimobi.com:18000".
	 */
	private String serverAddress = null;

	/**
	 * Is the protocol thread running or not.
	 */
	private boolean running = false;

	/**
	 * The protocol thread object.
	 */
	private Thread myThread = null;

	/**
	 * The first task created when the protocol starts. It will 
	 * try to create the session with the server, and when succeeded,
	 * is one of the two task objects, reading tasks from the task queue
	 * and handling them, writing results to the results queue.
	 */
	private OHAPTaskBase firstTask = null;

	/**
	 * The second task, which handles commands sent to
	 * the central unit server. It is created when the session
	 * has been established, by the protocol.
	 */
	private OHAPTaskBase secondTask = null;


	/** 
	 * Here is the task queue the {@link OHAPTaskBase} objects read.
	 * If the queue is empty, take() blocks the reading thread.
	 */
	protected BlockingQueue<TaskData> taskQueue = null;

	/**
	 * Here both tasks ({@link OHAPTaskBase}) write their results and the protocol
	 * then handles those. If the queue is empty, the reading
	 * thread (protocol, that is) calling take() will block.
	 */
	protected BlockingQueue<String> results = null;

	private static final String TAG = "HCApp-OHAPBase";

	// Server replies:
	/** Server reply OK. */
	public static final String OK = "OK";
	/** Server reply VALUE -- JSON should be also with this reply. */
	public static final String VALUE = "VALUE";
	/** Server reply ERROR -- error text should be also with this reply. */
	public static final String ERROR = "ERROR";
	/** Internal way for the tasks to indicate a session has been established.*/
	private static final String SESSION = "SESSION";

	/** Connection attempt number, used in making the first connection if it fails. */
	private static final int CONNECTION_ATTEMPTS = 1;
	/** Connection attempt counter, used in making the first connection if it fails.*/
	private int connectionCount = CONNECTION_ATTEMPTS;
	

	/**
	 * Use the method to check if the protocol has been initialized (not yet connected).
	 * @return true if it has been initialized.
	 */
	public boolean isInitialized() {
		return state == ProtocolState.Initialized;
	}


	/**
	 * Checks if the protocol thread is running or not.
	 * @return boolean Returns true if the protocol thread is running.
	 */
	public boolean isRunning() {
		return running;
	}

	
	//////////////////////////////////////
	// Private part starts from there!  //
	//////////////////////////////////////

	/**
	 * Run method of the protocol. Here the protocol checks the state of itself, and does
	 * correct things, depending on the state. 
	 * <ul><li>If the state is Initialized, protocol will create
	 * the firstTask object and use it to create a session with the server, using the task queue.</li>
	 * <li>If the state is Connecting, protocol checks if the firstTask has succeeded
	 * in getting the session up. If yes, protocol creates and launches the secondTask object,
	 * and sets the state to Connected. If no, it will put another startsession task 
	 * in the task queue, and attempts to connect for limited amount of tries.</li>
	 * <li>If the state is Connected, protocol first checks that the tasks have something to do.
	 * If not, it will put an empty task in the task queue. Then it will try to read the results
	 * queue and if there are results, it will handle them.</li>
	 * </ul>
	 * @see OHAPTaskBase 
	 */
	@Override
	public void run() {
		try {
			while (running) {
				switch (state) {
				case Uninitialized: {
					Thread.sleep(1000); // nah...
					break;
				}
				case Initialized: {
					Log.d(TAG, "Protocol initialized, starting the session task");
					firstTask = new OHAPTaskImplementation();
					firstTask.initialize(serverAddress, taskQueue, results);
					taskQueue.put(new TaskData(null, TaskData.INIT_SESSION_CMD, null));
					state = ProtocolState.Connecting;
					firstTask.start();
					Thread.sleep(1000);
					break;
				}
				case Connecting: {
					Log.d(TAG, "Protocol now connecting...");
					String result = results.take();
					Log.d(TAG, "Response: " + result);
					if (firstTask.hasSession()) {
						Log.d(TAG, "...protocol connected!");
						state = ProtocolState.Connected;
						secondTask = new OHAPTaskImplementation();
						Log.d(TAG, "Initializing workerTask...");
						secondTask.initialize(serverAddress, taskQueue, results);
						secondTask.start();
						handleServerResponse(result);
					} else {
						// if there are no tasks in queue, connection attempt counter says
						// we still can try, and firstTask is not in the middle of trying,
						// then we need to try to connect again for the certain max count times.
						// The we have to quit and believe there is no server to connect to.
						if (connectionCount-- > 0 && !firstTask.isBusy()) {
							// Do not fill the queue with init tasks...
							if (taskQueue.peek() == null) {
								Log.d(TAG, "Put another start session message in session task queue");
								taskQueue.put(new TaskData(null, TaskData.INIT_SESSION_CMD, null));
								Log.d(TAG, "Still connecting..." + connectionCount);
								Thread.sleep(5000);
							}
						} else {
							Log.d(TAG, "Could not connect, stop connections");
							handleServerResponse("ERROR Cannot connect to the server!!");
							doShutDown(true);
						}
					}
					break;
				}
				case Connected: {
					Log.d(TAG, "Protocol connected, checks task and results queues.");
					if (taskQueue.peek() == null && !firstTask.isBusy() && !secondTask.isBusy()) {
						Log.d(TAG, "No tasks in queues, put empty request in session task queue.");
						taskQueue.put(new TaskData(null, TaskData.EMPTY_REQUEST_CMD, null));
					}
					Log.d(TAG, "Check how  many items in results queue: " + results.size());
					handleServerResponse(results.take());
					break;
				}
				default: {
					break;
				}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.d(TAG, "Protocol thread exception!");
		}

	}


	/**
	 * In constructor, the task queue and the results queue are created.
	 */
	protected OHAPBase() {
		taskQueue = new LinkedBlockingQueue<TaskData>(50);
		results = new LinkedBlockingQueue<String>(50);
	}

	/**
	 * Initializes the protocol using the provided server address.
	 * Sets the protocol state to Initialized and starts the
	 * protocol thread. Rest of the work is done in the protocol's
	 * {@link run()} method.
	 * @param serverAddress The URL of the server.
	 */
	protected void initialize(String serverAddress) {
		if (state != ProtocolState.Uninitialized) {
			doShutDown(false);
		}
		Log.d(TAG, "Initiating HCProtocol object...");
		this.serverAddress = serverAddress;
		connectionCount = CONNECTION_ATTEMPTS;
		Log.d(TAG, "...initialized HCProtocol, now starting protocol thread...");
		state = ProtocolState.Initialized;
		if (myThread == null) {
			myThread = new Thread(this, "HCProtocolThread");
			running = true;
			myThread.start();
		}
		Log.d(TAG, "...Protocol thread started.");
	}

	/**
	 * Provide the concrete implementation a change to do shutdown related things.<p>
	 * Forced shutdown means that even if the protocol sends the HTTP DELETE to the
	 * server to indicate we wish to end the session with the server, we will not
	 * wait for the confirmation from the server, but just close everything down.
	 * @param forceShutdown Should the protocol be closed down forcefully or not
	 */
	protected abstract void doShutDown(boolean forceShutdown);
	
	/**
	 * Stops the protocol:<p>
	 * <ul>
	 * <li>Clears the task queue</li>
	 * <li>Clears the results queue</li>
	 * <li>Stops the firstTask thread</li>
	 * <li>Stops the secondTask thread</li>
	 * <li>Interrupts and stops the protocol thread</li>
	 * <li>Sets the protocol state to Uninitialized</li>
	 * </ul>
	 */
	protected void doStop() {
		Log.d(TAG, "Stopping protocol...");
		taskQueue.clear();
		results.clear();
		OHAPTaskBase.sessionStr = null;
		OHAPTaskBase.urlCounter = 0;
		if (firstTask != null) {
			firstTask.stop();
			firstTask = null;
		}
		if (secondTask != null) {
			secondTask.stop();
			secondTask = null;
		}
		running = false;
		if (null != myThread) {
			myThread.interrupt();
			try {
				myThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		myThread = null;
		state = ProtocolState.Uninitialized;
		Log.d(TAG, "...Protocol stopped");
	}

	/**
	 * Use this method to check if the session has been initialized or not.
	 * @return boolean True, if session exists.
	 */
	public boolean hasSession() {
		if (null != firstTask) {
			return firstTask.hasSession();
		}
		return false;
	}


	/**
	 * Passes the provided error message to the observer if one is set.
	 * @param msg The error message.
	 */
	protected void errorMessageToClient(String msg) {
		if (null != observer) {
			observer.errorMessageFromServer(msg);
		}
	}

	/**
	 * Handles the response from the server.<p>
	 * Basically, notifies the observer of what happened, using the
	 * {@link OHAPListener} interface methods. Only specially handled
	 * message is the DELETE_CMD message, which indicates that the
	 * server has been sent the end session message ("DELETE"). In this
	 * case, the protocol is stopped, by calling {@link doStop()}
	 * @param response String
	 */
	private void handleServerResponse(String response) {
		if (response.equalsIgnoreCase(OK)) {
			Log.d(TAG, "OK from server!");
			if (null != observer) {
				// OK, the request was successful, so what to do with it?
				// Put OK into the client to read? Or just let it be?
				observer.okFromServerArrived();
			}
		} else if (response.equalsIgnoreCase(TaskData.CLOSE_SESSION_CMD)) {
			doStop();
			if (null != observer) {
				observer.sessionEnded();
			}
		} else {
			int startsFrom = response.indexOf(VALUE, 0);
			if (startsFrom >= 0) {
				String responseStr = response.substring(startsFrom+VALUE.length());
				Log.d(TAG, "VALUE arrived from server: " + responseStr);
				if (null != observer) {
					observer.contentFromServerArrived(responseStr);
				}
				return;
			}
			startsFrom = response.indexOf(ERROR, 0);
			if (startsFrom >= 0) {
				String responseStr = response.substring(startsFrom+ERROR.length());
				Log.d(TAG, "ERROR arrived.");
				errorMessageToClient(responseStr);
				return;
			}
			startsFrom = response.indexOf(SESSION, 0);
			if (startsFrom >= 0) {
				Log.d(TAG, "Session initialized ");
				observer.sessionInitiatedSuccessfully();
				return;
			}
			Log.d(TAG, "Unknown message from server: " + response);
		}
	}

}

