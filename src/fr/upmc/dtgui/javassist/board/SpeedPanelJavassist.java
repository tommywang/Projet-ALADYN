/**
 * 
 */
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
 * @author Benoit GOEPFERT & Shiyue WANG
 *
 */
public class SpeedPanelJavassist {


	/**
	 * default constructor
	 */
	public SpeedPanelJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void create(ClassPool pool, CtClass robot, CtClass board) throws CannotCompileException, NotFoundException{	
		
		/* create class SpeedPanel */
		CtClass speedPanel = board.makeNestedClass("SpeedPanel", true);
		speedPanel.setSuperclass(pool.get("javax.swing.JPanel"));

		/* add field serialVersionUID */
		CtField serialVersionUID = new CtField(CtClass.longType, "serialVersionUID", speedPanel);
		serialVersionUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
		speedPanel.addField(serialVersionUID,CtField.Initializer.constant(1L));
		
		/* add field SpeedDisplayPanel */
		CtField sdp = new CtField(pool.get(board.getName() + "$SpeedDisplayPanel"), "sdp", speedPanel);
		sdp.setModifiers(Modifier.PROTECTED);
		speedPanel.addField(sdp);

		/* add field SpeedControllerPanel */
		CtField scp = new CtField(pool.get(board.getName() + "$SpeedControllerPanel"), "scp", speedPanel);
		scp.setModifiers(Modifier.PROTECTED);
		speedPanel.addField(scp);				

		/* add field lr */
		CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"), "lr", speedPanel);
		lr.setModifiers(Modifier.PROTECTED);
		speedPanel.addField(lr);

		/* add constructor */
		CtConstructor constructorSpeedPanel = new CtConstructor(new CtClass[]{},speedPanel);
		constructorSpeedPanel.setModifiers(Modifier.PUBLIC);		
		//CtClass he= pool.get(board.getName() + "$SpeedDisplayPanel() ;");
		//System.out.println(he);
		constructorSpeedPanel.setBody(
				"{" +
						"$0.setLayout(new java.awt.BorderLayout()) ;" +
						"$0.setSize(450, 250) ;" +
						"$0.sdp = new " + board.getName() + "$SpeedDisplayPanel() ;" +
						"$0.scp = new " + board.getName() + "$SpeedControllerPanel() ;" +
						"$0.add(sdp, java.awt.BorderLayout.NORTH) ;" +
						"$0.add(scp, java.awt.BorderLayout.SOUTH) ;" +
						"$0.setVisible(true) ;"	+
				"}");
		speedPanel.addConstructor(constructorSpeedPanel);

		/* add method disconnectRobot */
		CtMethod disconnectRobot = new CtMethod(CtClass.voidType, "disconnectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},speedPanel);
		disconnectRobot.setModifiers(Modifier.PUBLIC);
		disconnectRobot.setBody(
				"{" +
						"$0.scp.disconnectRobot($1) ;" +
						"$0.lr = null ;" +
				"}");
		speedPanel.addMethod(disconnectRobot);

		/* add method connectRobot */
		CtMethod connectRobot = new CtMethod(CtClass.voidType, "connectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},speedPanel);
		connectRobot.setModifiers(Modifier.PUBLIC);
		connectRobot.setBody(
				"{" +
						"$0.lr = $1 ;" +
						"$0.scp.connectRobot($1) ;" +
				"}");
		speedPanel.addMethod(connectRobot);

		/* add method updateSpeed */
		CtMethod updateSpeed = new CtMethod(CtClass.voidType, "updateSpeed", new CtClass[]{pool.get(robot.getName()+"$SpeedData")},speedPanel);
		updateSpeed.setModifiers(Modifier.PUBLIC);
		updateSpeed.setBody(
				"{" +
						"$0.sdp.updateSpeed($1) ;" +
				"}");
		speedPanel.addMethod(updateSpeed);
	}
}
