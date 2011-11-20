package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * 
 * @author Benoit GOEPFERT & Shiyue WANG
 *
 */
public class SteeringPanelJavassist {
	
	/**
	 * default constructor
	 */
	public SteeringPanelJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool the classpool that contains all the classes available at the loading of the current class
	 * @param robot the current robot
	 * @param board the TeleoperationBoard associated to the current robot
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void create(ClassPool pool, CtClass robot, CtClass board) throws CannotCompileException, NotFoundException{	
		
		/*create nested class SteeringPanel*/
		CtClass steeringPanel = board.makeNestedClass("SteeringPanel", true);
		steeringPanel.setSuperclass(pool.get("javax.swing.JPanel"));

		/* add field serialVersionUID */
		CtField serialVersionUID = new CtField(CtClass.longType, "serialVersionUID", steeringPanel);
		serialVersionUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
		steeringPanel.addField(serialVersionUID,CtField.Initializer.constant(1L));

		/* add field sdp */
		CtField sdp = new CtField(pool.get(board.getName() + "$SteeringDisplayPanel"), "sdp", steeringPanel);
		sdp.setModifiers(Modifier.PROTECTED);
		steeringPanel.addField(sdp);

		/* add field scp */
		CtField scp = new CtField(pool.get(board.getName() + "$SteeringControllerPanel"), "scp", steeringPanel);
		scp.setModifiers(Modifier.PROTECTED);
		steeringPanel.addField(scp);				

		/* add field lr */
		CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"), "lr", steeringPanel);
		lr.setModifiers(Modifier.PROTECTED);
		steeringPanel.addField(lr);

		/* add constructor */
		CtConstructor constructorSteeringPanel = new CtConstructor(new CtClass[]{},steeringPanel);
		constructorSteeringPanel.setModifiers(Modifier.PUBLIC);
		constructorSteeringPanel.setBody(
				"{" +
						"$0.setLayout(new java.awt.BorderLayout()) ;" +
						"$0.setSize(450, 250) ;" +
						"$0.sdp = new " + board.getName() + "$SteeringDisplayPanel() ;" +
						"$0.scp = new " + board.getName() + "$SteeringControllerPanel() ;" +
						"$0.add(sdp, java.awt.BorderLayout.NORTH) ;" +
						"$0.add(scp, java.awt.BorderLayout.SOUTH) ;" +
						"$0.setVisible(true) ;"	+
				"}");
		steeringPanel.addConstructor(constructorSteeringPanel);

		/* add method disconnectRobot */
		CtMethod disconnectRobot = new CtMethod(CtClass.voidType, "disconnectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},steeringPanel);
		disconnectRobot.setModifiers(Modifier.PUBLIC);
		disconnectRobot.setBody(
				"{" +
						"$0.scp.disconnectRobot($1) ;" +
						"$0.lr = null ;" +
				"}");
		steeringPanel.addMethod(disconnectRobot);

		/* add method connectRobot */
		CtMethod connectRobot = new CtMethod(CtClass.voidType, "connectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},steeringPanel);
		connectRobot.setModifiers(Modifier.PUBLIC);
		connectRobot.setBody(
				"{" +
						"$0.lr = $1 ;" +
						"$0.scp.connectRobot($1) ;" +
				"}");
		steeringPanel.addMethod(connectRobot);

		/* add method updateSteeringAngle */
		CtMethod updateSteeringAngle = new CtMethod(CtClass.voidType, "updateSteeringAngle", new CtClass[]{pool.get(robot.getName()+"$SteeringData")},steeringPanel);
		updateSteeringAngle.setModifiers(Modifier.PUBLIC);
		updateSteeringAngle.setBody(
				"{" +
						"$0.sdp.updateSteeringAngle($1) ;" +
				"}");
		steeringPanel.addMethod(updateSteeringAngle);
	}
}
