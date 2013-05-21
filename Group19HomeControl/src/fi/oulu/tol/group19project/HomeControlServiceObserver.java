package fi.oulu.tol.group19project;

/**
 * An interface implemented by clients of HomeControlService. Service will
 * notify the client of changes in the model by using this interface.
 * Changes happen to the model, when the protocol gets new or changed data 
 * from the server. Observer (which is the DeviceListActivity) then updates 
 * whatever is visibile to the user (list contents, etc.).
 */
public interface HomeControlServiceObserver {
	
private HomeControlServiceObserver observer = null;

public void setObserver(HomeControlServiceObserver observer) {
   this.observer = observer;
}
	/**
	 * Called when the model is updated. Client can
	 * get the new model from the service, and update the UI accordingly.<p>
	 * In future, it would be wise to provide some data
	 * to the client about what exactly changed (as a parameter to this method).
	 * Client could then check what actually needs to be updated.
	 */
	public void modelUpdated();



}