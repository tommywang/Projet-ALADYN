package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class SteeringPanelJavassist {
	/**
	 * default constructor
	 */
	public SteeringPanelJavassist(){

	}

	/**
	 * create the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public void create(ClassPool pool, CtClass robot, CtClass board) throws CannotCompileException, NotFoundException{	
		CtClass stp = board.makeNestedClass("SteeringPanel", true);
		stp.setSuperclass(pool.get("javax.swing.JPanel"));

		/**
		 * @serialField serialVersionUID
		 */
		CtField svUID2 = new CtField(CtClass.longType, "serialVersionUID", stp);
		svUID2.setModifiers(Modifier.PRIVATE |Modifier.STATIC |Modifier.FINAL);
		stp.addField(svUID2,CtField.Initializer.constant(1L));

		CtField stdp = new CtField(pool.get(board.getName() + "$SteeringDisplayPanel"), "sdp", stp);
		stdp.setModifiers(Modifier.PROTECTED);
		stp.addField(stdp);

		CtField stcp = new CtField(pool.get(board.getName() + "$SteeringControllerPanel"), "stcp", stp);
		stcp.setModifiers(Modifier.PROTECTED);
		stp.addField(stcp);				

		CtField lr2 = new CtField(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"), "lr", stp);
		lr2.setModifiers(Modifier.PROTECTED);
		stp.addField(lr2);

		CtConstructor cons_stp = new CtConstructor(new CtClass[]{},stp);
		cons_stp.setBody(
				"{" +
						"$0.setLayout(new java.awt.BorderLayout()) ;" +
						"$0.setSize(450, 250) ;" +
						"$0.sdp = new " + board.getName() + "$SteeringDisplayPanel() ;" +
						"$0.scp = new " + board.getName() + "$SteeringControllerPanel() ;" +
						"$0.add(sdp, java.awt.BorderLayout.NORTH) ;" +
						"$0.add(scp, java.awt.BorderLayout.SOUTH) ;" +
						"$0.setVisible(true) ;"	+
				"}");
		stp.addConstructor(cons_stp);

		CtMethod dr2 = new CtMethod(CtClass.voidType, "disconnectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},stp);
		dr2.setBody(
				"{" +
						"$0.scp.disconnectRobot($1) ;" +
						"$0.lr = null ;" +
				"}");
		dr2.setModifiers(Modifier.PUBLIC);
		stp.addMethod(dr2);

		CtMethod cr2 = new CtMethod(CtClass.voidType, "connectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},stp);
		cr2.setBody(
				"{" +
						"$0.lr = $1 ;" +
						"$0.scp.connectRobot($1) ;" +
				"}");
		cr2.setModifiers(Modifier.PUBLIC);
		stp.addMethod(cr2);

		CtMethod usa = new CtMethod(CtClass.voidType, "updateSteeringAngle", new CtClass[]{pool.get(robot.getName()+"$SteeringData")},stp);
		usa.setBody(
				"{" +
						"$0.sdp.updateSteeringAngle($1) ;" +
				"}");
		usa.setModifiers(Modifier.PUBLIC);
		stp.addMethod(usa);
	}
	
	/**
	 * update the nested class SpeedDisplayPanel
	 * @param pool
	 * @param board
	 * @param ann
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public void update(ClassPool pool, CtClass board, Object ann) throws CannotCompileException, NotFoundException{	
	
	}	
}
