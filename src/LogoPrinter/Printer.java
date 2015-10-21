package LogoPrinter;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class Printer {
	// Direction and position constants
	static final boolean PEN_UP = true;
	static final boolean PEN_DOWN = false;
	static final int DIR_LEFT = 1;
	static final int DIR_RIGHT = 2;
	static final int DIR_FORWARD = 3;
	static final int DIR_REVERSE = 4;
	
	// Maximum movement constants
	static final double MAX_LEFT = 0.0;
	static final double MAX_RIGHT = 5.0;
	static final int MAX_ANGLE = 477;

	// Instance position variables
	private double leftBorder;
	private double rightBorder;
	
	private boolean penStatus;
	private double penPosition;
	private int penAngle;
	
	// Lookup table for pen position
	private double[] positionTable = new double[MAX_ANGLE];
	
	// EV3 motor instances
	static RegulatedMotor pen = new EV3MediumRegulatedMotor(MotorPort.A);
	static RegulatedMotor lifter = new EV3LargeRegulatedMotor(MotorPort.B);
	static RegulatedMotor rollers = new EV3LargeRegulatedMotor(MotorPort.C);
	
	/**
	 * Build a lookup table for pen position indexed to motor angle
	 * @param None
	 * @return None
	 */
	private void BuildPenPositionLookupTable()
	{
		int smallAngle;
		double largeAngle;
		double r = 3.16;
		
		for (smallAngle=0; smallAngle < MAX_ANGLE; smallAngle++)
		{
			largeAngle = (smallAngle/3.0) * (Math.PI / 180);
			double temp1 = r * Math.cos(largeAngle);
			double temp2 = Math.pow(((r * Math.sin(largeAngle)) - 1),2);
			double temp3 = Math.sqrt(25-temp2);
			positionTable[smallAngle] = temp1 + temp3;
//			System.out.println(Integer.toString(smallAngle) + " : " + Double.toString(positionTable[smallAngle]));
		}
	}
	
	/**
	 * Search for target angle associated with an absolute pen position
	 * @param penPosition Desired absolute pen position
	 * @return MAX_ANGLE+1 for an error, else lookup table index (angle)
	 */
	private int FindAngleFromTable(double penPosition)
	{
		int loop;
		int returnVal = MAX_ANGLE + 1;
		
		if ((penPosition >= MAX_LEFT) && (penPosition <= MAX_RIGHT))
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
	
	/**
	 * Printer instance constructor
	 * @param leftBorder 
	 * @param rightBorder
	 * @param penStatus
	 * @param penPosition
	 */
	public Printer(double leftBorder, double rightBorder, boolean penStatus, double penPosition)
	{
		System.out.println("Initializing printer");
		System.out.println("--------------------");
		if (leftBorder >= MAX_LEFT)
		{
			this.leftBorder = leftBorder;
		}
		else
		{
			this.leftBorder = MAX_LEFT;
		}
		System.out.println("Left Border set to "+Double.toString(this.leftBorder));
		
		if (rightBorder <= MAX_RIGHT)
		{
			this.rightBorder = rightBorder;
		}
		else
		{
			this.rightBorder = MAX_RIGHT;
		}
		System.out.println("Right Border set to " + Double.toString(this.rightBorder));

		this.penStatus = penStatus;
		this.penPosition = penPosition;
		System.out.println("Pen Position set to " + Double.toString(this.penPosition));
		this.BuildPenPositionLookupTable();
		this.penAngle = this.FindAngleFromTable(penPosition);
		System.out.println("Pen Angle set to " + Integer.toString(this.penAngle));
		System.out.println("--------------------");
	}

	public int LiftPen(boolean UPDOWN)
	{
		int returnVal = 1;
		
		if (UPDOWN == PEN_UP)
		{
			if (penStatus == PEN_DOWN)
			{
				// Move pen up
				lifter.rotate(180);
				penStatus = PEN_UP;
			}
			returnVal = 0;
		}
		else if (UPDOWN == PEN_DOWN)
		{
			if (penStatus == PEN_UP)
			{
				// Move pen down
				lifter.rotate(180);
				penStatus = PEN_DOWN;
			}
			returnVal = 0;
		}
		return returnVal;
	}
	
	public int MovePen(double distance, int direction)
	{
		int returnVal = 1;
		
		if (direction == DIR_LEFT)
		{
			if ((penPosition - distance) >= leftBorder)
			{
				// Move pen left
				int targetAngle = this.FindAngleFromTable(penPosition - distance);
				if (targetAngle != (MAX_ANGLE + 1))
				{
					pen.rotateTo(-1 * (penAngle - targetAngle));
					returnVal = 0;
				}
			}
		}
		else if (direction == DIR_RIGHT)
		{
			if ((penPosition + distance) <= rightBorder)
			{
				// Move pen right
				int targetAngle = this.FindAngleFromTable(penPosition + distance);
				if (targetAngle != (MAX_ANGLE + 1))
				{
					pen.rotateTo(targetAngle - penAngle);
					returnVal = 0;
				}
			}
		}

		return returnVal;
	}
	
	public int MovePen(int direction)
	{
		int returnVal = 1;
		
		System.out.println("Moving pen");
		System.out.println("--------------------");
		if (direction == DIR_LEFT)
		{
			if (penPosition >= leftBorder)
			{
				// Move pen left
				int targetAngle = this.FindAngleFromTable(penPosition - MAX_LEFT);
				if (targetAngle != (MAX_ANGLE+1))
				{
					System.out.println("Moving left");
					System.out.println("Target Angle " + Integer.toString(targetAngle));
					System.out.println("Rotation Angle " + Integer.toString(-(penAngle - targetAngle)));
					pen.rotateTo(-1 * (penAngle - targetAngle));
					penPosition = MAX_LEFT;
					penAngle = targetAngle;
					returnVal = 0;
				}
			}
		}
		else if (direction == DIR_RIGHT)
		{
			if (penPosition <= rightBorder)
			{
				// Move pen right
				int targetAngle = this.FindAngleFromTable(MAX_RIGHT - penPosition);
				if (targetAngle != (MAX_ANGLE + 1))
				{
					System.out.println("Moving right");
					System.out.println("Target Angle " + Integer.toString(targetAngle));
					System.out.println("Rotation Angle " + Integer.toString(targetAngle - penAngle));
					pen.rotateTo(targetAngle - penAngle);
					penPosition = MAX_RIGHT;
					penAngle = targetAngle;
					returnVal = 0;
				}
			}
		}
		System.out.println("--------------------");

		return returnVal;
	}
	
	public int MoveRollers(double distance, int direction)
	{
		int returnVal = 1;
		
		System.out.println("Moving rollers");
		System.out.println("--------------------");
		if (direction == DIR_FORWARD)
		{
			// Move paper forward
			int targetAngle = this.FindAngleFromTable(distance);
			if (targetAngle != (MAX_ANGLE + 1))
			{
				System.out.println("Moving forward");
				System.out.println("Target/Rotation Angle " + Integer.toString(targetAngle));
				rollers.rotate(-targetAngle);
				returnVal = 0;
			}
		}
		else if (direction == DIR_REVERSE)
		{
			// Move paper backward
			int targetAngle = this.FindAngleFromTable(distance);
			if (targetAngle != (MAX_ANGLE + 1))
			{
				System.out.println("Moving right");
				System.out.println("Target/Rotation Angle " + Integer.toString(targetAngle));
				rollers.rotate(targetAngle);
				returnVal = 0;
			}
		}

		System.out.println("--------------------");
		return returnVal;
	}
	
}
