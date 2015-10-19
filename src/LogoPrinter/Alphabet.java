package LogoPrinter;

import LogoPrinter.Printer;

public class Alphabet {
	static Printer p = new Printer(0, 50, Printer.UP, 0);
	
	public static void main(String [ ] args)
	{
		if (p.LiftPen(Printer.UP) == 0) System.out.println("Pen moved UP");
		if (p.LiftPen(Printer.DOWN) == 0) System.out.println("Pen moved DOWN");
		if (p.LiftPen(Printer.UP) == 0) System.out.println("Pen moved UP");
		
		if (p.MovePen(20, Printer.LEFT) == 0) System.out.println("Pen moved 20 left");
	}
}
