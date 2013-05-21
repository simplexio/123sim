package fi.oulu.tol.group19project.model;

import android.location.Location;
import android.util.Log;

/**
 * The ConcreteDevice class describes a device which can be queried of the
 * values of the device, and change them. The {@link AbstractDevice.Type}
 * defines if the device is a sensor or actuator. This information should
 * be used in the UI construction so that if the device is a sensor, no
 * method is provided to the user to change the value. If the device is an
 * actuator, then UI should provide a way to manipulate the device's value.<p> 
 * 
 * The value (on/off, temperature, lightning level, anything basically), 
 * is stored as a Double, enabling representation of null values.
 * boolean values (yes/no, on/off) can be represented by values of 0 and 1, with
 * max value of 1 and min value of 0.<p>
 * 
 * <strong>Since version 1.1.</strong>, ValueType supports configuring the object to represent
 * on/off values and decimal values more explicitly. See the changes in the
 * constructor, {@link ValueType} enum and new {@link getValueType()} method.<p>
 * 
 * When you parse the OHAP JSON, the "state" object contains a "type" element. 
 * The type can be either binary or decimal.
 * If the type is "binary", then set the valuetype of ConcreteDevice to ValueType.BINARY. If
 * the parsed type is "decimal", set the valueType to ValueType.DECIMAL<p>
 * 
 * <strong>Since version 1.1.</strong>, unitAbbreviation supports representing the unit with
 * a shorter string, useful in the UI. For example, unit might be "Celsius" for
 * temperature sensors, and the unitAbbreviation is then "C". See {@link setUnitAbbreviation(String)} and
 * {@link getUnitAbbreviation()}<p>
 * 
 * @author Antti Juustila
 *
 */
public class ConcreteDevice extends AbstractDevice {

	/**
	 * The type of the value. If the type of the value is BINARY,
	 * the Double value is interpreted as a boolean type, where
	 * zero is false and anything else (usually 1) is true.
	 * @author Antti Juustila
	 * @since 1.1
	 *
	 */
	public enum ValueType {
		/** Type of value is boolean. */
		BINARY, 
		/** Type of value is numeric. */
		DECIMAL 
	};
	
	/**
	 * The type of the value. Influences on how
	 * we interpret the value -- is it binary (on/off, e.g. a 
	 * lock of a door) or a numeric value (e.g. a temperature sensor). 
	 * Mostly this effects the UI -- for on/off control, you show a 
	 * button with on/off states, as with numeric values, you show
	 * a slider or numeric editor which you can use to check the
	 * device state (sensors) and/or manipulate it (actuators).
	 * 
	 * @since 1.1
	 */
	private ValueType valueType = ValueType.BINARY;
	
	/**
	 * The value of the device, which may be null.
	 */
	private Double value = null;
	
	/**
	 * The minimum possible value of the device value, may be null. 
	 */
	private Double minValue = null;
	
	/**
	 * The maximum possible value of the device value, may be null. 
	 */
	private Double maxValue = null;
	
	/**
	 * The unit of the value, e.g. with temperatures, it could be
	 * "Fahrenheit" or "Celsius".
	 */
	private String unit = null;
	
	/**
	 * Abbreviation for the unit. For example, value's unit might
	 * be "Celsius", and the abbreviation is "C".
	 * @since 1.1
	 */
	private String unitAbbreviation = null;
		
	/** 
	 * Constructor where you can provide the parent for the device.
	 * 
	 * @param parent The parent device of this device.
	 */
	public ConcreteDevice(AbstractDevice parent) {	
		super(parent);
	}
	
	/**
	 * Constructor for the concrete device
	 * Sets the member variables, calling the upper
	 * class constructor too.
	 * 
	 * @param parent The parent object
	 * @param type The type of the device.
	 * @param id The id for the device
	 * @param name The name (user visible) of the device
	 * @param description The device description.
	 * @param location The location where the device is.
	 * @param valueType The value type of the value.
	 * @param value The current value of the device
	 * @param minValue The minimum accepted value.
	 * @param maxValue The maximum accepted value.
	 * @param unit The measurement unit of the value.
	 */
	
	public ConcreteDevice(AbstractDevice parent, 
			Type type, 
			String id, 
			String name, 
			String description, 
			Location location, 
			ValueType valueType,
			Double value, 
			Double minValue, 
			Double maxValue, 
			String unit) {
		super(parent, type, id, name, description, location);
		this.valueType = valueType;
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.unit = unit;
	}
	
	/**
	 * Returns the value type of the device's value property.
	 * @return Value type enumeration.
	 * @since 1.1
	 */
	public ValueType getValueType() {
		return valueType;
	}
	
	/**
	 * Use this method to get the value of the device.
	 * Note that the value can be null!
	 * @return The value or null.
	 */
	public Double getValue() {
		return value;
	}
	
	/**
	 * Use this method to get the min possible value of the device.
	 * Note that the returned value can be null!
	 * @return The minimum value or null.
	 */
	public Double getMinValue() {
		return minValue;
	}

	/**
	 * Use this method to get the max possible value of the device.
	 * Note that the returned value can be null!
	 * @return The maximum value or null.
	 */
	public Double getMaxValue() {
		return maxValue;
	}
	
	/**
	 * Use this method to get the unit of the value of the device.
	 * If there is no unit, returns an empty string
	 * @return The unit string or empty string.
	 */
	public String getUnit() {
		if (unit != null) {
			return unit;
		}
		return "";
	}

	/**
	 * Use this method to get the unit abbreviation of the value of the device.
	 * If there is no unit, abbreviation, returns an empty string
	 * @return The unit abbreviation string or empty string.
	 * @since 1.1
	 */
	public String getUnitAbbreviation() {
		if (unitAbbreviation != null) {
			return unitAbbreviation;
		}
		return "";
	}
	
	/**
	 * Sets the unit abbreviation. There is a set for this property,
	 * since unit abbreviation is not included in the constructor
	 * parameter list. You can of course create your own set methods
	 * for all properties if you like.
	 * @param abbr The unit abbreviation string.
	 * @since 1.1
	 */
	public void setUnitAbbreviation(String abbr) {
		this.unitAbbreviation = abbr;
	}

	/**
	 * Sets the value of the actuator.
	 * @param value The new value.
	 */
	public void setValue(Double value) {
		this.value = value;
	}
	
	/**
	 * Sets the minimum value of the actuator.
	 * @param minValue The new min value.
	 */
	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}
	
	/**
	 * Sets the maximum value of the actuator.
	 * @param maxValue The new min value.
	 */
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * Use this method to get a descriptive string
	 * of the value, including the unit, if present.
	 * Current implementation returns "On" or "Off"
	 * in those cases where the value is either 0 or 1,
	 * and the min value is 0 and max value is 1.<p>
	 * 
	 * In some situations, though, you'd want to present
	 * value and limits 0 and 1 as such, not as boolean
	 * or "On/Off" values. In this case, just change this
	 * implementation to  your preference.
	 * 
	 * @return The value information or empty string.
	 */
	public String getValueInformation() {
		String tmp = "";
		if (null != value) {
			if (valueType == ValueType.BINARY) {
				tmp = value != 0.0 ? "On" : "Off";
				return tmp;
			} else {
				tmp = value.toString();
				if (null != unit) {
					tmp += unit;
				}
			}

		}
		return tmp;
	}
	
	/**
	 * Returns the value as string, without unit information.
	 * Returns an empty string if value is null.
	 * @return The value as a string.
	 */
	public String getValueString() {
		if (null != value) {
			return value.toString();
		}
		return "";
	}
	
	// Composite pattern
	/**
	 * Empty implementation of the Composite pattern method.
	 */
	public void add(AbstractDevice c) {
	}
	
	/**
	 * Empty implementation of the Composite pattern method.
	 */
	public void remove(AbstractDevice c) {
	}
	
	/**
	 * Empty implementation of the Composite pattern method.
	 */
	public void removeAll() {
	}
	
	/**
	 * Implementation of the Composite pattern method.
	 * Concrete devices have no children.
	 * @return Returns 0 for concrete device.
	 */
	public int getChildCount() {
		return 0;
	}
	
	/**
	 * Concrete children do not have children, so returns null
	 * 
	 * @param index The child index
	 * @return Returns null.
	 */
	public AbstractDevice getChild(int index) {
		return null;
	}

	/** Updates the values of a device/sensor from the provided parameter.<p>
	 * 
	 * It is important to notice that the parameter may have been created
	 * from a parsed json, that only includes changed values. Thus this
	 * object may include values from the first data sent by the server
	 * and we have to keep them. Update only those values of fromDevice
	 * which are not null. Those that are null, were not included in
	 * the provided json.
	 * 
	 * @param fromDevice The object where values are read.
	 */
	public void updateValues(AbstractDevice fromDevice) {
		if (getId().equals(fromDevice.getId())) {
			super.updateValues(fromDevice);
			ConcreteDevice cd = (ConcreteDevice)fromDevice;
			if (cd.getValue() != null) {
				value = cd.getValue();
			}
			if (cd.getMinValue() != null) {
				minValue = cd.getMinValue();
			}
			if (cd.getMaxValue() != null) {
				maxValue = cd.getMaxValue();
			}
			if (cd.getUnit() != null) {
				unit = cd.getUnit();
			}
		}
	}
	
	/**
	 * Returns a string representation of the device
	 * data, including data from the AbstractDevice class.
	 * @return String describing the object data.
	 */
	public String toString() {
		String tmp = super.toString();
		if (null != value) {
			tmp += " Value: " + value;
		}
		if (null != unit) {
			tmp += unit;
		}
		return tmp;
	}
	
	/**
	 * Prints out the object as string to the Log.
	 */
	public void debugLog() {
		Log.d("ConcreteDevice", toString());
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