package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class SensorDataReceptorJavassist {

	public SensorDataReceptorJavassist(){
		
	}
	
	public void create(ClassPool pool, CtClass board, Object ann) throws CannotCompileException, NotFoundException{
		/**
		 * class SensorDataReceptor
		 */
		CtClass sdr = board.makeNestedClass("SensorDataReceptor", true);
		sdr.setSuperclass(pool.get("java.lang.Thread"));
		sdr.addInterface(pool.get("fr.upmc.dtgui.gui.SensorDataReceptorInterface"));

		/**
		 * add field
		 */
		CtField posd = new CtField(pool.get("fr.upmc.dtgui.gui.PositionDisplay"),"positionDisplay", sdr);
		posd.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		sdr.addField(posd);

		/**
		 * add field
		 */
		CtField tb = new CtField(board,"tBoard", sdr);
		tb.setModifiers(Modifier.PROTECTED);
		sdr.addField(tb);

		/**
		 * add field
		 */
		CtField dq = new CtField(pool.get("java.util.concurrent.BlockingQueue"),"dataQueue", sdr);
		dq.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		sdr.addField(dq);

		/**
		 * add field
		 */
		CtField ax = new CtField(CtClass.intType,"absoluteX", sdr);
		ax.setModifiers(Modifier.PROTECTED);
		sdr.addField(ax);

		/**
		 * add field
		 */
		CtField ay = new CtField(CtClass.intType,"absoluteY", sdr);
		ay.setModifiers(Modifier.PROTECTED);
		sdr.addField(ay);

		/**
		 * add field
		 */
		CtField cr = new CtField(CtClass.intType,"controlRadius", sdr);
		cr.setModifiers(Modifier.PROTECTED);
		sdr.addField(cr);

		/**
		 * add field shouldContinue
		 */
		CtField sc = new CtField(CtClass.booleanType,"shouldContinue", sdr);
		sc.setModifiers(Modifier.PROTECTED);
		sdr.addField(sc);

		/**
		 * add constructor
		 */
		CtClass[] args_sdr = new CtClass[]{
				pool.get("fr.upmc.dtgui.gui.PositionDisplay"),
				pool.get("java.util.concurrent.BlockingQueue"),
				CtClass.intType,
				CtClass.intType,
				CtClass.intType
		};
		CtConstructor cons_sdr = new CtConstructor(args_sdr, sdr);
		cons_sdr.setBody(
				"{\n" +
						"super();" +
						"$0.positionDisplay = $1;" +
						"$0.dataQueue = $2;" +
						"$0.absoluteX = $3 ;" +
						"$0.absoluteY = $4 ;" +
						"$0.controlRadius = $5 ;" +			
				"}");
		sdr.addConstructor(cons_sdr);

		CtMethod cut = new CtMethod(CtClass.voidType,"cutoff", new CtClass[]{}, sdr);
		cut.setBody(
				"{" +
						"this.shouldContinue = false;" +
				"}");
		cut.setModifiers(Modifier.SYNCHRONIZED);
		sdr.addMethod(cut);

		CtMethod sb = new CtMethod(CtClass.voidType,"cutoff", new CtClass[]{pool.get("fr.upmc.dtgui.gui.RobotTeleoperationBoard")}, sdr);
		sb.setBody(
				"{" +
						"$0.tBoard = (" + board.getName() + ") $1 ;" +
				"}");
		sb.setModifiers(Modifier.SYNCHRONIZED);
		sdr.addMethod(sb);

		CtMethod start = new CtMethod(CtClass.voidType,"start", new CtClass[]{}, sdr);
		start.setBody(
				"{" +
						"this.shouldContinue = true ;" +
						"super.start();" +
				"}");
		start.setModifiers(Modifier.SYNCHRONIZED);
		sdr.addMethod(start);			

		//class myrunnable
		
		CtClass runnable = board.makeNestedClass("MyRunnable", true);
		runnable.addInterface(pool.get("java.lang.Runnable"));
		
		CtField posd_r = new CtField(pool.get("fr.upmc.dtgui.robot.PositioningData"),"pd", runnable);
		posd_r.setModifiers(Modifier.FINAL);
		runnable.addField(posd_r);
		
		CtField posdisp = new CtField(pool.get("fr.upmc.dtgui.gui.PositionDisplay"),"positionDisplay", runnable);
		posdisp.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		runnable.addField(posdisp);			
		
		CtConstructor cons_runnable = new CtConstructor(new CtClass[]{pool.get("fr.upmc.dtgui.robot.PositioningData"), pool.get("fr.upmc.dtgui.gui.PositionDisplay")},runnable);
		cons_runnable.setBody(
				"{" +
						"$0.pd = $1;" +
						"$0.positionDisplay = $2;" +
				"}");
		runnable.addConstructor(cons_runnable);
		
		
		CtMethod myrun = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, runnable);
		myrun.setBody(
				"{" +
						"$0.positionDisplay.draw(pd) ;" +
				"}");
		runnable.addMethod(myrun);
		
		//class myrunnable2
		
		CtClass runnable2 = board.makeNestedClass("MyRunnable2", true);
		runnable2.addInterface(pool.get("java.lang.Runnable"));	
		
		CtField tbo = new CtField(board,"tBoard", runnable2);
		tbo.setModifiers(Modifier.PROTECTED);
		runnable2.addField(tbo);
		
		CtField rsd1 = new CtField(pool.get("fr.upmc.dtgui.robot.RobotStateData"),"rsd1", runnable2);
		rsd1.setModifiers(Modifier.PROTECTED);
		runnable2.addField(rsd1);
		
		CtConstructor cons_runnable2 = new CtConstructor(new CtClass[]{pool.get("fr.upmc.dtgui.robot.RobotStateData"), board},runnable2);
		cons_runnable2.setBody(
				"{" +
						"$0.tBoard = $2;" +
						"$0.rsd1 = $1;" +
				"}");
		runnable2.addConstructor(cons_runnable2);
		
		CtMethod myrun2 = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, runnable2);
		myrun2.setBody(
				"{" +
						"if ($0.tBoard != null) {" +
							"$0.tBoard.processSensorData(rsd1) ;" +
						"}" +
				"}");
		runnable2.addMethod(myrun2);
		
		CtMethod run_sdr = new CtMethod(CtClass.voidType,"run", new CtClass[]{}, sdr);
		run_sdr.setBody(
				"{" +
						"fr.upmc.dtgui.robot.RobotStateData rsd = null ;" +
						"Vector current = new Vector(4) ;" +
						"while ($0.shouldContinue) {" +
							"try {" +
								"rsd = $0.dataQueue.take() ;" +
							"} catch (InterruptedException e) {" +
								"e.printStackTrace();" +
							"}" +
							"current.add(rsd) ;" +
							"int n = $0.dataQueue.drainTo(current) ;" +
							"for (int i = 0 ; i <= n ; i++) {" +
								"rsd = (fr.upmc.dtgui.robot.RobotStateData)current.elementAt(i) ;" +
								"try {" +
									"if (rsd instanceof fr.upmc.dtgui.robot.PositioningData) {" +
										"final fr.upmc.dtgui.robot.PositioningData pd = (fr.upmc.dtgui.robot.PositioningData) rsd ;" +
										"$0.MyRunnable runnable = new $0.MyRunnable(pd, positionDisplay);"+
										"SwingUtilities.invokeAndWait(runnable) ;" +
									"} " +
									"else {" +
										"if ($0.tBoard != null) {" +
											"final fr.upmc.dtgui.robot.RobotStateData rsd1 = rsd ;" +
											"$0.MyRunnable2 runnable2 = new $0.MyRunnable2(rsd1, tBoard);"+
											"SwingUtilities.invokeAndWait(runnable2 ) ;" +
										"}" +
									"}" +
								"} catch (InterruptedException e) {" +
									"e.printStackTrace();" +
								"} catch (InvocationTargetException e) {" +
									"e.printStackTrace();" +
								"}" +
							"}" +
							"current.clear() ;" +
						"}" +
				"}");
		sdr.addMethod(run_sdr);
	}
	
}
