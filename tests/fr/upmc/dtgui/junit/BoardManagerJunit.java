package fr.upmc.dtgui.junit;

import junit.framework.TestCase;
import org.junit.Test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import fr.upmc.dtgui.annotations.WithSensors;
import fr.upmc.dtgui.javassist.board.BoardManager;
import fr.upmc.dtgui.javassist.robot.RobotManager;


public class BoardManagerJunit extends TestCase {
	@Override
	public void setUp() throws Exception {
		pool = ClassPool.getDefault();
		littleRobot=pool.get("fr.upmc.dtgui.tests.LittleRobot");
		anotherLittleRobot=pool.get("fr.upmc.dtgui.tests.AnotherLittleRobot");
		
	}
	
	private CtClass littleRobot;
	private CtClass anotherLittleRobot;
	private ClassPool pool;
	private boolean classFoundRobot=true;
	private boolean classFoundAnotherRobot=true;
	
	public void testLittleRobotTeleoperationBoard() throws ClassNotFoundException, CannotCompileException, RuntimeException, NotFoundException {
		BoardManager bman=new BoardManager();
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
							bman.manageSensors(pool, littleRobot, alls[k]);
						}
					}
				}
			}
		}
		
	}
	
	
	
	
	
}
