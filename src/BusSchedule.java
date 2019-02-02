import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.*;

public class BusSchedule {

    private String company;
    private Date departure_time;
    private Date arrival_time;

    private BusSchedule(String c, Date d_t, Date a_t){
        company = c;
        departure_time = d_t;
        arrival_time = a_t;
    }

    private static void readFromFile(ArrayList<BusSchedule> list, Scanner reader) throws ParseException {

        while (reader.hasNextLine()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String company = reader.next();
            BusSchedule bs;

            Date depart_date = dateFormat.parse(reader.next());
            Date arrive_date = dateFormat.parse(reader.next());
            bs = new BusSchedule(company, depart_date, arrive_date);
            list.add(bs);
        }
    }

    private static void writeToFile(ArrayList<BusSchedule> list, FileWriter writer){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            for(int i = 0; i < list.size(); i++){
                writer.write(list.get(i).company + " " +
                        dateFormat.format(list.get(i).departure_time) + " " +
                        dateFormat.format(list.get(i).arrival_time) + "\r\n");
            }
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void sortByDepartureTime(ArrayList<BusSchedule> list){
        Collections.sort(list, new Comparator<BusSchedule>() {
            public int compare(BusSchedule object1, BusSchedule object2) {
                return object1.departure_time.compareTo(object2.departure_time);
            }
        });
    }

    private static void removeLongRouters(ArrayList<BusSchedule> list){
        for(int i = 0; i < list.size(); i++){
            if(((list.get(i).arrival_time.getTime() - list.get(i).departure_time.getTime()) / (60 * 1000))>60)
                list.remove(i);
        }
    }

    private static void findEffectiveRouters(ArrayList<BusSchedule> list, ArrayList<BusSchedule> effective_list){

        for(BusSchedule l : list) {
            effective_list.add(l);
        }

        for(int i = 0; i < list.size(); i++){
            for(int j = i + 1; j < list.size(); j++){

                if(list.get(i).departure_time.equals(list.get(j).departure_time) &&
                        list.get(i).arrival_time.equals(list.get(j).arrival_time)) {

                    if (list.get(i).company.equals("Posh")) effective_list.get(j).company = "del";
                    else effective_list.get(i).company = "del";;
                }

                if(list.get(i).departure_time.equals(list.get(j).departure_time)) {

                    if(list.get(i).arrival_time.before(list.get(j).arrival_time))
                        effective_list.get(j).company = "del";
                    else if(list.get(i).departure_time.after(list.get(j).departure_time))
                        effective_list.get(i).company = "del";
                }

                if(list.get(i).arrival_time.equals(list.get(j).arrival_time)) {

                    if(list.get(i).departure_time.after(list.get(j).departure_time))
                        effective_list.get(j).company = "del";
                    else if(list.get(i).departure_time.before(list.get(j).departure_time))
                        effective_list.get(i).company = "del";
                }

                if(list.get(i).departure_time.after(list.get(j).departure_time) &&
                        list.get(i).arrival_time.before(list.get(j).arrival_time)) {
                    effective_list.get(j).company = "del";
                }
                if(list.get(i).departure_time.before(list.get(j).departure_time) &&
                        list.get(i).arrival_time.after(list.get(j).arrival_time)) {
                    effective_list.get(i).company = "del";
                }
            }
        }
    }

    private static void splitPoshAndGrotty(ArrayList<BusSchedule> effective_list,
                                           ArrayList<BusSchedule> posh_list,
                                           ArrayList<BusSchedule> grotty_list){

        for(int i = 0; i<effective_list.size(); i++){
            if(effective_list.get(i).company.equals("Posh"))
                posh_list.add(effective_list.get(i));
            else if (effective_list.get(i).company.equals("Grotty"))
                grotty_list.add(effective_list.get(i));
        }
    }

    public static void main(String[] args) {
        try{
            ArrayList<BusSchedule> bus_list = new ArrayList<>();

            Scanner reader = new Scanner(new File("input.txt"));

            readFromFile(bus_list, reader);
            reader.close();

            ArrayList<BusSchedule> effective_routers = new ArrayList<>();
            ArrayList<BusSchedule> posh_routers = new ArrayList<>();
            ArrayList<BusSchedule> grotty_routers = new ArrayList<>();

            sortByDepartureTime(bus_list);
            removeLongRouters(bus_list);
            findEffectiveRouters(bus_list, effective_routers);
            splitPoshAndGrotty(effective_routers, posh_routers, grotty_routers);
            sortByDepartureTime(posh_routers);
            sortByDepartureTime(grotty_routers);

            FileWriter writer = new FileWriter("output.txt", false);
            writeToFile(posh_routers, writer);
            writer.write("\r\n");
            writeToFile(grotty_routers, writer);
            writer.close();
        }

        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        catch (ParseException pe){
            System.out.println(pe.getMessage());
        }
    }
}