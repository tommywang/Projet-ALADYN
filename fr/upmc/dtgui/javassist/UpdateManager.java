package fr.upmc.dtgui.javassist;

import fr.upmc.dtgui.annotations.*;
import javassist.*;

public class UpdateManager {
	
	public UpdateManager(){
	}
	
	public void update(Object ann, CtMethod ctm ){
		
		if (ann instanceof RealSensorData){
			RealSensorData annot = (RealSensorData)ann;
			if (annot.groupName().equals("position")){
				if (annot.unit() instanceof MeasurementUnit){
					if (annot.unit().name().equals("m")){
						if (ctm.getName().equals("getX")){
							
						}
					}
				}
			}
		}
	}
}
