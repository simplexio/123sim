package fi.oulu.tol.group19project.model;

import android.location.Location;
import android.util.Log;

/**
 * AbstractDevice holds all the common properties of different devices 
 * in the home control application. It is used as an abstract class
 * in containers and in handling all generic functionality for it's
 * subclasses.<p>
 * 
 * AbstractDevice implements the Comparable interface so that we can
 * sort the devices when shown in a list view. The class also supports
 * the Visitor design pattern by implementing the accept method. It also
 * acts as the abstract base class for the Composite design pattern, and
 * declares the abstract child handling methods for the Composite.<p>
 * 
 * The member variables for the device(s) are null by default. This enables
 * us to handle those properties of the device we didn't get information
 * about from the server. This is because sometimes server can send only
 * some set of data about a device, not all of it. Since there are no
 * reasonable "not set" values for many properties, null is used instead.<p>
 * 
 * @author Antti Juustila
*/
public abstract class AbstractDevice implements Comparable<AbstractDevice> {

	/**
	 * The enumeration for different device types.
	 * @author Antti Juustila
	 */
	public enum Type {
		/** Sensor type */
		SENSOR,
		/** Actuator type */
		ACTUATOR,
		/** Container type */
		CONTAINER
	};

	/**
	 * The type of this device.
	 */
	private Type type = Type.SENSOR;

	/** 
	 * The unique ID for a device. Must be unique within a home.
	 * A device must always have this value.
	 */
	private String id = null;
	/**
	 * The user visible name for the device.
	 */
	private String name = null;
	/**
	 * The gps location for the device.
	 */
	private Location location = null;
	/**
	 * User visible description for the device.
	 */
	private String description = null;
	
	/**
	 * The parent device of this device.
	 */
	private AbstractDevice parent = null;

	/**
	 * The constructor with no property values.
	 * The {@link DeviceContainer} sets the parent of AbstractDevice,
	 * when {@link DeviceContainer#add(AbstractDevice)} is called.
	 * @param parent The parent of this device. Can be null, when there is no parent.
	 */
	protected AbstractDevice(AbstractDevice parent) {
		this.parent = parent;
	}
	
	/**
	 * The constructor with parameters for the properties.
	 * Each of the properties (except for id) can be null.
	 * 
	 * @param parent The parent device of this device.
	 * @param type The type of the device.
	 * @param id The id for this device. Cannot be null.
	 * @param name The device's name.
	 * @param description The device's description.
	 * @param location The GPS location for the device.
	 */
	protected AbstractDevice(AbstractDevice parent, Type type, String id, String name, String description, Location location) {
		this.parent = parent;
		this.type = type;
		this.id = id;
		this.name = name;
		this.description = description;
		this.location = location;
	}

	/**
	 * Get the concrete type of the device.
	 * @return The type enum.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set the device's type.
	 * @param type The device type.
	 */
	public void setType(Type type) {
		this.type = type;
	}


	/**
	 * Returns the parent device of this device. 
	 * @return Parent object, can be null if there is no parent.
	 */
	public AbstractDevice getParent() {
		return parent;
	}
	
	/**
	 * Sets the parent device of this device.
	 * The DeviceContainer sets the parent of AbstractDevice,
	 * when {@link DeviceContainer#add(AbstractDevice)} is called.
	 * @param parent Parent. Can be null.
	 */
	public void setParent(AbstractDevice parent) {
		this.parent = parent;
	}
	
	/** 
	 * Use this method to check if the device has a parent.
	 * @return Returns true if the device has a parent device.
	 */
	public boolean hasParent() {
		return parent != null;
	}
	
	/**
	 * Returns the id of the device.
	 * @return Id of the device.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets an id for the device.
	 * @param id The new id for the device.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the description for the device.
	 * If the device does not have a description, returns
	 * an empty string instead of null. This is because it is easier
	 * to display the data when you do not have to check against null in UI code.
	 * @return The device desription.
	 */
	public String getDescription() {
		if (description != null)
		{
			return description;
		}
		return "";
	}

	/**
	 * Set the description of the device.
	 * @param description The new description. Can be null.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set the name of the device.
	 * @param n The new name for the device. Can be null.
	 */
	public void setName(String n) {
		name = n;
	}
	
	/**
	 * Returns the name of the device.
	 * If the device does not have a name, returns
	 * an empty string instead of null. This is because it is easier
	 * to display the data when you do not have to check against null in UI code.
	 * @return The device name.
	 */
	public String getName() {
		if (name != null) {
			return name;
		}
		return "";
	}

	/**
	 * Sets the location for the device.
	 * @param l The new location for the device.
	 */
	public void setLocation(Location l) {
		location = l;
	}
		
	/**
	 * Gets the location for the device.
	 * @return Location, can be null.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Gets the location of the device as string.
	 * If the device does not have a location, returns an empty string.
	 * @return The device location string (or empty string).
	 */
	public String getLocationString() {
		if (location != null) {
			return Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + ", "
					+ Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
		}
		return "";
	}
	
	
	/**
	 * Returns the state of the device in a string.
	 * Includes the on/off state, name, description and location of the 
	 * string, if values exist for these.
	 * @return The device's state as string.
	 */
	public String toString() {
		return getName() + " " + getDescription()+ " " + getLocationString();
	}
	
	/**
	 * For getting the string of value information to be
	 * displayed on lists etc. on UI, with unit. AbstractDevice does
	 * not have values, so this is implemented in concrete
	 * subclasses with actual values. The string contains 
	 * both the values and possible units, e.g. "-21.3C".
	 * @return The values string.
	 */
	abstract public String getValueInformation();
	
	/** 
	 * For getting the value string without unit data.
	 * This is an abstract method, implemented by subclasses.
	 * 
	 * @return The value of the data in the object.
	 */
	abstract public String getValueString();

	/**
	 * For debugging, prints out the toString() representation
	 * of the object to Log.d.
	 */
	public void debugLog() {
		Log.d("AbstractDevice", toString());
	}
	
	/**
	 * Updates the values from the provided device.<p>
	 * 
	 * Checks if the fromDevice has the corresponding value,
	 * does not update if the fromDevice's value is null, keeping
	 * the possible previously aqcuired value.<p>
	 * 
	 * Derived class must check if the id of the fromDevice equals 
	 * the id of this object. Thus there is no need to that check here.<p>
	 * 
	 * @param fromDevice The object with new values.
	 */
	protected void updateValues(AbstractDevice fromDevice) {
		if (fromDevice.getName() != null && fromDevice.name.length() > 0) {
			setName(fromDevice.getName());
		}
		if (fromDevice.getDescription() != null && fromDevice.description.length() > 0) {
			setDescription(fromDevice.getDescription());
		}
		if (fromDevice.getLocation() != null) {
			setLocation(fromDevice.getLocation());
		}
	}

	/**
	 * Compares this object to the one in the parameter.
	 * Currently this is used when sorting the devices in
	 * a {@link DeviceContainer} so that first there are devices with
	 * no child objects and then there are devices with
	 * child objects (child count in asceding order).
	 * 
	 * @param device The device to compare to.
	 */
	public int compareTo(AbstractDevice device) {
		return this.getChildCount() - device.getChildCount();
	}
	

	/**
	 * Utility method to query if the device has child devices.
	 * @return true, if has children, otherwise false.
	 */
	public boolean hasChildren() {
		return false;
	}
	
	/**
	 * Abstract method for adding children, related to
	 * the Composite design pattern. Concrete subclasses must
	 * implement this method according to it's role in the
	 * pattern.
	 * @param c The device to add.
	 */
	abstract public void add(AbstractDevice c);

	/**
	 * Abstract method for removing children, related to
	 * the Composite design pattern. Concrete subclasses must
	 * implement this method according to it's role in the
	 * pattern.
	 * @param c The device to remove.
	 */
	abstract public void remove(AbstractDevice c);
	
	/**
	 * Abstract method for removing all children, related to
	 * the Composite design pattern. Concrete subclasses must
	 * implement this method according to it's role in the
	 * pattern.
	 */
	abstract public void removeAll();
	
	/**
	 * Abstract method for getting the count of children in 
	 * this object, related to the Composite design pattern. 
	 * Concrete subclasses must implement this method according 
	 * to it's role in the pattern.
	 * 
	 * @return The count of children in this object.
	 */
	abstract public int getChildCount();
	
	/** 
	 * Gets the child from the specified index.
	 * @param index The index of the child object.
	 * @return The child, null if not found.
	 */
	abstract public AbstractDevice getChild(int index);
	
	
	/**
	 * We need the sort interface also to this class, due to Composite
	 * pattern. {@link DeviceContainer} composite may include also DeviceContainer, and we
	 * need to be able to tell the children of DeviceContainer to sort themselves.
	 * Since DeviceContainer contains AbstractDevices, we need the abstract sort 
	 * method declared already here.
	 */
	public void sort() {
		// nada
	}

	/**
	 * Required for the Visitor design pattern.
	 * 
	 * @param visitor The visitor to visit this object.
	 * We just invite it for a visitation to this object.
	 */
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}