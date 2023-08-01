import java.io.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;

class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader f = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
        StringTokenizer st = new StringTokenizer(f.readLine());
        Integer n = Integer.parseInt(st.nextToken()); //This is the number of radii
        List<Integer> radii = new ArrayList<Integer>(n);

        //everything besides the predetermined prefs are 0 indexed

        //Process recording capacities for the hospitals
        st = new StringTokenizer(f.readLine());

        radii.add(1); // dummy index at start, set to 1 so it doesn't impact calculation
        for (int i = 0; i < n; i++){
            radii.add(Integer.parseInt(st.nextToken()));
        }
        radii.add(1); // dummy index at end, set to 1 so it doesn't impact calculation

        ArrayList<ArrayList<Integer>> opt = new ArrayList<>(); //initialize an arraylist with n+2 elements, padding on both sides to account for opt(0, n+1) thing mentioned in text

        for(int i = 0; i < (n+2); i++){
            ArrayList<Integer> arr = new ArrayList<>();
            for(int j = 0; j < (n+2); j++){
                arr.add(0);
            }
            opt.add(arr); //adding n+2 arrays of size n+2 to the memoization array
        }

        for(int difference_l_r = 1; difference_l_r < n+2; difference_l_r++){
            for(int l = 0; l < n-difference_l_r+2; l++){
                int r = l + difference_l_r;
                for(int m = l + 1; m < r; m++){ //determine the value of opt(l, r) by finding some m
                    int new_l_r = Math.max(opt.get(l).get(r), radii.get(l)*radii.get(m)*radii.get(r) + opt.get(l).get(m) + opt.get(m).get(r));
                    opt.get(l).set(r, new_l_r); //memoization step
                }
            }
        }            


        out.printf("%d\n", opt.get(0).get(n+1)); //return opt(0,n+1)
        out.close();
    }
}
