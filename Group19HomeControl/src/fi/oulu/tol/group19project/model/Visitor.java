package fi.oulu.tol.group19project.model;

/**
 * The model of the HomeControl application supports the Visitor 
 * design pattern. The idea is to provide the structure a possibility
 * to add functionality to the model by adding new Visitors. 
 * Visitors can visit all the model data types (classes like 
 * {@link AbstractDevice}, {@link ConcreteDevice} etc) and do actions on them.<p> 
 * 
 * As a visitor visits an object, it calls it's methods to fulfill
 * some goal or task. See {@link fi.oulu.tol.homecontrol.model.DeviceCounter} 
 * and {@link DeviceFetcher} visitors for concrete examples.<p>
 * 
 * 
 * @author Antti Juustila
 * @see <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor design pattern</a>
 *
 */
public interface Visitor {
	/**
	 * Visiting method for visiting {@AbstractDevice}s
	 * @param device The device to visit.
	 */
	abstract void visit(AbstractDevice device);
	/**
	 * Visiting method for visiting {@ConcreteDevice}s
	 * @param device The device to visit.
	 */
	abstract void visit(ConcreteDevice device);
	/**
	 * Visiting method for visiting {@DeviceContainer}s
	 * @param device The device to visit.
	 */
	abstract void visit(DeviceContainer device);
}
