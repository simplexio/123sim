package fi.oulu.tol.group19project.ohap;

import fi.oulu.tol.group19project.model.AbstractDevice;
import fi.oulu.tol.group19project.model.ConcreteDevice;
import fi.oulu.tol.group19project.model.DeviceContainer;
import fi.oulu.tol.group19project.model.Visitor;

/**
 * This class builds an OHAP path to be used when sending SET, GET and LISTEN and UNLISTEN
 * commands to the OHAP central unit.<p>
 *
 * The path follows this structure:
 * <pre>LISTEN /container:room-1/actuator:light-1</pre>
 * when using GET, LISTEN and UNLISTEN. The path contains a device id with type
 * information.<p>
 * When setting values of a device, with the SET command, the path also
 * includes the new value for the device. For example:<p>
 * <pre>SET /container:room-1/actuator:light-1/state/value true</pre>
 *
 * @author Antti Juustila
 *
 */
public class OHAPPathBuilder implements Visitor {
 
  private static final String STATE = null;
private static final String VALUE = null;
private static final String ACTUATOR = null;
private static final String SENSOR = null;
private static final String CONTAINER = null;
/** The buffer used in building the path string. */
  private StringBuffer path = null;
  /** Should the value of a concrete object put in the path or not. */
  private boolean putValueInPath = false;
 
  /**
   * Call this method to get the path for a device.<p>
   * If the device is a concrete device and the withValue parameter
   * indicates that value should be included in the path, it is put
   * there. Otherwise, only the device hierarchy is included in
   * the path.
   * @param device The device to get path for.
   * @param withValue Do we want the value of the device in the path.
   * @return The path string.
   */
  public String createPath(AbstractDevice device, boolean withValue) {
    if (null != path) {
      path = null;
    }
    path = new StringBuffer();
    putValueInPath = withValue;
    device.accept(this);
    return path.toString();
  }
 
  /**
   * Constructs a path in to the buffer, using the device's
   * id and type strings.
   * @param id The device's id.
   * @param type The device's type.
   */
  private void addPath(String id, String type) {
    path.insert(0, id);    // "dev-id"
    path.insert(0, ":");  // ":dev-id"
    path.insert(0, type);  // "sensor:dev-id"
    path.insert(0, "/");  // "/sensor:dev-id"
  }
 
  /**
   * Adds the device's value to the path.
   * If the device's value type is binary,
   * adds "true" if value != 0.0, else adds false. For decimal
   * value types, adds the number as double.
   * @param device The device.
   */
  private void addValue(ConcreteDevice device) {
    path.append("/" + STATE + "/" + VALUE + " ");  // "/state/value "
    if (device.getValueType() == ConcreteDevice.ValueType.BINARY) {
      path.append(device.getValue() != 0.0 ? "true" : "false");     // "/state/value true"
    } else {
      path.append(device.getValue());                  // "/state/value 21.123"
    }
  }
 
  /** Handles the navigation to the parent object, if one exists.
   * @param device The device to visit.
   */
  @Override
  public void visit(AbstractDevice device) {
    AbstractDevice parent = device.getParent();
    if (null != parent) {
      parent.accept(this);
    }
  }
 
  /**
   * Visits a concrete device and puts the value (optionally)
   * and path of the device to the path buffer.
   * @param device The device to visit.
   */
  @Override
  public void visit(ConcreteDevice device) {
    if (putValueInPath) {
      addValue(device);
    }
    String devType = null;
    if (device.getType() == AbstractDevice.Type.ACTUATOR) {
      devType = ACTUATOR;
    } else if (device.getType() == AbstractDevice.Type.SENSOR) {
      devType = SENSOR;
    }
    addPath(device.getId(), devType);
    visit((AbstractDevice)device);
  }
 


@Override
public void visit(DeviceContainer device) {
    addPath(device.getId(), CONTAINER);
    visit((AbstractDevice)device);	
}
 
}