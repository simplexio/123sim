package fi.oulu.tol.group19project.ohap;

/**
 * Encapsulates the OHAP task data elements in a simple class.
 * <p>
 * 
 * This class is used to describe the task that should be executed. An example,
 * where a session initiation command is put on the taskQueue:
 * <p>
 * 
 * <pre>
 * <code>
 * taskQueue.put(new TaskData(null, TaskData.INIT_SESSION_CMD, null));
 * </code>
 * </pre>
 * <p>
 * Here both uid and path are null since they are not needed with this command.
 * 
 * @author Antti Juustila
 * @see OHAPBase
 * @see OHAPTaskBase
 */
public class TaskData {

	// Session management commands:
	/**
	 * Send this command to the server to initiate an OHAP session with the
	 * server. This should be the first thing to send to the server.
	 * <p>
	 * For this command, the path parameter of the command is not used.
	 */
	public static final String INIT_SESSION_CMD = "INIT";
	/**
	 * This kind of command is sent to the server if nothing else sensible is
	 * needed from the server. This is needed just to keep an ongoing request
	 * open to the server. So the server is then able to send us something if
	 * some device state changes.
	 * <p>
	 * For this command, the path parameter of the command is not used.
	 */
	public static final String EMPTY_REQUEST_CMD = "EMPTY";
	/**
	 * This is sent to the server when the client no longer wants to communicate
	 * with the server. After sending this, you can only send the session
	 * initiation command, nothing else.
	 * <p>
	 * For this command, the path parameter of the command is not used.
	 */
	public static final String CLOSE_SESSION_CMD = "DELETE";

	// Commands to send to the server:
	/**
	 * Use this command to get device data from the server. To get all devices,
	 * you send a GET with path "/".
	 * <p>
	 * Check the OHAP protocol specs for details on path values.
	 */
	public static final String GET_CMD = "GET";
	/**
	 * Use SET command to change properties of a device in the server. Usually,
	 * you send the changed actuator values using SET. The path contains the new
	 * values in JSON format.
	 * <p>
	 * Check the OHAP protocol specs for details on path values.
	 */
	public static final String SET_CMD = "SET";
	/**
	 * Send a LISTEN command to the server to request change notifications from
	 * the server. Server then sends the changed properties of the device in
	 * JSON format when something happens to the device.
	 * <p>
	 * Check the OHAP protocol specs for details on path values.
	 */
	public static final String LISTEN_CMD = "LISTEN";
	/**
	 * Use UNLISTEN to cancel a sent LISTEN command.
	 * <p>
	 * Check the OHAP protocol specs for details on path values.
	 * 
	 */
	public static final String UNLISTEN_CMD = "UNLISTEN";

	/**
	 * The uid for the task. This can be null. Use it if you want to track which
	 * reply from the server relates to which sent command.
	 */
	private String taskUid = null;
	/**
	 * The actual command, e.g. GET, LISTEN, etc.
	 */
	private String command = null;
	/**
	 * The data related to the command. Content depends on the command sent.
	 * <p>
	 * Check the OHAP protocol specs on the possibile data values of different
	 * commands.
	 */
	private String data = null;

	/**
	 * Constructor for TaskData.
	 * 
	 * @param uid
	 *            Task uid, may be null.
	 * @param command
	 *            Command string, should be a valid OHAP command.
	 * @param data
	 *            Data to send with the command, depends on the command.
	 */
	TaskData(String uid, String command, String data) {
		taskUid = uid;
		this.command = command;
		this.data = data;
	}

	/**
	 * Copy constructor for TaskData.
	 * 
	 * @param another
	 *            Another object to copy values from.
	 */
	TaskData(TaskData another) {
		this.taskUid = another.taskUid;
		this.command = another.command;
		this.data = another.data;
	}

	/**
	 * Sets the task Uid.
	 * 
	 * @param uid
	 *            Uid of the task.
	 */
	public void setTaskUid(String uid) {
		taskUid = uid;
	}

	/**
	 * Sets the task's command.
	 * 
	 * @param command
	 *            The command.
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * Sets the tasks data. See the OHAP specs on what data is sent with each
	 * command.
	 * 
	 * @param data
	 *            The data to send.
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Gets the uid of the task.
	 * 
	 * @return The uid, may be null.
	 */
	public String getUid() {
		return taskUid;
	}

	/**
	 * Gets the command of the task.
	 * 
	 * @return The command string.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Gets the data of the command.
	 * 
	 * @return Data string.
	 */
	public String getData() {
		return data;
	}

}
