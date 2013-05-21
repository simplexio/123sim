package fi.oulu.tol.group19project.ohap;


public class OHAPImplementation extends OHAPBase implements OHAPInterface {
	
	@Override
	public void setObserver(OHAPListener observer) {

	}

	@Override
	public void startSession(String serverAddress) {
		initialize(serverAddress);

	}

	@Override
	public void endSession(boolean forceStop) {
		if(hasSession()) {
			TaskData closeSession = new TaskData(null, TaskData.CLOSE_SESSION_CMD, null);
			try {
				taskQueue.put(closeSession);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else {
			doStop();
			errorMessageToClient("No session");
		}
	}

	@Override
	public void getPath(String uid, String path) throws InterruptedException {
		if (hasSession()) {
			TaskData getData = new TaskData(uid, TaskData.GET_CMD, path);
			taskQueue.put(getData);
		}
		else {
			errorMessageToClient("No Session");

		}

	}

	@Override
	public void listenTo(String uid, String path) throws InterruptedException {
		if (hasSession()) {
			TaskData message = new TaskData(uid, TaskData.LISTEN_CMD, path);
			taskQueue.put(message);	

		}
		errorMessageToClient("No session");
	}

	@Override
	public void unlistenTo(String uid, String path) throws InterruptedException {
		if (hasSession()) {
			TaskData message1 = new TaskData(uid, TaskData.UNLISTEN_CMD, path);
			taskQueue.put(message1);	

		}
		errorMessageToClient("No session");
	}

	@Override
	public void setPath(String uid, String path) throws InterruptedException {
		if (hasSession()) {
			TaskData stateChange = new TaskData(uid, TaskData.SET_CMD, path);
			taskQueue.put(stateChange);
		}
		else {
			errorMessageToClient("No Session");

		}

	}

	@Override
	protected void doShutDown(boolean forceShutdown) {
		endSession(forceShutdown);

	}


}
