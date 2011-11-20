package fr.upmc.dtgui.junit.board;

import java.lang.reflect.Modifier;

import org.junit.Test;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import fr.upmc.dtgui.annotations.RealActuatorData;
import fr.upmc.dtgui.annotations.RealSensorData;
import fr.upmc.dtgui.annotations.WithSensors;
import fr.upmc.dtgui.javassist.board.SpeedDisplayPanelJavassist;
import junit.framework.TestCase;

public class BoardSpeedDisplayPanelTest extends TestCase {

	@Override
	public void setUp() throws Exception {
		pool = ClassPool.getDefault();
		littleRobot=pool.get("fr.upmc.dtgui.tests.LittleRobot");
		//littleRobotBoard=pool.get("fr.upmc.dtgui.tests.RobotTeleoperationBoard");

		littleRobotBoard=pool.makeClass(littleRobot.getName()+"TeleoperationBoard");
		littleRobotBoard.setModifiers(Modifier.PUBLIC);
		littleRobotBoard.setSuperclass(pool.get("javax.swing.JPanel"));
		littleRobotBoard.addInterface(pool.get("fr.upmc.dtgui.gui.RobotTeleoperationBoard"));
		SpeedDisplayPanelJavassist sdp=new SpeedDisplayPanelJavassist();
		//System.out.println(littleRobot.getMethods()[0].getAnnotations()[0]);

		//SpeedDisplayPanelJavassist.create(pool, littleRobot, littleRobotBoard,(RealActuatorData)o);
		Object[] all;
		all = littleRobot.getAnnotations();
		for (int i=0; i<all.length; i++){
			if (all[i] instanceof WithSensors){
				CtMethod[] methods;
				methods=littleRobot.getMethods();
				Object[] alls;
				for (int j=0; j<methods.length; j++){
					alls=methods[j].getAnnotations();						
					if (alls.length>0){
						for (int k=0; k<alls.length; k++){	
							System.out.println(alls[k].toString());
							if (alls[k] instanceof RealSensorData){
								RealSensorData a = (RealSensorData)alls[k];
								if (a.groupName().equals("speed")){
									SpeedDisplayPanelJavassist.create(pool, littleRobot, littleRobotBoard,a);
								}
							}
							//bman.manageSensors(pool, littleRobot, alls[k]);
						}
					}
				}
			}
		}
	}

	private CtClass littleRobot;
	private CtClass littleRobotBoard;
	private ClassPool pool;
	private boolean classFound=true;

	@Test
	public void testMakeClass() throws Exception {
		try{
			pool.get("fr.upmc.dtgui.tests.LittleRobotTeleoperationBoard$SpeedDisplayPanel");
		}
		catch(Exception e){
			classFound=false;
			throw new Exception(e.toString());
		}
		assertTrue(classFound);
	}
}

