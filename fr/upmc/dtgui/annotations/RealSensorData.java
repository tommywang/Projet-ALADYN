//	SensorData.java --- 

package fr.upmc.dtgui.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * The annotation <code>RealSensorData</code> tags getter methods of a
 * robot with information concerning how to use the corresponding real data
 * in the robot representation as a property that can be observed to look
 * into the robot's state.
 *
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	minWritingRate() <= maxWritingRate()
 * </pre>
 * 
 * <p>Created on : 2011-09-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
public @interface RealSensorData {

	/**
	 * A group name allowing to put together into one aggregated
	 * sensor with several data.
	 */
	String			groupName() ;
	/**
	 * A measurement unit to interpret the data.
	 */
	MeasurementUnit	unit() ;
	/**
	 * The range of admissible values for the data.
	 */
	RealRange		dataRange() ;
	/**
	 * An indication of the way the data varies over time: increasing,
	 * decreasing or randomly.
	 */
	VariationType	variation() ;
	/**
	 * The maximum frequency, in Hz, at which the sensor can be called.
	 */
	double			maxReadingRate() ; // in Hz
	/**
	 * The minimum frequency, in Hz, at which the sensor must be called.
	 */
	double			minReadingRate() ; // in Hz

}

// $Id$