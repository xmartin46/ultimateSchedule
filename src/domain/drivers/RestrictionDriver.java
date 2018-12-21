package domain.drivers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.parser.ParseException;

import com.sun.xml.internal.ws.util.xml.CDATA;

import domain.classes.Group;
import domain.classes.Lecture;
import domain.classes.Restriction;
import domain.classes.Subject;
import domain.classes.restrictions.CorequisitRestriction;
import domain.classes.restrictions.DayPeriodRestriction;
import domain.classes.restrictions.LectureFromSameGroupOverlapRestriction;
import domain.classes.restrictions.OccupiedRoomRestriction;
import domain.classes.restrictions.ParentGroupOverlapRestriction;
import domain.classes.restrictions.SpecificDayOrHourRestriction;
import domain.classes.restrictions.SubjectLevelRestriction;
import domain.classes.restrictions.UnaryRestriction;
import domain.controllers.CtrlDomain;
import domain.classes.restrictions.NaryRestriction;

/**
 * @author Xavier Lacasa Curto
*/

public class RestrictionDriver {
	private static Restriction r;
	private static Scanner sc;
	private static boolean silent = false;
	
	//TODO: Fer que el joc de proves estigui be. Crec que lo de fer-ho automatic ja va
	
	private static void menu() {
		System.out.print(
	            "Room Driver\n"
	            + "---------------------\n"
	            + "Opciones\n"
	            + " 1| new Restriction\n"
	            + " 2| disable\n"
	            + " 3| enable\n"
	            + " 4| isEnabled\n"
	            + " 5| isNegotiable\n"
	            + " 6| validate\n"
	            + " 0| Salir\n"
	            + "---------------------\n"
	            );
	                    
    }
	
	private static void printMain() {
		System.out.print(
	            "Lecture Driver\n"
	            + "---------------------\n"
	    	    + "Opciones\n"
	    	    + " 1| Test Automatico\n"
	    	    + " 2| Probar Manualmente\n"
	    	    + " 0| Salir\n"
	    	    + "---------------------\n"
	    	    );                     
    }
	
	public static void main (String [] args) throws Exception {
		sc = new Scanner(System.in);
		int n;
	    if (!silent) printMain();
	    n = sc.nextInt();
	    switch (n) {
	    	case 1:
	    		automaticTestMenu();
	            break;
	        case 2:
	        	loadTestMenu();
	            break;
	    }
	}
	
	private static void printLoadFileMenu(String title, List<String> filenames) {
		System.out.println(title);
		System.out.print("--------------------------\n");
		for (int i = 0; i < filenames.size(); i++) {
			System.out.println(i + "| "+ filenames.get(i));
		}
		
		System.out.print("--------------------------\n");
    }
	
	
	public static void automaticTestMenu(){
		String path = "data/driverTests/restriction/";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		List<String> filenames = new ArrayList<String>();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	filenames.add(file.getName());
		    }
		}
		printLoadFileMenu("Cargar Test", filenames);
	    int n = sc.nextInt();
	    try {
	    	String filename = filenames.get(n);
	    	sc = new Scanner(new FileReader(new File(path+filename)));
	
	    	silent = true;
	    	loadTestMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void loadTestMenu() throws Exception {
		int n;
	    menu();
	    n = sc.nextInt();
	    while (n != 0) {
	        switch (n) {
	            case 1:
	            	testConstructor(sc);
	                break;
	            case 2:
	            	testDisable(sc);
	                break;
	            case 3:
	            	testEnable(sc);
	                break;
	            case 4:
	            	testIsEnabled(sc);
	                break;
	            case 5:
	            	testIsNegotiable(sc);
	                break;
	            case 6:
	            	testValidate(sc);
	            	break;
	        }
	        if (!silent) menu();
	        if (sc.hasNext()) n = sc.nextInt();
	        else return;
	    }
	}

	
	public static void testValidate(Scanner sc) throws ParseException, IOException {
		if (r instanceof UnaryRestriction) {
			if (!silent) System.out.println("Introduzca dia y/o hora en este orden.\n");
        	Integer day, hour;
        	day = sc.nextInt();
        	hour = sc.nextInt();
        	if (!silent) System.out.println("Introduzca duracion de la Lecture:\n");
        	Integer duration = sc.nextInt();
			boolean x = ((UnaryRestriction) r).validate(day, hour, duration);
			if (x) System.out.println("Tu combinacion de dia-hora-clase es valida segun la ultima restriccion creada");
			else System.out.println("Tu combinacion de dia-hora-clase NO es valida segun la ultima restriccion creada");
		}
		else {
			CtrlDomain cdo = CtrlDomain.getInstance();
			String filename = sc.next();
			cdo.importEnvironment(filename, false);
			String room = sc.next();
			Integer day = sc.nextInt();
			Integer hour = sc.nextInt();
			String lecture = sc.next();
			String ro = sc.next();
			Integer d = sc.nextInt();
			Integer h = sc.nextInt();
			String l = sc.next();
			
			boolean x = ((NaryRestriction) r).validate(room, day, hour, lecture, d, h, ro, l);
			if (x) System.out.println("La combinacion es valida segun la restriccion " + r.getClass().getSimpleName());
		}
	}
	
		public static void listRestrictions() {
			System.out.println(
					"---------------------\n"
		            + "Opciones\n"
		            + " 1| CorequisitRestriction\n"
		            + " 2| DayPeriodRestriction\n"
		            + " 3| LectureFromSameGroupOverlap\n"
		            + " 4| OccupiedRoomRestriction\n"
		            + " 5| ParentGroupOverlapRestriction\n"
		            + " 6| SpecificDayOrHourRestriction\n"
		            + " 7| SubjectLevelRestriction\n"
		            + " 0| Salir\n"
		            + "---------------------\n"
		            );
		}
	
		public static void testConstructor(Scanner sc){
		    try {
		    	System.out.println("Escoger restriccion a crear. Solo se guarda la ultima restriccion creada.\n");
		    	if (!silent)listRestrictions();
		    	int n;
			    n = sc.nextInt();
			    while (n != 0) {
			        switch (n) {
			            case 1:
			            	r = new CorequisitRestriction();
					        System.out.println("Nueva Restriccion ( " + r.toString() + " ) definida\n");
			                break;
			            case 2:
			            	if (!silent) System.out.println("Introduzca la hora que quiere usar para el mediodia:\n");
			            	Integer midDay = sc.nextInt();
			            	Group.DayPeriod dp = null;
			            	if (!silent) System.out.println("Escoja el periodo de clases del grupo:\n"
			            			+"---------------------\n"
			    		            + "Opciones\n"
			    		            + " 1| MORNING\n"
			    		            + " 2| AFTERNOON\n"
			    		            + " 3| INDIFERENT\n"
			    		            + "---------------------\n");
			            	int o;
			            	o = sc.nextInt();
			            	switch (o) {
			            		case 1:
			            			dp = Group.DayPeriod.MORNING;
			            			break;
			            		case 2:
			            			dp = Group.DayPeriod.AFTERNOON;
			            			break;
			            		case 3:
			            			dp = Group.DayPeriod.INDIFERENT;
			            			break;
			            	}
			            	r = new DayPeriodRestriction(midDay, dp);
			            	System.out.println("Nueva Restriccion ( " + r.toString() + " ) definida\n");
			                break;
			            case 3:
			            	r = new LectureFromSameGroupOverlapRestriction();
			            	System.out.println("Nueva Restriccion ( " + r.toString() + " ) definida\n");
			                break;
			            case 4:
			            	r = new OccupiedRoomRestriction();
			            	System.out.println("Nueva Restriccion ( " + r.toString() + " ) definida\n");
			                break;
			            case 5:
			            	r = new ParentGroupOverlapRestriction();
			            	System.out.println("Nueva Restriccion ( " + r.toString() + " ) definida\n");
			                break;
			            case 6:
			            	if (!silent) System.out.println("Introduzca dia y/o hora en este orden.\n"
            						+"en caso de solo dia o hora, introduzca -1 en el campo que no requiera.\n");
			            	Integer day, hour;
			            	day = sc.nextInt();
			            	if (day.equals(-1)) day = null;
			            	hour = sc.nextInt();
			            	if (hour.equals(-1)) hour = null;
			            	r = new SpecificDayOrHourRestriction(day, hour);
					        System.out.println("Nueva Restriccion ( " + r.toString() + " ) definida\n");	
					        break;
			            case 7:
			            	r = new SubjectLevelRestriction();
			            	System.out.println("Nueva Restriccion ( " + r.toString() + " ) definida\n");
			            	break;
			        }
			        if (!silent) listRestrictions();
			        n = sc.nextInt();
			    }
		    } catch (Exception e) {
		        System.out.println(e);
		    }
		}
		
		public static void testDisable(Scanner sc){
			try {
	            if (r.disable()) System.out.println("La restriccion ( " + r.toString() + " ) ha sido desactivada.\n");
	            else System.out.println("La restriccion ( " + r.toString() + " ) no se puede desactivar porque no es negociable.\n");
	        } catch (Exception e) {
	            System.out.println(e);
	        }
		}
		
		public static void testEnable(Scanner sc){
			try {
	            if (r.enable()) System.out.println("La restriccion ( " + r.toString() + " ) ha sido activada.\n");
	            else System.out.println("La restriccion ( " + r.toString() + " ) esta siempre activada porque no es negociable.\n");
	        } catch (Exception e) {
	            System.out.println(e);
	        }
		}
		
		
		public static void testIsEnabled(Scanner sc){
			try {
	            Boolean x = r.isEnabled();
	            if (x) System.out.println("La restriccion ( " + r.toString() + " ) esta ACTIVADA.\n");
	            else System.out.println("La restriccion ( " + r.toString() + " ) esta DESACTIVADA.\n");
	        } catch (Exception e) {
	            System.out.println(e);
	        }
		}
		
		public static void testIsNegotiable(Scanner sc){
			try {
	            Boolean x = r.isNegotiable();
	            if (x) System.out.println("La restriccion ( " + r.toString() + " ) es negociable.\n");
	            else  System.out.println("La restriccion ( " + r.toString() + " ) NO es negociable.\n");
	        } catch (Exception e) {
	            System.out.println(e);
	        }
		}
		
	
	}
