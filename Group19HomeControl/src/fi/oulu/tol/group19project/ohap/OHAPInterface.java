package fi.oulu.tol.group19project.ohap;

/**
 * This is the interface class of the protocol. You can use this interface
 * in your application and change / use different protocol implementations behind the
 * interface class. As long as the interface and the implementation are compatible, of course.<p>
 * 
 * Usage:
 * <ul>
 * <li>Create the protocol object and start observing it (how, is defined by the concrete implementation).</li>
 * <li>Call {@link startSession(String)} to connect to the server.</li>
 * <li>The protocol calls methods of {@link OHAPListener} observer interface, while it is running, 
 * let the observer know about relevant events within the protocol.</li>
 * <li>Call {@link getPath(String, String)} to get device data. Use path "/" to get all devices.</li>
 * <li>Call {@link setPath(String, String)} to send changed device state to the server.</li>
 * <li>Call {@link listenTo(String, String)} to start listening to state changes to devices in the server.</li>
 * <li>Call {@link unlistenTo(String, String)} to stop listening to device state changes.</li>
 * <li>As mentioned, the protocol notifies the client of received values by calling  {@link OHAPListener}. 
 * When actual data comes from the server to the protocol, protocol calls 
 * {@link OHAPListener#contentFromServerArrived(String)} and client can take the data (JSON string)
 * and handle it.</li>
 * <li>Finally, call {@link endSession(boolean)} to close the session with the server.</li>
 * </ul>
 * @author Antti Juustila
 * @see OHAPListener
 */
public interface OHAPInterface {

	/**
	 * Set the observer which gets notified when something worth
	 * announcing happens within the protocol.
	 * @param observer The observer of the protocol
	 * @see fi.oulu.tol.homecontrol.ohap.OHAPInterface#setObserver(OHAPListener)
	 */
	public void setObserver(OHAPListener observer);
/**
	 * Starts a session with the specified server. All threads needed for this
	 * are started and a session initiation message is sent to the server.<p>
	 * TODO: call {@link initialize(String)}, which will do the setup of the protocol.
	 * Investigate the initialize() method, as well as the protocol's
	 * run() method to see what is actually happening in starting the session.
	 * @param serverAddress The address of the server.
	 */
	public void startSession(String serverAddress);
	
	/**
	 * Ends the session with the server. Will send a HTTP DELETE request to the
	 * server, then closes the connection and resets the protocol.<p>
	 * TODO: Check that there is a session ongoing, and if yes, create a 
	 * TaskData object with OHAP DELETE command and put it into the task queue.
	 * If forceStop is true, (or there is no session at all) then also call doStop
	 * which stops the protocol immediately. If there is no session, show error
	 * to the user by calling {@link errorMessageToClient(String)}. 
	 * @param forceStop If true, does not wait for server response before closing everything down.
	 */
	public void endSession(boolean forceStop);
	/**
	 * Sends the OHAP GET (as HTTP POST) to the server. Server should
	 * respond with JSON describing the results.<p>
	 * TODO: If a session exists, create a TaskData object with GET command, the path
	 * as data, and put it into the task queue. If there is no session, show error
	 * to the user by calling {@link errorMessageToClient(String)}.
	 * @param uid Uid for the request, may be null if not used.
	 * @param path Which device path to get. Use "/" to get all devices.
	 * @throws InterruptedException
	 */
	public void getPath(String uid, String path) throws InterruptedException;
	/**
	 * Sends the OHAP LISTEN message to the server. Server will then
	 * notify the clients when device's state changes, with an updated
	 * JSON string in a VALUE message.<p>
	 * TODO: If a session exists, create a TaskData object with LISTEN command, the path
	 * as data, and put it into the task queue. If there is no session, show error
	 * to the user by calling {@link errorMessageToClient(String)}.
	 * @param uid The Uid for the request, may be null if not used.
	 * @param path The path of the device(s) to listen.
	 * @throws InterruptedException
	 */
	public void listenTo(String uid, String path) throws InterruptedException;
	/**
	 * Cancels the previous LISTEN message. Server should response with OK message.<p>
	 * TODO: If a session exists, create a TaskData object with LISTEN command, the path
	 * as data, and put it into the task queue. If there is no session, show error
	 * to the user by calling {@link errorMessageToClient(String)}.
	 * @param uid Uid of the request, may be null if not used.
	 * @param path Path which is unlistened.
	 * @throws InterruptedException
	 */
	public void unlistenTo(String uid, String path) throws InterruptedException;
	/**
	 * Changes the state of a device/devices. Usually this applies to actuators,
	 * when user has changed the state of the actuator. The change data is sent
	 * to the server as JSON string in the path parameter. Server should response with OK message.<p>
	 * TODO: If a session exists, create a TaskData object with SET command, the path
	 * as data, and put it into the task queue. If there is no session, show error
	 * to the user by calling {@link errorMessageToClient(String)}.
	 * @param uid Uid for the request, may be null if not used.
	 * @param path The path of the request, basically JSON with change data.
	 * @throws InterruptedException
	 */
	public void setPath(String uid, String path) throws InterruptedException;
	
}
