package LogoPrinter;

import LogoPrinter.Printer;

public class Alphabet {
	static Printer p = new Printer(0.0, 6.0, Printer.UP, 0.0);
	
	public static void main(String [ ] args)
	{
//		if (p.LiftPen(Printer.UP) == 0) System.out.println("Pen moved UP");
		if (p.LiftPen(Printer.DOWN) == 0) System.out.println("Pen moved DOWN");
//		if (p.LiftPen(Printer.UP) == 0) System.out.println("Pen moved UP");
		
		if (p.MovePen(1, Printer.DIR_LEFT) == 0) System.out.println("Pen moved 1 left");
		if (p.MovePen(2, Printer.DIR_RIGHT) == 0) System.out.println("Pen moved 2 right");
		if (p.MovePen(2, Printer.DIR_LEFT) == 0) System.out.println("Pen moved 2 left");
	}
}
