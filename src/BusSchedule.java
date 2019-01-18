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

    private static void read_from_file(ArrayList<BusSchedule> list, Scanner reader) throws ParseException {

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

    private static void write_to_file(ArrayList<BusSchedule> list, FileWriter writer){
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

    private static void sort_by_departure_time(ArrayList<BusSchedule> list){
        Collections.sort(list, new Comparator<BusSchedule>() {
            public int compare(BusSchedule object1, BusSchedule object2) {
                return object1.departure_time.compareTo(object2.departure_time);
            }
        });
    }

    private static void remove_long_routers(ArrayList<BusSchedule> list){
        for(int i = 0; i < list.size(); i++){
            if(((list.get(i).arrival_time.getTime() - list.get(i).departure_time.getTime()) / (60 * 1000))>60)
                list.remove(i);
        }
    }

    private static void find_effective_routers(ArrayList<BusSchedule> list, ArrayList<BusSchedule> effective_list){

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

    private static void split_posh_and_grotty(ArrayList<BusSchedule> effective_list,
                                              ArrayList<BusSchedule> posh_list, ArrayList<BusSchedule> grotty_list){

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

            read_from_file(bus_list, reader);
            reader.close();

            ArrayList<BusSchedule> effective_routers = new ArrayList<>();
            ArrayList<BusSchedule> posh_routers = new ArrayList<>();
            ArrayList<BusSchedule> grotty_routers = new ArrayList<>();

            sort_by_departure_time(bus_list);
            remove_long_routers(bus_list);
            find_effective_routers(bus_list, effective_routers);
            split_posh_and_grotty(effective_routers, posh_routers, grotty_routers);
            sort_by_departure_time(posh_routers);
            sort_by_departure_time(grotty_routers);

            FileWriter writer = new FileWriter("output.txt", false);
            write_to_file(posh_routers, writer);
            writer.write("\r\n");
            write_to_file(grotty_routers, writer);
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