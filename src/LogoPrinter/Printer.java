package LogoPrinter;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class Printer {
	// Direction and position constants
	static final boolean PEN_UP = true;
	static final boolean PEN_DOWN = false;
	static final int DIR_UP = 1;
	static final int DIR_DOWN = 2;
	static final int DIR_LEFT = 3;
	static final int DIR_RIGHT = 4;
	
	// Maximum movement constants
	static final double MAX_BOTTOM = 0.0;
	static final double MAX_TOP = 5.5;
	static final int MAX_ANGLE = 477;

	// Instance position variables
	private double bottomBorder;
	private double topBorder;
	
	private boolean penStatus;
	private double penPosition;
	private int penAngle;
	private double rollerPosition;
	
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
		
		if ((penPosition >= MAX_BOTTOM) && (penPosition <= MAX_TOP))
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
	 * @param bottomBorder 
	 * @param topBorder
	 * @param penStatus
	 * @param penPosition
	 */
	public Printer(double bottomBorder, double topBorder, boolean penStatus, double penPosition)
	{
		//TODO Add speed setting for motors
		if (bottomBorder >= MAX_BOTTOM)
		{
			this.bottomBorder = bottomBorder;
		}
		else
		{
			this.bottomBorder = MAX_BOTTOM;
		}
		
		if (topBorder <= MAX_TOP)
		{
			this.topBorder = topBorder;
		}
		else
		{
			this.topBorder = MAX_TOP;
		}

		this.penStatus = penStatus;
		this.penPosition = penPosition;
		this.BuildPenPositionLookupTable();
		this.penAngle = this.FindAngleFromTable(penPosition);
		this.rollerPosition = 0.0;
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
		
		if (direction == DIR_DOWN)
		{
			if ((penPosition - distance) >= bottomBorder)
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
		else if (direction == DIR_UP)
		{
			if ((penPosition + distance) <= topBorder)
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
		
		if (direction == DIR_DOWN)
		{
			if (penPosition >= bottomBorder)
			{
				// Move pen down
				int targetAngle = this.FindAngleFromTable(penPosition - MAX_BOTTOM);
				if (targetAngle != (MAX_ANGLE+1))
				{
					pen.rotateTo(-1 * (penAngle - targetAngle));
					penPosition = MAX_BOTTOM;
					penAngle = targetAngle;
					returnVal = 0;
				}
			}
		}
		else if (direction == DIR_UP)
		{
			if (penPosition <= topBorder)
			{
				// Move pen up
				int targetAngle = this.FindAngleFromTable(MAX_TOP - penPosition);
				if (targetAngle != (MAX_ANGLE + 1))
				{
					pen.rotateTo(targetAngle - penAngle);
					penPosition = MAX_TOP;
					penAngle = targetAngle;
					returnVal = 0;
				}
			}
		}

		return returnVal;
	}
	
	public int MoveRollers(double distance, int direction)
	{
		//TODO Add different relative positions for rollers (current cell, start of line, etc)
		//TODO Adjust movement for diameter of rollers, not just gears
		//TODO Add ability to move in multiples of current lookup table (ie, move more than 6.0 left or right)
		int returnVal = 1;
		
		if (direction == DIR_LEFT)
		{
			// Move paper forward
			int targetAngle = this.FindAngleFromTable(distance);
			if (targetAngle != (MAX_ANGLE + 1))
			{
				rollers.rotate(-targetAngle);
				rollerPosition -= distance;
				returnVal = 0;
			}
		}
		else if (direction == DIR_RIGHT)
		{
			// Move paper backward
			int targetAngle = this.FindAngleFromTable(distance);
			if (targetAngle != (MAX_ANGLE + 1))
			{
				rollers.rotate(targetAngle);
				rollerPosition += distance;
				returnVal = 0;
			}
		}

		return returnVal;
	}
	
	public int PlacePen(double posPen, double posRoller, boolean relPen, boolean relRoller)
	{
		int returnVal = 1;
		double targetPen;
		double targetRollers;
		int dirPen;
		int dirRollers;
		
		if (relPen || (posPen < 0.0))
		{
			if (posPen < 0.0)
				dirPen = DIR_DOWN;
			else
				dirPen = DIR_UP;
			targetPen = Math.abs(posPen);
		}
		else
		{
			if ((posPen - penPosition) > 0.0)
			{
				dirPen = DIR_UP;
				targetPen = (posPen - penPosition);
			}
			else
			{
				dirPen = DIR_DOWN;
				targetPen = (penPosition - posPen);
			}
		}
		
		//TODO Determine best relative position for rollers to work from (see MoveRollers)
		if (relRoller || (posRoller < 0.0))
		{
			if (posRoller < 0.0)
				dirRollers = DIR_LEFT;
			else
				dirRollers = DIR_RIGHT;
			targetRollers = Math.abs(posRoller);
		}
		else
		{
			if ((posRoller - rollerPosition) > 0.0)
			{
				dirRollers = DIR_RIGHT;
				targetRollers = (posRoller - rollerPosition);
			}
			else
			{
				dirRollers = DIR_LEFT;
				targetRollers = (rollerPosition - posRoller);
			}
		}
		
		//TODO Enable synchronised movement of pen and rollers
		returnVal = this.MoveRollers(targetRollers, dirRollers);
		if (returnVal == 0)
			returnVal = this.MovePen(targetPen, dirPen);
	
		return returnVal;
	}
	
	//TODO Find most intuitive way to instruct pen movement (relative pen, absolute roller?)
	
	public int PlacePenRel(double penPos, double rollerPos)
	{
		return (PlacePen(penPos, rollerPos, true, true));
	}
	
	public int PlacePenAbs(double penPos, double rollerPos)
	{
		return (PlacePen(penPos, rollerPos, false, false));
	}
	
}
