package fi.oulu.tol.group19project.model;

/**
 * DeviceFetcher is a {@link Visitor} which goes through a device object
 * structure and finds a specified device, returning it to the caller.<p>
 * 
 * It is possible to fetch a device by it's index in the device structure
 * or by device id, currently.<p>
 * 
 * The method for getting the n'th device goes through the whole hierarchy
 * of devices depth first and returns the child device from the hierarchy.<p>
 * 
 * You can use this fetcher to get the n'th device in the DeviceAdapter,
 * to provide the device object to show in a specific row in the list view.<p>
 * 
 * Usage example:
 * <pre><code>
 * DeviceContainer devices = new DeviceContainer();
 * ...
 * DeviceFetcher fetcher = new DeviceFetcher();
 * ...
 * // Get the 10th device from devices: 
 * AbstractDevice d = fetcher.fetchChildDevice(devices, 10);
 * ...
 * // Get the device with with id "my-device-id" from devices:
 * d = fetcher.fetchChildDevice(devices, "my-device-id");
 * </code></pre>
 * @author Antti Juustila
 *
 */
public class DeviceFetcher implements Visitor {

	/**
	 *  The index of the object to get, in the object structure.
	 *  If the value is < 0, this variable is not used in search.
	 */
	private int targetDeviceIndex = 0;
	/**
	 * The current index, increasing as going forward in the structure, 
	 * if using the device index in search.
	 */
	private int currentDeviceIndex = -1;
	/** 
	 * The id of the device to search for.
	 * If the value is null, id is not used in search.
	 */
	private String deviceId = null;
	
	/** The found device. null if no device was found. */
	private AbstractDevice foundDevice = null;
		
	/**
	 * Searches for a device from a specified index. Search goes forward depth
	 * first.
	 * @param fromDevice Start to search for the device from this device (usually a DeviceContainer).
	 * @param index The index of the device to search for.
	 * @return The found device, null if not found.
	 */
	public AbstractDevice fetchChildDevice(AbstractDevice fromDevice, int index) {
		targetDeviceIndex = index;
		currentDeviceIndex = -1;
		foundDevice = null;
		deviceId = null;
		
		fromDevice.accept(this);
		return foundDevice;
	}

	/**
	 * Searches for a device with a specified id. Search goes forward depth
	 * first.
	 * @param fromDevice Start to search for the device from this device (usually a DeviceContainer).
	 * @param deviceId The id of the device to search for.
	 * @return The found device, null if not found.
	 */
	public AbstractDevice fetchChildDevice(AbstractDevice fromDevice, String deviceId) {
		targetDeviceIndex = -1;
		currentDeviceIndex = -1;
		foundDevice = null;
		this.deviceId = deviceId;
		
		fromDevice.accept(this);
		return foundDevice;
	}
	
// ---- The visit implementations ----
		
	/**
	 * Visits an abstract device. With this visitor,
	 * all the different devices are treated the same way,
	 * so this method is the only implementation for this
	 * visitor and other visit methods (except for {@link DeviceContainer})
	 * just call this one.
	 * 
	 * @param device The device to visit.
	 */
	@Override
	public void visit(AbstractDevice device) {
		// Search using the deviceindex:
		if (targetDeviceIndex >= 0) {
			if (++currentDeviceIndex == targetDeviceIndex) {
				foundDevice = device;
			}
		// Or search using the deviceId.
		} else if (null != deviceId) {
			if (device.getId().equalsIgnoreCase(deviceId)) {
				foundDevice = device;
			}
		}
	}

	/**
	 * Visits a concrete device. Visits the device as an
	 * AbstractDevice since the concrete type does not
	 * matter with this visitor.
	 * 
	 * @param device The device to visit.
	 */
	@Override
	public void visit(ConcreteDevice device) {
		visit((AbstractDevice)device);
	}


	/**
	 * Visits this container, and if this object is not the
	 * object to search, passes the visitor to the children.
	 * If the object to search is found, stops the search.
	 * 
	 * @param devices The device to visit.
	 */
	@Override
	public void visit(DeviceContainer devices) {
		visit((AbstractDevice)devices);
		if (null == foundDevice) {
			for (int i = 0; i < devices.getChildCount(); i++) {
				devices.getChild(i).accept(this);
				if (null != foundDevice) {
					break;
				}
			}
		}
	}

}
