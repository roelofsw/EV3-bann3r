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

	int leftBorder;
	int rightBorder;
	
	boolean penStatus;
	int penPosition;
	
	static RegulatedMotor pen = new EV3MediumRegulatedMotor(MotorPort.A);
	static RegulatedMotor lifter = new EV3LargeRegulatedMotor(MotorPort.B);
	static RegulatedMotor rollers = new EV3LargeRegulatedMotor(MotorPort.C);
	
	public Printer(int leftBorder, int rightBorder, boolean penStatus, int penPosition)
	{
		this.leftBorder = leftBorder;
		this.rightBorder = rightBorder;
		this.penStatus = penStatus;
		this.penPosition = penPosition;
	}

	public int LiftPen(boolean UPDOWN)
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
	
	public int MovePen(int distance, int direction)
	{
		int returnVal = 1;
		
		if (direction == LEFT)
		{
			if ((penPosition - distance) >= leftBorder)
			{
				// Move pen left
			}
		}
		else if (direction == RIGHT)
		{
			if ((penPosition + distance) <= rightBorder)
			{
				// Move pen right
			}
		}

		return returnVal;
	}
	
}
