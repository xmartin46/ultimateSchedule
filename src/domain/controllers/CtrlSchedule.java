package domain.controllers;

import domain.Schedule;

import domain.Group.DayPeriod;
import domain.Group.Type;
import domain.controllers.CtrlDomain;
import domain.restrictions.DayPeriodRestriction;
import domain.restrictions.RoomCapacityRestriction;
import domain.Timeframe;

import domain.Room;
import domain.Group;
import domain.Restriction;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Map;
// import java.util.Calendar; No s'utilitza ja
import java.util.HashMap;

public class CtrlSchedule {
	
	private static CtrlSchedule instance;
	
	public static CtrlSchedule getInstance() {
		if (instance == null)
			instance = new CtrlSchedule();
		return instance;
	}
	
	private CtrlSchedule() {
		// TODO Auto-generated constructor stub
	}

	
	// **************************** PROVA MAIN ********************************
	
	public static void main(String [] args) {
		CtrlDomain domainController = CtrlDomain.getInstance();
		
		// Map<String, Group> groups = domainController.groups;
		// Map<String, Room> rooms = domainController.rooms;
		
		// Harcodejare els grups i aules
		HashMap<String, Group> groups = new HashMap<String, Group>();
		HashMap<String, Room> rooms = new HashMap<String, Room>();
		
		// Borrem tot el que hi hagi (proves)
		//groups.clear();
		//rooms.clear();
		
		// Aules
		Room room = new Room("A6001", 90, false);
		rooms.put(room.toString(), room);
		
		room = new Room("A6002", 80, false);
		rooms.put(room.toString(), room);
		
		// Grups
		ArrayList<Integer> durations = new ArrayList<Integer>();
		durations.add(1);
		durations.add(2);
		
		Type t = Type.THEORY;
		DayPeriod d = DayPeriod.MORNING;
		Group group = new Group("10", 90, "10", "PRO1", t, d, durations);
		groups.put(group.toString(), group);
		
		
		durations = new ArrayList<Integer>();
		durations.add(2);
		
		t = Type.LABORATORY;
		d = DayPeriod.MORNING;
		group = new Group("11", 30, "10", "PRO1", t, d, durations);
		groups.put(group.toString(), group);
		
		// Fib calendar
		ArrayList<Timeframe> timeFrames = domainController.generatePossibleTimeframes(DayOfWeek.MONDAY, DayOfWeek.FRIDAY, 8, 20);
		
		// Preguntar carlos si ja existeix aquesta funcio: fer un ArrayList de Restriction
		ArrayList<Restriction> restrictions = new ArrayList<Restriction>();
		restrictions.add(new DayPeriodRestriction(12));
		restrictions.add(new RoomCapacityRestriction());
		// S'haur� d'imprimir per pantalla?
		// Guardar en un file?
		generateSchedule(groups, rooms, timeFrames, restrictions);
		System.out.println("All correct");
	}
	
	// ************************************************************************
	
	//TODO: Do we need to pass a Map<String, Subject> as a parameter as well?

	public static void generateSchedule(Map<String, Group> groups, Map<String, Room> rooms, ArrayList<Timeframe> timeFrames, ArrayList<Restriction> restrictions) {
		Schedule schedule = new Schedule();
		
		boolean exists = generate(groups, rooms, timeFrames, restrictions, schedule);
		
		// Ordenar groups i rooms per capacitat en ordre decreixent?
		
		if (exists) {
			// schedule tindr� l'horari que volem
			// retornar schedule o posar l'horari en un arxiu de text...
			schedule.imprimir();
		} else {
			// Error, no es pot crear un horari amb aquestes restriccions
			// Cal treure restriccions (o relaxar-les)
		}
	}
	
	public static boolean checkRestrictions(ArrayList<Restriction> restrictions, Group group, Room room, Timeframe timeFrame, Schedule schedule) {
		for (Restriction r : restrictions) {
	    	if(r.isEnabled() && r.validate(group, room, timeFrame, schedule)) {
	    		// Everything goes perfect
	    	} else {
	    		// group, room i timeFrame no poden anar junts a schedule
	    		// Haurem de provar amb el mateix grup i timeFrame canviant el room fins que no en tinguem m�s
	    		return false;
	    	}
	    }
		return true;
	}
	
	public static boolean generate(Map<String, Group> groups, Map<String, Room> rooms, ArrayList<Timeframe> timeFrames, ArrayList<Restriction> restrictions, Schedule schedule) {
		// Mirar si hem anat per totes les hores possibles
		if (timeFrames.isEmpty()) { // timeFrames �s buit
			// No ens queden m�s hores per mirar
			
			// Si encara hi ha grups per posar
			if (!groups.isEmpty()) {
				// Retornar flase indicant que no es pot crear un horari amb aquestes restriccions
				return false;
			} else {
				// Existeix un horari amb aquestes restriccions
				return true;
			}
		} else if (groups.isEmpty()) { // timeFrames no �s buit i groups s�
			// No queden m�s grups per posar
			// Queden timeFrames per posar
			// �s possible que quedin aules lliures. No tenim cap problema amb aix�
			
			// Tirar cap a baix posant el timeFrame al schedule?
			//						o
			// deixar-ho en blanc (no afegir el timeFrame)
			return true;
		} else { // timeFrames no �s buit i groups tampoc
			// Boole� que indica si hi ha soluci� o no
			boolean result = false;
			// Encara es poden mirar timeFrames
			
			/// Mirar aqu� si ja no hi caben m�s lectures a timeFrames(0)? 
			
			// Clonem grups
			HashMap<String, Group> copyGroups = new HashMap<String, Group>(groups);
			
			// Ho agafem aleatori? Jo de moment no ho far�
			for (Map.Entry<String, Group> groupE : groups.entrySet()) {
				// 1. take the value of the group
				Group group = groupE.getValue();
				
				// 2. pick the first timeFrame from the available ones
				Timeframe timeFrame = timeFrames.get(0);
				
				// Es posar� a true si trobem una room que compleixi les restriccions per group i timeFrame
				boolean possibleAnswer = false;
				
				for (Map.Entry<String, Room> roomE : rooms.entrySet()) {
					if (!possibleAnswer) {
						// 3. pick the room object
						Room room = roomE.getValue();
						
						// 4. Watch if group, room and timeFrame "compleixen" the restrictions in the given schedule
						possibleAnswer = checkRestrictions(restrictions, group, room, timeFrame, schedule);
						
						if (possibleAnswer) { // Trobada room correcta
							// Hi ha soluci�
							result = true;
							
							// Tenim un group, room i timeFrame que poden anar a l'schedule
							String r = room.toString();
							String g = group.toString();
							Integer duration = group.getLectureDuration(0);
							//System.out.println(timeFrame);
							// Afegir la lecture room, group i timeFrame amb duration
							boolean correcte = false;
							correcte = schedule.addLecture(timeFrame, r, g, duration);			

							// Elimino la duraci� del grup que acabo d'afegir
							//if (correcte) {
								if (correcte) {
									group.removeLecture(duration);
									
									if (group.hasLectures()) {
										// Encara s'han d'afegir lectures del grup
										if (schedule.getLecturesAtTime(timeFrame).size() < rooms.size()) {
											// Encara hi caben grups + aula en aquella hora
											result = generate(groups, rooms, timeFrames, restrictions, schedule);
										} else {
											// No hi cap res mes
											
											// Eliminem el timeFrame actual
											timeFrames.remove(0);
											
											// Backtracking
											result = generate(groups, rooms, timeFrames, restrictions, schedule);
											
											// Tornem a posar el timeFrame
											timeFrames.add(0, timeFrame);
										}
									} else {
										// Elimino el grup
										copyGroups.remove(group.toString());
										
										// Backtracking
										if (schedule.getLecturesAtTime(timeFrame).size() < rooms.size()) {
											// Encara hi caben grups + aula en aquella hora
											result = generate(copyGroups, rooms, timeFrames, restrictions, schedule);
										} else {
											// No hi cap res m�s
											
											// Eliminem el timeFrame actual
											timeFrames.remove(0);
											
											// Backtracking
											result = generate(copyGroups, rooms, timeFrames, restrictions, schedule);
											
											// Tornem a posar el timeFrame
											timeFrames.add(0, timeFrame);
										}
										
										// Afageixo el grup de nou
										copyGroups.put(group.toString(), group);
									}
									
									// Tornar a afegir la duraci� del grup
									group.addLecture(duration);
								} else {
									possibleAnswer = false;
									
								}
								
								
							} else { // Canviar de grup, no de room?
								// No s'ha pogut afegir la lecture
								possibleAnswer = false;
								result = false;
								//break;
							}
						}
					//} else { // No fer res
						// No miris res, ja tenim una room que compleix les restriccions
						// Segur? Fer algo amb el backtracking?
					//}
				}
				
				/*
				 * Fa falta aix�? => Crec que no
				if (!found) {
					// No s'ha trobat cap combinaci� room, group i timeFrame, per� potser el seg�ent grup hi cap
					
					//No hi ha cap room que aconsegueixi cumplir amb les restriccions tenint group i timeFrame
					//Avan�ar 1 timeFrame (backtracking)
					
					//Eliminem el timeFrame actual
					timeFrames.remove(0);
					
					//Backtracking
					result = generate(groups, rooms, timeFrames, restrictions, schedule);
					
					//Tornem a posar el timeFrame
					timeFrames.add(0, timeFrame);
					
					return result;
				} else {
					// S'haur� afegit
				}*/
			}
			
			if (!result) {
				// Amb aquest timeFrame no es pot fer res m�s
				// Encara no que no estigui ple l'eliminem
				
				// Eliminem el timeFrame actual
				Timeframe timeFrame = timeFrames.get(0);
				timeFrames.remove(0);
				
				// Backtracking
				result = generate(groups, rooms, timeFrames, restrictions, schedule);
				
				// Tornem a posar el timeFrame
				timeFrames.add(0, timeFrame);
			}
			return result;
		}
	}

	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	/* ArrayList<Calendar> oldTimeFrames = new ArrayList<Calendar>(timeFrames);
		
		for (Map.Entry<String, Group> groupE : groups.entrySet()) { // Fa falta aquest for? Si cridem recursivament ja estaria no? fer group.get(0);
			Group group = groupE.getValue();
<<<<<<< HEAD
			Room room = rooms.get(0); // Alguna altra millor manera d'agafar-lo?
			Calendar timeFrame = timeFrames.get(0);
=======
			Room room = rooms.get("A6-001");
			Timeframe timeFrame = timeFrames.get(0);
>>>>>>> origin/master
			//1. pick random timeframe from the available ones?
			//2. pick random room from the available ones?
			boolean isFeasible = true;
		    for (Restriction r : restrictions) {
		    	if(r.isEnabled() && r.validate(group, room, timeFrame, schedule)) { 
		    		
		    	} else {
		    		isFeasible = false;
		    	}
		    }
		    
		    if (isFeasible) {
			    // L'afageixo a l'horari
		    	// Pot donar errors de duraci�? O aix� ja ho mirar� les restriccions?
		    	schedule.addLecture(timeFrame, room.toString(), group.toString(), 1);
		    	
		    	// Borro room, group i timeFrame
			    rooms.remove(room.toString());
			    groups.remove(group.toString());
			    timeFrames.remove(timeFrame);
			    
			    // Cridem recursivament
			    generateSchedule(groups, rooms, timeFrames, restrictions);
			    
			    // Tornem a afegir room, group i timeFrame agafats
			    rooms.put(room.toString(), room);
			    groups.put(group.toString(), group);
			    timeFrames.add(timeFrame);
			    
		    } else {
		    	// Trec el timeFrame agafat per poder mirar el seg�ent
		    	timeFrames.remove(timeFrame);
		    	
		    	// Crido recursivament
		    	generateSchedule(groups, rooms, timeFrames, restrictions);
		    	
		    	// Torno a afegir el timeFrame
		    	timeFrames.add(timeFrame);
		    	
		    	
		    	 * Tamb� podria haver estat aix�:
		    	 * 
		    	 * exit inner loop and backtrack
		    	 * timeFrames = oldTimeFrames;
		    	 * 
		    	 * Qu� �s millor?
		    	 
		    }
		}
		return schedule;
	}*/
}