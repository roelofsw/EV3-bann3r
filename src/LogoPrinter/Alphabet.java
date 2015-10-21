package LogoPrinter;

import LogoPrinter.Printer;

public class Alphabet {
	static Printer p = new Printer(0.0, 5.5, Printer.PEN_UP, 0.0);
	
	public static int drawLine(double startX, double startY, double endX, double endY)
	{
		int returnVal = 0;
		
		p.LiftPen(Printer.PEN_UP);
		p.PlacePenAbs(startY, startX);
		p.LiftPen(Printer.PEN_DOWN);
		p.PlacePenAbs(endY, endX);
		
		return returnVal;
	}
	
	public static void drawRect()
	{
		p.LiftPen(Printer.PEN_DOWN);
		p.MovePen(Printer.DIR_UP);
		p.MoveRollers(5.0, Printer.DIR_RIGHT);
		p.MovePen(Printer.DIR_DOWN);
		p.MoveRollers(5.0, Printer.DIR_LEFT);
		p.LiftPen(Printer.PEN_UP);
	}
	
	public static void main(String [ ] args)
	{
		drawLine(0.0, 2.0, 5.0, 5.0);
//		drawRect();
		p.LiftPen(Printer.PEN_UP);
		p.MovePen(Printer.DIR_DOWN);
	}
}
