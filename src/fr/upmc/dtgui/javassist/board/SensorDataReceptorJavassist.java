package fr.upmc.dtgui.javassist.board;

import java.lang.reflect.Modifier;

import com.sun.org.apache.xpath.internal.operations.Mod;

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
public class SensorDataReceptorJavassist {

	/**
	 * the default constructor
	 */
	public SensorDataReceptorJavassist(){
		
	}
	
	/**
	 * create the code of the nested class SensorDataSender excepted the method run
	 * @param pool the classpool that contains all classes at the loading in Javassist
	 * @param board the TeleoperationBoard associated to the current robot
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void create(ClassPool pool, CtClass board) throws CannotCompileException, NotFoundException{
		
		/* class SensorDataReceptor */
		CtClass sensorDataReceptor = board.makeNestedClass("SensorDataReceptor", true);
		sensorDataReceptor.setSuperclass(pool.get("java.lang.Thread"));
		sensorDataReceptor.addInterface(pool.get("fr.upmc.dtgui.gui.SensorDataReceptorInterface"));

		/* add field positionDisplay*/
		CtField positionDisplay = new CtField(pool.get("fr.upmc.dtgui.gui.PositionDisplay"),"positionDisplay", sensorDataReceptor);
		positionDisplay.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		sensorDataReceptor.addField(positionDisplay);

		/* add field tBoard */
		CtField tBoard = new CtField(board,"tBoard", sensorDataReceptor);
		tBoard.setModifiers(Modifier.PROTECTED);
		sensorDataReceptor.addField(tBoard);

		/* add field dataQueue */
		CtField dataQueue = new CtField(pool.get("java.util.concurrent.BlockingQueue"),"dataQueue", sensorDataReceptor);
		dataQueue.setModifiers(Modifier.FINAL | Modifier.PROTECTED);
		sensorDataReceptor.addField(dataQueue);

		/* add field absoluteX */
		CtField absoluteX = new CtField(CtClass.intType,"absoluteX", sensorDataReceptor);
		absoluteX.setModifiers(Modifier.PROTECTED);
		sensorDataReceptor.addField(absoluteX);

		/* add field absoluteY */
		CtField absoluteY = new CtField(CtClass.intType,"absoluteY", sensorDataReceptor);
		absoluteY.setModifiers(Modifier.PROTECTED);
		sensorDataReceptor.addField(absoluteY);

		/* add field controlRadius */
		CtField controlRadius = new CtField(CtClass.intType,"controlRadius", sensorDataReceptor);
		controlRadius.setModifiers(Modifier.PROTECTED);
		sensorDataReceptor.addField(controlRadius);

		/* add field shouldContinue*/
		CtField shouldContinue = new CtField(CtClass.booleanType,"shouldContinue", sensorDataReceptor);
		shouldContinue.setModifiers(Modifier.PROTECTED);
		sensorDataReceptor.addField(shouldContinue);

		/* add constructor */
		CtClass[] argsConstructorSensorDataReceptor = new CtClass[]{
				pool.get("fr.upmc.dtgui.gui.PositionDisplay"),
				pool.get("java.util.concurrent.BlockingQueue"),
				CtClass.intType,
				CtClass.intType,
				CtClass.intType
		};
		CtConstructor constructorSensorDataReceptor = new CtConstructor(argsConstructorSensorDataReceptor, sensorDataReceptor);
		constructorSensorDataReceptor.setBody(
				"{\n" +
						"super();" +
						"$0.positionDisplay = $1;" +
						"$0.dataQueue = $2;" +
						"$0.absoluteX = $3 ;" +
						"$0.absoluteY = $4 ;" +
						"$0.controlRadius = $5 ;" +			
				"}");
		sensorDataReceptor.addConstructor(constructorSensorDataReceptor);

		/* add method cutOff */
		CtMethod cutoff = new CtMethod(CtClass.voidType,"cutoff", new CtClass[]{}, sensorDataReceptor);
		cutoff.setModifiers(Modifier.SYNCHRONIZED | Modifier.PUBLIC);
		cutoff.setBody(
				"{" +
						"this.shouldContinue = false;" +
				"}");
		sensorDataReceptor.addMethod(cutoff);

		/* add method setTBoard */
		CtMethod setTBoard = new CtMethod(CtClass.voidType,"setTBoard", new CtClass[]{pool.get("fr.upmc.dtgui.gui.RobotTeleoperationBoard")}, sensorDataReceptor);
		setTBoard.setModifiers(Modifier.SYNCHRONIZED | Modifier.PUBLIC);
		setTBoard.setBody(
				"{" +
						"$0.tBoard = (" + board.getName() + ") $1 ;" +
				"}");
		sensorDataReceptor.addMethod(setTBoard);

		/* add method start */
		CtMethod start = new CtMethod(CtClass.voidType,"start", new CtClass[]{}, sensorDataReceptor);
		start.setModifiers(Modifier.SYNCHRONIZED | Modifier.PUBLIC);
		start.setBody(
				"{" +
						"this.shouldContinue = true ;" +
						"super.start();" +
				"}");
		sensorDataReceptor.addMethod(start);			

	}
	
	/**
	 * add the method run in the class SensorDataSender
	 * @param pool the classpool that contains all classes at the loading in Javassist
	 * @param board the TeleoperationBoard associated to the current robot
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static void update(ClassPool pool, CtClass board) throws CannotCompileException, NotFoundException{	
	
		CtClass sensorDataReceptor = pool.get(board.getName() + "$SensorDataReceptor");
		
		/*add nested class MyRunnable1 */		
		CtClass runnable1 = board.makeNestedClass("MyRunnable1", true);
		runnable1.addInterface(pool.get("java.lang.Runnable"));
		
		/* add field pd*/
		CtField pd = new CtField(pool.get("fr.upmc.dtgui.robot.PositioningData"),"pd", runnable1);
		pd.setModifiers(Modifier.FINAL);
		runnable1.addField(pd);		
		
		/* add constructor */
		CtConstructor constructorRunnable1 = new CtConstructor(new CtClass[]{pool.get("fr.upmc.dtgui.robot.PositioningData")},runnable1);
		constructorRunnable1.setBody(
				"{" +
						"$0.pd = $1;" +
				"}");
		runnable1.addConstructor(constructorRunnable1);
		
		/* add method run */
		CtMethod runMyRunnable1 = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, runnable1);
		runMyRunnable1.setBody(
				"{" +
						sensorDataReceptor.getName()+".$0.positionDisplay.draw(pd) ;" +
				"}");
		runnable1.addMethod(runMyRunnable1);
		
		/*create nested class myrunnable2*/	
		CtClass runnable2 = sensorDataReceptor.makeNestedClass("MyRunnable2", true);
		runnable2.addInterface(pool.get("java.lang.Runnable"));	
		
		/* add field rsd1 */
		CtField rsd1 = new CtField(pool.get("fr.upmc.dtgui.robot.RobotStateData"),"rsd1", runnable2);
		rsd1.setModifiers(Modifier.PROTECTED);
		runnable2.addField(rsd1);
		
		/* add constructor */
		CtConstructor constructorRunnable2 = new CtConstructor(new CtClass[]{pool.get("fr.upmc.dtgui.robot.RobotStateData"), board},runnable2);
		constructorRunnable2.setBody(
				"{" +
						"$0.rsd1 = $1;" +		
				"}");
		runnable2.addConstructor(constructorRunnable2);
		
		/* create method run */
		CtMethod runMyRunnable2 = new CtMethod(CtClass.voidType, "run", new CtClass[]{}, runnable2);
		runMyRunnable2.setBody(
				"{" +
						"if ($0.tBoard != null) {" +
							sensorDataReceptor.getName()+".$0.tBoard.processSensorData(rsd1) ;" +
						"}" +
				"}");
		runnable2.addMethod(runMyRunnable2);
		
		/* add the method run*/
		CtMethod runSensorDataReceptor = new CtMethod(CtClass.voidType,"run", new CtClass[]{}, sensorDataReceptor);
		runSensorDataReceptor.setBody(
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
										"$0.MyRunnable1 runnable1 = new $0.MyRunnable1(pd);"+
										"SwingUtilities.invokeAndWait(runnable1) ;" +
									"} " +
									"else {" +
										"if ($0.tBoard != null) {" +
											"final fr.upmc.dtgui.robot.RobotStateData rsd1 = rsd ;" +
											"$0.MyRunnable2 runnable2 = new $0.MyRunnable2(rsd1);"+
											"SwingUtilities.invokeAndWait(runnable2) ;" +
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
		sensorDataReceptor.addMethod(runSensorDataReceptor);
	}
	
}
