package LogoPrinter;

import java.util.HashMap;
import java.util.Map;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class Printer {
	static final boolean UP = true;
	static final boolean DOWN = false;
	static final int DIR_LEFT = 1;
	static final int DIR_RIGHT = 2;
	static final double MAX_LEFT = 0.0;
	static final double MAX_RIGHT = 6.0;
	static final int MAX_ANGLE = 476;

	private double leftBorder;
	private double rightBorder;
	
	private boolean penStatus;
	private double penPosition;
	private int penAngle;
	private double[] positionTable = new double[MAX_ANGLE];
	
	static RegulatedMotor pen = new EV3MediumRegulatedMotor(MotorPort.A);
	static RegulatedMotor lifter = new EV3LargeRegulatedMotor(MotorPort.B);
	static RegulatedMotor rollers = new EV3LargeRegulatedMotor(MotorPort.C);
	
	private void BuildPenPositionLookupTable()
	{
		int smallAngle;
		double largeAngle;
		double r = 3.16;
		
		for (smallAngle=0; smallAngle < MAX_ANGLE; smallAngle++)
		{
			largeAngle = smallAngle/3.0;
			double temp1 = r * Math.cos(largeAngle);
			double temp2 = Math.pow(((r * Math.sin(largeAngle)) - 1),2);
			double temp3 = Math.sqrt(25-temp2);
			positionTable[smallAngle] = temp1 + temp3;
		}
	}
	
	private int FindAngleFromTable(double penPosition)
	{
		int loop;
		int returnVal = MAX_ANGLE + 1;
		
		if ((penPosition >= this.MAX_LEFT) && (penPosition <= this.MAX_RIGHT))
		{
			for (loop = 0; loop < MAX_ANGLE; loop++)
			{
				if (this.positionTable[loop] >= (this.positionTable[0] - penPosition))
				{
					returnVal = loop;
				}
			}
		}
		return returnVal;
	}
	
	public Printer(double leftBorder, double rightBorder, boolean penStatus, double penPosition)
	{
		if (leftBorder >= MAX_LEFT)
		{
			this.leftBorder = leftBorder;
		}
		else
		{
			this.leftBorder = MAX_LEFT;
		}
		
		if (rightBorder <= MAX_RIGHT)
		{
			this.rightBorder = rightBorder;
		}
		else
		{
			this.rightBorder = MAX_RIGHT;
		}

		this.penStatus = penStatus;
		this.penPosition = penPosition;
		this.BuildPenPositionLookupTable();
		this.penAngle = this.FindAngleFromTable(penPosition);
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
		
		if (direction == DIR_LEFT)
		{
			if ((penPosition - distance) >= leftBorder)
			{
				// Move pen left
				int targetAngle = this.FindAngleFromTable(penPosition - distance);
				pen.rotateTo(penAngle - targetAngle);
				returnVal = 0;
			}
		}
		else if (direction == DIR_RIGHT)
		{
			if ((penPosition + distance) <= rightBorder)
			{
				// Move pen right
				int targetAngle = this.FindAngleFromTable(penPosition + distance);
				pen.rotateTo(targetAngle - penAngle);
				returnVal = 0;
			}
		}

		return returnVal;
	}
	
}
