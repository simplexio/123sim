package fi.oulu.tol.group19project.model;

/**
 * DeviceCounter is a {@link Visitor} which goes through a device object
 * structure and counts the number of devices the structure contains.<p>
 * 
 * Counter separately counts the child objects
 * and container objects (with children). The container
 * objects are included in the child count though.<p>
 * 
 * You can use this counter in the DeviceAdapter to provide the count
 * of rows (devices) to the list view.<p>
 * 
 * Usage:
 * <pre><code>
 * DeviceContainer devices = new DeviceContainer();
 * ...
 * DeviceCounter counter = new DeviceCounter();
 * ...
 * int count = counter.startCounting(devices);
 * </pre></code> 
 * 
 * @author Antti Juustila
 */
public class DeviceCounter implements Visitor {

	/**
	 * The count of all the devices in the structure.
	 */
	private int childCount = 0;
	/**
	 * The count of DeviceContainers in the structure. This
	 * value is included in the childCount.
	 */
	private int groupCount = 0;

	/** 
	 * Counts all the groups and children of all groups.
	 * @param device The structure where to start the search from.
	 * @return The count of devices in the structure.
	 */
	public int startCounting(AbstractDevice device) {
		childCount = 0;
		groupCount = 0;
		device.accept(this);
		return childCount;
	}

	/**
	 * Get the number of container devices in the structure.
	 * @return The number of container devices.
	 */
	public int getGroupCount() {
		return groupCount;
	}
	
	/**
	 * Nothing to do here, AbstractDevice is an abstract class
	 * so instances of just it cannot exist. All relevant code is
	 * in subclasses.
	 * @param device Not used.
	 */
	@Override
	public void visit(AbstractDevice device) {
	}

	/**
	 * Adds the childCount with one.
	 * @param device The sensor to visit.
	 */
	@Override
	public void visit(ConcreteDevice device) {
		childCount++;
	}

	/**
	 * Adds the childCount and groupCount with one.
	 * Passes the counter visitor to the children.
	 * @param devices The container device to visit.
	 */
	@Override
	public void visit(DeviceContainer devices) {
		groupCount++;
		childCount++;
		for (int i = 0; i < devices.getChildCount(); i++) {
			devices.getChild(i).accept(this);
		}
	}

}
