package LogoPrinter;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class Printer {
	static final boolean UP = true;
	static final boolean DOWN = false;
	static final int LEFT = 1;
	static final int RIGHT = 2;

	static int leftBorder = 0;
	static int rightBorder = 50;
	
	static boolean penStatus = UP;
	static int penPosition = 0;
	
	static RegulatedMotor pen = new EV3MediumRegulatedMotor(MotorPort.A);
	static RegulatedMotor lifter = new EV3LargeRegulatedMotor(MotorPort.B);
	static RegulatedMotor rollers = new EV3LargeRegulatedMotor(MotorPort.C);
	
	private static int LiftPen(boolean UPDOWN)
	{
		int returnVal = 1;
		
		if (UPDOWN == UP)
		{
			if (penStatus == DOWN)
			{
				// Move pen up
				lifter.rotate(180);
				penStatus = UP;
			}
			returnVal = 0;
		}
		else if (UPDOWN == DOWN)
		{
			if (penStatus == UP)
			{
				// Move pen down
				lifter.rotate(180);
				penStatus = DOWN;
			}
			returnVal = 0;
		}
		return returnVal;
	}
	
	private static int MovePen(int distance, int direction)
	{
		int returnVal = 1;
		
		if (direction == LEFT)
		{
			if ((penPosition - distance) >= leftBorder)
			{
				// Move pen
			}
		}
		else if (direction == RIGHT)
		{
			if ((penPosition + distance) <= rightBorder)
			{
				// Move pen
			}
		}

		return returnVal;
	}
		
	public static void main(String [ ] args)
	{
		if (LiftPen(UP) == 0) System.out.println("Pen moved UP");
		if (LiftPen(DOWN) == 0) System.out.println("Pen moved DOWN");
		if (LiftPen(UP) == 0) System.out.println("Pen moved UP");
		
		if (MovePen(20, LEFT) == 0) System.out.println("Pen moved 20 left");
	}
	
}
