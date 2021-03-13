package Pet;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
//import com.google.api.server.spi.config.Named;
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

/*
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
*/
import java.util.Date;
import java.util.List;
import java.util.Random;



@Api(name = "myApi",
	version = "v1",
	audiences = "927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
	clientIds = "927375242383-t21v9ml38tkh2pr30m4hqiflkl3jfohl.apps.googleusercontent.com",
	namespace =
	@ApiNamespace(
		   ownerDomain = "developpement-cloud.appspot.com",
		   ownerName = "developpement-cloud.appspot.com",
		   packagePath = "")
	)

public class Petition {
	/*
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {

		    response.setContentType("text/plain");
		    response.setCharacterEncoding("UTF-8");

		    response.getWriter().print("Hello Petition test!\r\n");
		    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    Entity e = new Entity("Score");
		    e.setProperty("score", 0);
		    datastore.put(e);
		    
		    
		    System.out.println("test");
	  }
	
	/*
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
			}

	*/
	
	@ApiMethod(name = "addpetition", httpMethod = HttpMethod.POST)
	public Entity addpetition(@Named("name") String name,@Named("desc") String desc, @Named("mail") String mailCreateur) {
		//Entity e = new Entity("Petition", Long.MAX_VALUE - (new Date().getTime()+":"+ mailCreateur)));
		Entity e = new Entity("Petition");
		e.setProperty("Name", name);
		e.setProperty("description", desc);
		e.setProperty("nbVote", 0);
		e.setProperty("createur", mailCreateur);
		e.setProperty("date", new Date());

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(e);
		
		return e;
	}
	
	@ApiMethod(name = "allPetition", httpMethod = HttpMethod.GET)
	public List<Entity> allPetition() {
		System.out.println("test allpetition");
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition").addSort("Key", SortDirection.DESCENDING);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		return result;
	}
	
	@ApiMethod(name = "petitionVotedByUser", httpMethod = HttpMethod.GET)
	public List<Entity> petitionVotedByUser(@Named("name") String name) {
		
		System.out.println("test"+name);
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition").setFilter(new FilterPredicate("votants", FilterOperator.EQUAL, name));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		return result;
	}
	
	@ApiMethod(name = "petitionByName", httpMethod = HttpMethod.GET)
	public List<Entity> petitionByName(@Named("name") String name) {
		
		System.out.println("test"+name);
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition").setFilter(new FilterPredicate("Name", FilterOperator.EQUAL, name));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		return result;
	}
	
	@ApiMethod(name = "BestPetition", httpMethod = HttpMethod.GET)
	public List<Entity> BestPetition() {
		System.out.println("BestPetition");
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition").addSort("nbvotants", SortDirection.DESCENDING);;
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		return result;
	}
	
	
	
	
}
