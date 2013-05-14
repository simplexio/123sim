package fi.oulu.tol.group19project.ohap;

/**
 * The listener interface should be implemented by the clients of {@link OHAPInterface}.<p>
 * The listener will be notified using the callback methods of interesting events within
 * the protocol.<p>
 * NOTE!: The protocol runs in a separate thread from the client. As protocol calls these callbacks,
 * the call is made in the protocol thread context. Thus, if you do any UI related stuff in the
 * callbacks, you have to implement it so that the code is run in the main thread context of the application,
 * not in the protocol thread context. Example client implementation in the HomeControlService class:
 * <pre><code>
 * public void errorMessageFromServer(final String msg) {
 *	eventHandler.post(new Runnable() { // eventHandler is a Handler you need to instantiate as class member.
 *		public void run() {
 *			Log.d(TAG, "Model got error msg from server: " + msg);
 *			String str = getString(R.string.server_said_error);
 *			Toast.makeText(HomeControlService.this, str + msg, Toast.LENGTH_LONG).show();
 *		}
 *	});
 *}
 * </code></pre>
 */
public interface OHAPListener {
	/**
	 * Called by the protocol when the session with the server has been established.
	 */
	public void sessionInitiatedSuccessfully();
	/**
	 * Calls by the protocol when the session has been ended.
	 */
	public void sessionEnded();
	/**
	 * Called by the protocol when a VALUE response arrived from the server.
	 * @param content The JSON string.
	 */
	public void contentFromServerArrived(final String content);
	/**
	 * Called by the protocol when an error happened either on the client side
	 * or in the server side.
	 * @param msg Descriptive text about the error.
	 */
	public void errorMessageFromServer(final String msg);
	/**
	 * Server replied with OK to a request.
	 */
	public void okFromServerArrived();
}
