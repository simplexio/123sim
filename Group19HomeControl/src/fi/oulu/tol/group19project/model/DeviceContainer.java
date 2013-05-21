package fi.oulu.tol.group19project.model;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import android.location.Location;
import android.util.Log;

/**
 * DeviceContainer contains devices in a device hierarcy. Therefore, it
 * follows the Composite class role in the Composite design pattern.<p>
 * 
 * This class enables us to create a hierarchy of devices or hierarchical
 * groups of devices we can manipulate as one device.<p>
 * 
 * This class also supports the Visitor design pattern by implementing
 * the accept method for accepting visitors of different kind.
 * 
 * @author Antti Juustila
 * @see <a href="http://en.wikipedia.org/wiki/Composite_pattern">Composite design pattern</a>
 * @see Visitor
 *
 */
public class DeviceContainer extends AbstractDevice {

	/** A vector of devices */
	List<AbstractDevice> devices = new Vector<AbstractDevice>();
	
	/** Constructor, providing the possible parent object.
	 * 
	 * @param parent The parent of this object, may be null.
	 */
	protected DeviceContainer(AbstractDevice parent) {
		super(parent);
	}
	
	/** Constructor, providing the parent, and other member variable values.<p>
	 * 
	 *  All but the id of the parameters may be null. Usually, the name of the device is
	 *  given, though.<p>
	 * 
	 * @param parent Parent of this device, may be null.
	 * @param id The id for this device, a must to have.
	 * @param name The name for this devices.
	 * @param description The description of the device.
	 * @param location The location for the device.
	 */
	public DeviceContainer(AbstractDevice parent, String id, String name, String description, Location location) {
		super(parent, Type.CONTAINER, id, name, description, location);
	}

	/** Adds a child device to this parent device, setting the parent of the child to this.
	 * @param d Device to be added here.
	 */
	public void add(AbstractDevice d) {
		devices.add(d);
		d.setParent(this);
	}
	
	/**
	 * Removes a child from this container device.
	 * @param d A child to remove.
	 */
	public void remove(AbstractDevice d) {
		devices.remove(d);
		d.setParent(null);
	}
	
	/**
	 * Removes all devices from this container.
	 */
	public void removeAll() {
		for (AbstractDevice device : devices) {
			device.setParent(null);
		}
		devices.clear();
	}
	
	/**
	 * Gets the count of child devices in this container.<p>
	 * Note that this count includes only direct children,
	 * not children of children (remember the Composite pattern
	 * used there).
	 * 
	 * @return The count of child devices.
	 */
	public int getChildCount() {
		return devices.size();
	}

	/**
	 * Returns true if this object has children.
	 * 
	 * @return true, if has children.
	 */
	public boolean hasChildren() {
		return (devices.size() > 0);
	}

	/**
	 * Gets the index'th direct child object of this
	 * container. Does not consider children of the children.
	 * 
	 * @return The child device, null if not found.
	 */
	public AbstractDevice getChild(int index) {
		int counter = 0;
		for (AbstractDevice d : devices) {
			if (counter == index) {
				return d;
			}
			counter++;
		}
		return null;
	}
	
	/**
	 * Updates the device data from the parameter either in device,
	 * or if this is not the device in question, passes the device
	 * to the children to see if the device(s) is/are found there.<p>
	 * 
	 * If this is the device, takes the possible children from the parameter
	 * and passes those to children of this device.<p>
	 * 
	 * @param fromDevice The device containing possible new or changed data.
	 */
	public void updateValues(DeviceContainer fromDevice) {
		// TODO: if a device is not found, it should be added to the
		// structure in the correct place as a new device.
		// If this device is the one in fromDevice...
		if (getId().equals(fromDevice.getId())) {
			// ...update my values. Basically only the base class 
			// member some variables to update.
			super.updateValues(fromDevice);
			// Also remember to update my children, 
			// if the fromDevice has something to update there too.
			if (fromDevice.getChildCount() > 0) {
				for (AbstractDevice from : ((DeviceContainer)fromDevice).devices) {
					for (AbstractDevice my : devices) {
						my.updateValues(from);
					}
				}
			}
		} else {
			// not this device, pass to children to see
			// if one of those is the object needing update.
			for (AbstractDevice device : devices) {
				device.updateValues(fromDevice);
			}
		}
	}

	/**
	 * Provides a string representation of this device and it's children
	 * 
	 * @return Stringed data of this object, including children.
	 */
	public String toString() {
		String tmp = super.toString();
		for (AbstractDevice d : devices) {
			tmp += d.toString() + "\n";
		}
		return tmp;
	}

	/**
	 * For debugging, prints the contents of this object (incl. children) to the Log.d.
	 * 
	 */
	public void debugLog() {
		Log.d("DeviceGroup", getName() +" " + getDescription() + " Children: " + getChildCount());
		for (AbstractDevice d : devices) {
			d.debugLog();
		}
	}

	/**
	 * Sorts the child devices of this device, and also tells the
	 * children to sort themselves out.<p>
	 * 
	 * The sort order is defined by the compareTo -implementation.
	 */
	public void sort() {
		Collections.sort(devices);
		for (AbstractDevice d : devices) {
			d.sort();
		}
	}
	
	/**
	 * Returns an empty string since containers have no value.
	 * 
	 * @return An empty string.
	 */
	@Override
	public String getValueInformation() {
		return "";
	}
	
	/**
	 * Returns an empty string since containers have no value.
	 * 
	 * @return An empty string.
	 */
	@Override
	public String getValueString() {
		return "";
	}
	
	/**
	 * Required for the Visitor design pattern.
	 * We just invite it for a visitation to this object.
	 * 
	 * @param visitor The visitor to visit this object.
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
