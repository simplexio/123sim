package fi.oulu.tol.group19project;

public class Device {

	public enum Type { SENSOR, ACTUATOR };
	
	private Type type = Type.SENSOR;
	private String name;
	private String description;
	
	public Device(Type type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
}
