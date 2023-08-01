import java.io.*;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class Main {
    //I've tried to optimize this without completely redoing it, but its hard to do so
    static boolean search_for_path(int s, int t, List<List<Integer>> residual_graph, List<Integer> path, List<List<Integer>> tracker){
        int n = residual_graph.size();
        //list of all of the unvisited nodes... functions as hashmap
        List<Boolean> unvisited = new ArrayList<Boolean>();
        for(int i = 0; i < n; i++){ unvisited.add(true); }

        //queue of the nodes to check
        Queue<Integer> queue = new LinkedList<>();
        //set the previous/parent of s to minint indicating it is the start
        path.set(s, Integer.MIN_VALUE);
        unvisited.set(s, false);
        queue.add(s);

        while(queue.size() != 0){
            int current_node = queue.remove();
            for(int j = 0; j < tracker.get(current_node).size(); j++){
                int i = tracker.get(current_node).get(j);
                if(residual_graph.get(current_node).get(i) != 0){
                    if(unvisited.get(i) == true){
                        if(i != t){
                            unvisited.set(i,false);
                            queue.add(i);
                            path.set(i, current_node);
                        }
                        if(i == t){
                            path.set(i,current_node);
                            return true; //there is a path
                        }
                    }

                }
            }
        }
        return false; //at the end there is no path...
    }

    static Set<Integer> search_visitable(int s, List<List<Integer>> residual_graph){
        int n = residual_graph.size();
        //list of all of the unvisited nodes
        List<Boolean> unvisited = new ArrayList<Boolean>();
        for(int i = 0; i < n; i++){
            unvisited.add(true);
        }

        //queue of all of the nodes
        Queue<Integer> queue = new LinkedList<>();
        //set of reached nodes
        Set<Integer> visitable = new HashSet<Integer>();

        queue.add(s);
        unvisited.set(s, false);
        visitable.add(s);

        while(queue.size() != 0){
            int current_node = queue.remove();

            for(int i = 0; i < n; i++){
                if(residual_graph.get(current_node).get(i) != 0){
                    if(unvisited.get(i) == true){
                        if(i != s){
                            visitable.add(i);
                        }
                        unvisited.set(i,false);
                        queue.add(i);
                    }
                }
            }
        }
        return visitable;
    }

    static List<List<Integer>> ford_fulkerson(List<List<Integer>> residual_graph, boolean flow, List<List<Integer>> tracker){
        int s = 0;
        int t = residual_graph.size()-1;
        //create the path that will track path from s to t
        List<Integer> path = new ArrayList<>();
        for(int i = 0; i < residual_graph.size(); i++){
            path.add(0);
        }
        int max_flow = 0;
        while(search_for_path(s, t, residual_graph, path, tracker) == true){
            //this will be the bottleneck flow throught the residual graph
            int bottle_flow = Integer.MAX_VALUE;
            int current_node = t;
            
            //this will find the bottleneck flow
            while(current_node != s){
                bottle_flow = Math.min(bottle_flow, residual_graph.get(path.get(current_node)).get(current_node));
                current_node = path.get(current_node);
            }
            //this updates the max flow
            max_flow = max_flow + bottle_flow;

            //this updates the residual graph
            //note: you can update the residual graph without updating the actual graph
            //subtract bottleflow from forward edges along the path
            //add bottleflow to reverse edges along the path
            current_node = t;
            while(current_node != s){
                int previous_node = path.get(current_node);
                int previous_forward = residual_graph.get(previous_node).get(current_node);
                int previous_reverse = residual_graph.get(current_node).get(previous_node);
                //reset these values
                //forward flow in rg
                residual_graph.get(previous_node).set(current_node, previous_forward - bottle_flow);
                residual_graph.get(current_node).set(previous_node, previous_reverse + bottle_flow);
                current_node = previous_node;
            }
        }
        if(flow == true){
            residual_graph.get(s).set(t, max_flow); //a way of passing max-flow back to main
        }
        return residual_graph;
    }

    static List<List<Integer>> reverse_graph(List<List<Integer>> residual_graph, int n){
        //reverse the graph so that you can run search
        List<List<Integer>> rg_reversed_edges = new ArrayList<>();
        //initializing all edge capacities to 0 to start
        for(int i = 0; i < n; i++){
            ArrayList<Integer> new_a = new ArrayList<>();
            for(int j = 0; j < n; j++){
                new_a.add(0);
            }
            rg_reversed_edges.add(new_a);
        }
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                int cap = residual_graph.get(i).get(j);
                rg_reversed_edges.get(j).set(i,cap);
            };
        }
        return rg_reversed_edges;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader f = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
        StringTokenizer st = new StringTokenizer(f.readLine());
        Integer n = Integer.parseInt(st.nextToken()); //This is the number of nodes
        Integer m = Integer.parseInt(st.nextToken()); //This is the number of edges

        Integer s = 0;
        Integer t = n-1;

        //Process recording scores for all edges
        List<Integer> scores = new ArrayList<Integer>(n);
        st = new StringTokenizer(f.readLine());
        for (int i = 0; i < n; i++){
            scores.add(Integer.parseInt(st.nextToken()));
        }  

        //initializing all edge capacities in residual graph to 0 to start
        List<List<Integer>> residual_graph = new ArrayList<>();
        for(int i = 0; i < n; i++){
            ArrayList<Integer> new_a = new ArrayList<>();
            for(int j = 0; j < n; j++){
                new_a.add(0);
            }
            residual_graph.add(new_a);
        }   
        //create edges of proper capacity, 0 means there is no edge there
        //NOTE: at start residual graph is the same as capacities graph
        for(int i = 0; i < m; i++){
            st = new StringTokenizer(f.readLine());
            int first_node = Integer.parseInt(st.nextToken());
            int second_node = Integer.parseInt(st.nextToken());
            int capacity = Integer.parseInt(st.nextToken());
            residual_graph.get(first_node-1).set(second_node-1, capacity);
        }

        //tracker[i] tracks the node j that represent possible edges (i,j) in the residual graph
        List<List<Integer>> tracker = new ArrayList<>();
        for(int i = 0; i < n; i++){
            ArrayList<Integer> trkr = new ArrayList<>();
            tracker.add(trkr);
        }
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                int cap = residual_graph.get(i).get(j);
                if(cap!=0){
                    tracker.get(i).add(j);
                    tracker.get(j).add(i);
                }
            }
        }

        //creating a graph of the actual flow is not necessary
        //all you need to actual calculate it is to track the
        //residual graph
        residual_graph = ford_fulkerson(residual_graph, false, tracker);
        Set<Integer> As = search_visitable(s, residual_graph);
        List<List<Integer>> rg_reversed_edges = reverse_graph(residual_graph, n);
        Set<Integer> Bt = search_visitable(t, rg_reversed_edges);
        
        //definition of v'... this should be correct if Bt and As are correct
        List<Integer> v_prime = new ArrayList<>();
        for(int i = 0; i < n; i++){
            if(!(Bt.contains(i) || As.contains(i))){
                    v_prime.add(i);
            }
        }

        int C = 0;
        for(int i = 0; i < v_prime.size(); i++){
            if(scores.get(v_prime.get(i)) > 0){
                C = C + scores.get(v_prime.get(i));
            }
        }

        List<List<Integer>> project_selection_graph = new ArrayList<>();
        List<List<Integer>> ps_tracker = new ArrayList<>();
        //initializing all edge capacities to 0 to start
        for(int i = 0; i < n+2; i++){
            ArrayList<Integer> new_a = new ArrayList<>();
            ArrayList<Integer> new_a2 = new ArrayList<>();
            for(int j = 0; j < n+2; j++){
                new_a.add(0);
            }
            project_selection_graph.add(new_a);
            ps_tracker.add(new_a2);
        }

        for(int i = 0; i < v_prime.size(); i++){
            int score = scores.get(v_prime.get(i));
            if(score > 0){
                project_selection_graph.get(0).set(v_prime.get(i)+1,score);
                ps_tracker.get(0).add(v_prime.get(i)+1);
                ps_tracker.get(v_prime.get(i)+1).add(0);
            }
            if(score < 0){
                project_selection_graph.get(v_prime.get(i)+1).set(n+1,-1*score);
                ps_tracker.get(v_prime.get(i)+1).add(n+1);
                ps_tracker.get(n+1).add(v_prime.get(i)+1);
            }
        }

        for(int i = 0; i < v_prime.size(); i++){
            for(int j = 0; j < v_prime.size(); j++){
                if(v_prime.get(i) != v_prime.get(j)){
                    if(residual_graph.get(v_prime.get(i)).get(v_prime.get(j)) != 0){
                        project_selection_graph.get(v_prime.get(i)+1).set(v_prime.get(j)+1, C + 1);
                        ps_tracker.get(v_prime.get(i)+1).add(v_prime.get(j)+1);
                        ps_tracker.get(v_prime.get(j)+1).add(v_prime.get(i)+1);
                    }
                }
            }
        }
        
        List<List<Integer>> ff_job_selection =  ford_fulkerson(project_selection_graph, true, ps_tracker);
        int job_flow = ff_job_selection.get(0).get(ff_job_selection.size()-1);
        // profit = C - maxflow = C - c(A',B') as described in 7.11 of textbook
        int profit = C - job_flow;

        int As_Sum = 0;
        for(int i = 0; i < n; i++){
            if(As.contains(i)){
                As_Sum = As_Sum + scores.get(i);
            }
        }

        out.printf("%d\n", (profit + As_Sum));
        out.close();
    }
}