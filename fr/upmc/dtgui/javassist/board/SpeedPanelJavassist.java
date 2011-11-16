/**
 * 
 */
package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import fr.upmc.dtgui.annotations.RealSensorData;

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
	public void create(ClassPool pool, CtClass robot, CtClass board, RealSensorData annot) throws CannotCompileException, NotFoundException{	
		/**
		 * create class SpeedPanel
		 */
		CtClass spp = board.makeNestedClass("SpeedPanel", true);
		spp.setSuperclass(pool.get("javax.swing.JPanel"));

		/**
		 * @serialField serialVersionUID
		 */
		CtField svUID = new CtField(CtClass.longType, "serialVersionUID", spp);
		svUID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
		spp.addField(svUID,CtField.Initializer.constant(1L));

		/**
		 * add field SpeedDisplayPanel
		 */
		CtField spdp = new CtField(pool.get(board.getName() + "$SpeedDisplayPanel"), "sdp", spp);
		spdp.setModifiers(Modifier.PROTECTED);
		spp.addField(spdp);

		/**
		 * add field SpeedControllerPanel
		 */
		CtField spcp = new CtField(pool.get(board.getName() + "$SpeedControllerPanel"), "scp", spp);
		spcp.setModifiers(Modifier.PROTECTED);
		spp.addField(spcp);				

		/**
		 * add field lr
		 */
		CtField lr = new CtField(pool.get("fr.upmc.dtgui.robot.InstrumentedRobot"), "lr", spp);
		lr.setModifiers(Modifier.PROTECTED);
		spp.addField(lr);

		/**
		 * add constructor
		 */
		CtConstructor cons_spp = new CtConstructor(new CtClass[]{},spp);
		cons_spp.setBody(
				"{" +
						"$0.setLayout(new java.awt.BorderLayout()) ;" +
						"$0.setSize(450, 250) ;" +
						"$0.sdp = new " + board.getName() + "$SpeedDisplayPanel() ;" +
						"$0.scp = new " + board.getName() + "$SpeedControllerPanel() ;" +
						"$0.add(sdp, java.awt.BorderLayout.NORTH) ;" +
						"$0.add(scp, java.awt.BorderLayout.SOUTH) ;" +
						"$0.setVisible(true) ;"	+
				"}");
		spp.addConstructor(cons_spp);

		CtMethod dr = new CtMethod(CtClass.voidType, "disconnectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},spp);
		dr.setBody(
				"{" +
						"$0.scp.disconnectRobot($1) ;" +
						"$0.lr = null ;" +
				"}");
		dr.setModifiers(Modifier.PUBLIC);
		spp.addMethod(dr);

		CtMethod cr = new CtMethod(CtClass.voidType, "connectRobot", new CtClass[]{pool.get("fr.upmc.dtgui.robot.InstrumentedRobot")},spp);
		dr.setBody(
				"{" +
						"$0.lr = $1 ;" +
						"$0.scp.connectRobot($1) ;" +
				"}");
		cr.setModifiers(Modifier.PUBLIC);
		spp.addMethod(dr);

		CtMethod usa = new CtMethod(CtClass.voidType, "updateSpeed", new CtClass[]{pool.get(robot.getName()+"$SpeedData")},spp);
		usa.setBody(
				"{" +
						"$0.sdp.updateSpeed($1) ;" +
				"}");
		usa.setModifiers(Modifier.PUBLIC);
		spp.addMethod(usa);
	}
}
