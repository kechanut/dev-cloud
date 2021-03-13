import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;



@WebServlet(
    name = "HelloAppEngine",
    urlPatterns = {"/hello"}
)

public class HelloAppEngine extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
	  System.out.println("test");
	  Random r = new Random();
	   	 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	   	 // Create Petition
	   	 for (int i = 0; i < 500; i++) {
	   		 Entity e = new Entity("Petition", "P" + i); // à améliorer
	   		 int owner=r.nextInt(1000);
	   		 e.setProperty("Owner", "U" + owner);
	   		 e.setProperty("Date", new Date()); //date aléatoire (import java util)
	   		 e.setProperty("Body","ho yo");
	   		 
	   		 // Create random votants
	   		 HashSet<String> fset = new HashSet<String>();
	   		 for (int j=0; j<200; j++){
	   			 fset.add("U" + r.nextInt(1000));
	   		 }
	   		 e.setProperty("votants", fset);
	   		 e.setProperty("nbvotants", fset.size());
	   		 
	   		 // Create random tags
	   		 HashSet<String> ftags = new HashSet<String>();
	   		 while (ftags.size() < 10) {
	   			 ftags.add("T" + r.nextInt(100));
	   		 }
	   		 e.setProperty("tags", ftags);
	   		 
	   		 datastore.put(e);
	   		 response.getWriter().print("<li> created post:" + e.getKey() + "<br>");
	   	 }
  }
  
  
  
  	@ApiMethod(name = "addScore", httpMethod = HttpMethod.GET)
 	public Entity addScore(@Named("score") int score, @Named("name") String name) {

 		Entity e = new Entity("Score", "" + name + score);
 		e.setProperty("name", name);
 		e.setProperty("score", score);

 		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
 		datastore.put(e);
 		 
 		return e;
 	}
 	
  
 /* @ApiMethod(name = "GetValue", httpMethod = HttpMethod.GET)
	public int GetValue() {
	 
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity e = new Entity("Score");
		Entity score = datastore.get(e.getKey());
	}*/
}