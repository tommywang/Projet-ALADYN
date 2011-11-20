package fr.upmc.dtgui.javassist;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class PersonalizedGUIJavassist {

	public PersonalizedGUIJavassist(){		
	}

	/** initial creation of the class EnergyData and associated elements in the robot */
	public static void update(ClassPool pool, CtClass robot) throws CannotCompileException, RuntimeException, NotFoundException{

		CtClass pgui = pool.get("fr.upmc.dtgui.gui.PersonalizedGUI");

		//method createBoard
		CtMethod cb = pool.getMethod("fr.upmc.dtgui.gui.PersonalizedGUI", "createBoard");
		cb.setBody(
				"{\n" +
						"fr.upmc.dtgui.gui.RobotTeleoperationBoard board;\n" +
						"board = new "+ robot.getName() + "TeleoperationBoard($0, $0.sizeX - 50);\n" +
						"return board ;\n" +
				"}\n");

		//method createSensorDataReceptor
		CtMethod csdr = pool.getMethod("fr.upmc.dtgui.gui.PersonalizedGUI", "createSensorDataReceptor");
		csdr.setBody(
				"{\n" +
						"fr.upmc.dtgui.gui.SensorDataReceptorInterface sdr = null ;\n" +
						"sdr = $2.makeSensorDataReceptor(" +
							"$0.positionDisplay, $1.getSensorDataQueue()," +
							"$0.absoluteX, $0.absoluteY, $0.controlRadius" +
						") ;\n" +
						"return sdr ;\n" +						
				"}\n");						

		//method detectRobot
		CtMethod dr = new CtMethod(CtClass.voidType, "detectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")}, pgui);
		dr.setBody(
				"{\n" +
						"fr.upmc.dtgui.gui.RobotTeleoperationBoard board = null ;\n" +
						"fr.upmc.dtgui.gui.SensorDataReceptorInterface sdr = null ;\n" +
						"if (!this.detected($1)) {\n" +
							"board = $0.createBoard($1) ;\n" +
							"sdr = $0.createSensorDataReceptor($1, board) ;\n" +
							"$0.sensors.put($1, sdr) ;\n" +
							"$0.boards.put($1, board) ;\n" +
							"sdr.start() ;\n" +
							"this.validate() ;\n" +
						"}\n" +	
				"}\n");
		pgui.addMethod(dr);
	}

}
