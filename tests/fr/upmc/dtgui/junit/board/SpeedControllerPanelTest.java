package fr.upmc.dtgui.junit.board;

import java.lang.reflect.Modifier;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import org.junit.Test;

import fr.upmc.dtgui.annotations.RealActuatorData;
import fr.upmc.dtgui.javassist.board.ActuatorDataSenderJavassist;
import fr.upmc.dtgui.javassist.board.SpeedActuatorDataListenerJavassist;
import fr.upmc.dtgui.javassist.board.SpeedControllerPanelJavassist;
import fr.upmc.dtgui.javassist.robot.RobotManager;
import junit.framework.TestCase;

public class SpeedControllerPanelTest extends TestCase {
	@Override
	public void setUp() throws Exception {
		pool = ClassPool.getDefault();
		littleRobot=pool.get("fr.upmc.dtgui.tests.LittleRobot");
		littleRobotBoard=pool.makeClass(littleRobot.getName()+"TeleoperationBoard");
		littleRobotBoard.setModifiers(Modifier.PUBLIC);
		littleRobotBoard.setSuperclass(pool.get("javax.swing.JPanel"));
		littleRobotBoard.addInterface(pool.get("fr.upmc.dtgui.gui.RobotTeleoperationBoard"));
		CtMethod[] methods=littleRobot.getMethods();
		Object o=new Object();
		for (CtMethod m:methods){
			if (m.getAnnotations().length>0){
				if (m.getAnnotations()[0] instanceof RealActuatorData){
					o=m.getAnnotations()[0];
					break;
				}
			}
		}
		RobotManager rman=new RobotManager();
		rman.manageActuators(pool, littleRobot, o);
		ActuatorDataSenderJavassist.create(pool, littleRobotBoard);
		SpeedActuatorDataListenerJavassist.create(pool, littleRobot, littleRobotBoard);
		SpeedControllerPanelJavassist.create(pool,littleRobotBoard,o);
	}

	private CtClass littleRobot;
	private CtClass littleRobotBoard;
	private ClassPool pool;
	private boolean classFound=true;

	@Test
	public void testMakeClassAndMethod() 
			throws Throwable{
		try{
			classFound=false;
			pool.get("fr.upmc.dtgui.tests.LittleRobotTeleoperationBoard$SpeedControllerPanel");
			CtMethod[] methods=pool.get("fr.upmc.dtgui.tests.LittleRobotTeleoperationBoard$SpeedControllerPanel").getMethods();
			for (CtMethod m:methods){
				if (m.getName().equals("connectRobot")){
					classFound=true;
				}
			}
		}
		catch(Exception e){
			classFound=false;
			//throw new Exception(e.toString());
		}
		assertTrue(classFound);
	}
}
