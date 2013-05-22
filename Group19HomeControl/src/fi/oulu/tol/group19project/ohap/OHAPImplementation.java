package fi.oulu.tol.group19project.ohap;


public class OHAPImplementation extends OHAPBase implements OHAPInterface {
	private static OHAPImplementation instance = null;
	private OHAPImplementation() {
		
	}
	
	@Override
	public void setObserver(OHAPListener observer) {
		this.observer=observer;
	}

	@Override
	public void startSession(String serverAddress) {
		initialize(serverAddress);

	}

	@Override
	public void endSession(boolean forceStop) {
		if(hasSession()) {
			
			try {
				taskQueue.put(new TaskData(null, TaskData.CLOSE_SESSION_CMD, null));
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
			taskQueue.put(new TaskData(uid, TaskData.GET_CMD, path));
		}
		else {
			errorMessageToClient("No Session");

		}

	}

	@Override
	public void listenTo(String uid, String path) throws InterruptedException {
		if (hasSession()) {
			taskQueue.put(new TaskData(uid, TaskData.LISTEN_CMD, path));	

		}
		errorMessageToClient("No session");
	}

	@Override
	public void unlistenTo(String uid, String path) throws InterruptedException {
		if (hasSession()) {
			taskQueue.put(new TaskData(uid, TaskData.UNLISTEN_CMD, path));
		}
		errorMessageToClient("No session");
	}

	@Override
	public void setPath(String uid, String path) throws InterruptedException {
		if (hasSession()) {
			taskQueue.put(new TaskData(uid, TaskData.SET_CMD, path));
		}
		else {
			errorMessageToClient("No Session");

		}

	}

	@Override
	protected void doShutDown(boolean forceShutdown) {
		endSession(forceShutdown);

	}

	public static OHAPImplementation getInstance() {
		if (instance == null) {
			instance = new OHAPImplementation();
		}
		return instance;
	}


}
