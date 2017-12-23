package csi403;

// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.json.*;
import java.util.*;
import java.lang.*;

// Extend HttpServlet class
public class TopologicalSortTest extends HttpServlet {

  public PrintWriter out;

  // Standard servlet method 
  public void init() throws ServletException
  {
      // Do any required initialization here - likely none
  }

  // Standard servlet method - handles a POST operation
  public void doPost(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      response.setContentType("application/json");
      out = response.getWriter();

      try {
          doService(request, response);
      } catch (Exception e){
          e.printStackTrace();
          out.println("{ \"message\" : \"Malformed JSON\"}");
      }
  }

  // Standard servlet method - does not respond to GET
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      // Set response content type and return an error message
      response.setContentType("application/json");
      out = response.getWriter();
      out.println("{ \"message\" : \"Use POST!\"}");
  }


  // Our main worker method
  private void doService(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      // Get received JSON data from HTTP request
      BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
      String jsonStr = "";
      if(br != null){
          jsonStr = br.readLine();
      }

      // Create JsonReader object
      StringReader strReader = new StringReader(jsonStr);
      JsonReader reader = Json.createReader(strReader);

      // Get the singular JSON object (name:value pair) in this message.    
      JsonObject obj = reader.readObject();

      //If more than one key:value pair (not only "inList" present), send an error message
      if(obj.size() > 1){
        out.println("{ \"message\" : \"Invalid number of key:value pairs\" }");
        return;
      }

      // From the object get the array named "inList"
      JsonArray inArray = obj.getJsonArray("inList");

      //Declare variable to hold each element of the inArray
      JsonObject element;
      //Declare variable to hold each element's JsonArray
      String[] names = new String[2];

      //ArrayList of String for the people's names
      ArrayList<String> people = new ArrayList<String>();
      //ArrayList of Linked List<String> for adjacency representations
      ArrayList<LinkedList<String>> adjacencies = new ArrayList<LinkedList<String>>();
      //Temporary LinkedList to update adjacencies
      LinkedList<String> tempList;
      //Array of ints for # of in degrees
      int[] inDegrees;

      //Access and execute the commands from "inList"
      for(int i = 0; i < inArray.size(); i++){
          element = inArray.getJsonObject(i);

          //If more than one key:value pair (not only "smarter" present), send an error message
          if(element.size() > 1){
              out.println("{ \"message\" : \"Invalid number of key:value pairs\" }");
              return;
          }

          //If element does not have "smarter", send an error message
          if (!element.containsKey("smarter")){
              out.println("{ \"message\" : \"No \"smarter\" present\" }");
              return;
          }

          //If "smarter" array size exceeds 2, send an error message
          if (element.getJsonArray("smarter").size() > 2){
              out.println("{ \"message\" : \"\"smarter\" array exceeds size limit of 2\" }");
              return;
          }

          //Initialize names array to be the elements
          for(int k = 0; k < element.getJsonArray("smarter").size(); k++) {
              names[k] = element.getJsonArray("smarter").getString(k);
          }

          //If people doesn't contain the name yet, add it and add its outdegree to adjacencies
          if(!people.contains(names[0])) {
              people.add(names[0]);
              tempList = new LinkedList<String>();
              tempList.add(names[1]);
              adjacencies.add(tempList);
          }
          //Otherwise, add the new outdegree to adjacencies
          else{
              tempList = adjacencies.get(people.indexOf(names[0]));
              tempList.add(names[1]);
              adjacencies.set(people.indexOf(names[0]), tempList);
          }
      }

      //Check if there are any Strings in adjacencies that aren't present in people, and if so add them
      for(int i = 0; i < adjacencies.size(); i++) {
          if (adjacencies.get(i) != null){
              tempList = adjacencies.get(i);
              for(int j = 0; j < tempList.size(); j++) {
                  if (!people.contains(tempList.get(j))) {
                      people.add(tempList.get(j));
                      adjacencies.add(null);
                  }
              }
          }
      }

      //Initialize int array inDegrees to contain all zeros
      inDegrees = new int[adjacencies.size()];

      //Increment inDegree number if needed
      for(int i = 0; i < adjacencies.size(); i++){
          if (adjacencies.get(i) != null){
              tempList = adjacencies.get(i);
              for(int j = 0; j < tempList.size(); j++){
                  inDegrees[people.indexOf(tempList.get(j))]++;
              }
          }
      }

      ArrayList<String> finalList = topologicalSort(inDegrees, people, adjacencies);
      //If there are no cycles in the graph
      if (finalList.size() == people.size()){
            JsonArrayBuilder outArrayBuilder = Json.createArrayBuilder();
            for (String s : finalList)
                outArrayBuilder.add(s);
            out.println("\"outlist\" : " + outArrayBuilder.build().toString() + "}");
      }
      //Otherwise, send an error message
      else{
          out.println("{ \"message\" : \"Cyclic order present, impossible sort\" }");
          return;
      }

      /* Print the priority queue in order
      JsonArrayBuilder outArrayBuilder = Json.createArrayBuilder();
      while(!pq.isEmpty())
          outArrayBuilder.add(pq.poll().getName());

      out.println("{ \"outList\" : " + outArrayBuilder.build().toString() + " }");
      */
  }

    private static ArrayList<String> topologicalSort(int[] inDegrees, ArrayList<String> people, ArrayList<LinkedList<String>> adj){
        Stack<String> s = new Stack<String>();

        for(int i = inDegrees.length - 1; i >= 0; i--){
            if (inDegrees[i] == 0){
                s.add(people.get(i));
            }
        }

        ArrayList<String> sorted = new ArrayList<String>();
        String temp;
        int i = 0;
        int index;

        while(!s.isEmpty()){
            temp = s.pop();
            sorted.add(i, temp);
            i++;
            index = people.indexOf(temp);

            while(adj.get(index) != null && adj.get(index).size() > 0){
                temp = adj.get(index).getLast();
                inDegrees[people.indexOf(temp)]--;
                if (inDegrees[people.indexOf(temp)] == 0){
                    s.push(temp);
                }
                adj.get(index).removeLast();
            }
        }

        return sorted;
    }

  // Standard Servlet method
  public void destroy() {
      // Do any required tear-down here, likely nothing.
  }
}