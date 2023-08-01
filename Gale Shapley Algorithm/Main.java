import java.io.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;

class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader f = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
        StringTokenizer st = new StringTokenizer(f.readLine());
        Integer m = Integer.parseInt(st.nextToken()); //This is the number of hospitals
        Integer n = Integer.parseInt(st.nextToken()); //This is the number of students
        List<Integer> capacities = new ArrayList<Integer>(m);

        //everything besides the predetermined prefs are 0 indexed

        //Process recording capacities for the hospitals
        for (int i = 0; i < m; i++){
            st = new StringTokenizer(f.readLine());
            capacities.add(Integer.parseInt(st.nextToken()));
            //out.printf("%d\n", capacities.get(i));
        }
        List<List<Integer>> hospital_prefs = new ArrayList<>(m); //This is the preference lists of hopsitals
        List<HashMap<Integer, Integer>> resident_prefs = new ArrayList<>(n); //This is the preference lists of residents
        LinkedList<Integer> open_hospitals = new LinkedList<Integer>(); //This is a linked list of not full hospitals
        List<Integer> matching = new ArrayList<Integer>(Collections.nCopies(n, -1)); //current matching, resident number is the key
        List<Integer> current_num_residents = new ArrayList<Integer>(Collections.nCopies(m, 0)); //tracks the current resident occupancy of the hospitals
        List<Integer> next_offer = new ArrayList<Integer>(Collections.nCopies(m, 0)); //next_offer[i] is the index 
        //of the next resident to be offered to by hospital i


        for (int i = 0; i < m; i++){ //This handles processing of hospital preference lists
            List<Integer> prefs = new ArrayList<>();
            st = new StringTokenizer(f.readLine());
            for (int j = 0; j < n; j++){
                prefs.add((Integer.parseInt(st.nextToken()))-1);
            }
            hospital_prefs.add(prefs);
        }

        for (int i = 0; i < n; i++){ //This handles processing of resident preference lists
            HashMap<Integer, Integer> prefs = new HashMap<Integer, Integer>();
            st = new StringTokenizer(f.readLine());
            for (int j = 0; j < m; j++){
                prefs.put((Integer.parseInt(st.nextToken())-1), j);
            }
            resident_prefs.add(prefs);
        }

        for (int i = 0; i < m; i++){ //This handles adding hospitals to open hospital list initially
            open_hospitals.add(i);
        }

        while (open_hospitals.peek() != null){
            //identify the offering hospital
            int offering_hospital = open_hospitals.peek();
            //identify highest non-previously offered resident
            int offer_number = next_offer.get(offering_hospital);
            int offered_to_resident = hospital_prefs.get(offering_hospital).get(offer_number);
            if (matching.get(offered_to_resident) == -1){
                matching.set(offered_to_resident, offering_hospital);
                int prev = current_num_residents.get(offering_hospital);
                current_num_residents.set(offering_hospital, prev+1);
                next_offer.set(offering_hospital, offer_number+1);
                if (prev + 1 == capacities.get(offering_hospital)){
                    open_hospitals.pop();
                } 
            }
            else{
                int previous_match = matching.get(offered_to_resident);
                int prev_priority = resident_prefs.get(offered_to_resident).get(previous_match);
                int new_priority = resident_prefs.get(offered_to_resident).get(offering_hospital);
                if (new_priority < prev_priority){
                    matching.set(offered_to_resident, offering_hospital);
                    int prev = current_num_residents.get(offering_hospital);
                    current_num_residents.set(offering_hospital, prev+1);
                    if (prev + 1 == capacities.get(offering_hospital)){
                        open_hospitals.pop();
                    }
                    current_num_residents.set(previous_match, current_num_residents.get(previous_match)-1);
                    if (current_num_residents.get(previous_match) + 1 == capacities.get(previous_match)){
                        open_hospitals.add(previous_match);
                    }

                }
                next_offer.set(offering_hospital, offer_number+1);
            }
        }


        

        for (int i = 0; i < n; i++){
            out.printf("%d\n", matching.get(i)+1);
        }

        out.close();
    }
}