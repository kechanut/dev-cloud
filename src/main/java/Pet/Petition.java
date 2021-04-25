package Pet;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
//import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Projection;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.ArrayList;
import java.util.Calendar;

/*
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
*/
import java.util.Date;
import java.util.HashSet;
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
		System.out.println("name :"+name );
		System.out.println("desc :"+desc );
		Date d = new Date();
		Entity e = new Entity("Petition", Long.MAX_VALUE - new Date().getTime() + mailCreateur);
		e.setProperty("Key", Long.MAX_VALUE - new Date().getTime() + mailCreateur);
		e.setProperty("Name", name);
		e.setProperty("Body", desc);
		e.setProperty("nbvotants", 0);
		e.setProperty("votants", new HashSet<String>());
		e.setProperty("createur", mailCreateur);
		e.setProperty("jour", new SimpleDateFormat("dd/MM/yyyy").format(d));
		e.setProperty("heure", new SimpleDateFormat("HH:mm").format(d));

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(e);
		System.out.println("valeur de retour "+ e);
		return e;
	}
	

	@ApiMethod(name = "allPetition", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> allPetition( @Nullable @Named("next") String cursorString) {
		long t1=System.currentTimeMillis();
		System.out.println("test allpetition");
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		
		
		//query avec projection sur les éléments utiles
		Query q = new Query("Petition");
				/*.addProjection(new PropertyProjection("Key", String.class))
				.addProjection(new PropertyProjection("Name", String.class))
				.addProjection(new PropertyProjection("Body", String.class))
				.addProjection(new PropertyProjection("nbvotants", Long.class))
				.addProjection(new PropertyProjection("createur", String.class))
				.addProjection(new PropertyProjection("jour", String.class))
				.addProjection(new PropertyProjection("heure", String.class))
				.addSort("Key", SortDirection.DESCENDING);*/
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);
		
		//Cursor pour pagination
		if (cursorString != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
	    }
		//List<Entity> result = pq.asQueryList(FetchOptions.Builder.withLimit(100));
		 QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
		cursorString = results.getCursor().toWebSafeString();
		
		long t2=System.currentTimeMillis();
		System.out.println("query all pétition:"+(t2-t1));
		System.out.println("c modifié encire une fois");
		//return results;
		return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();
	}
	
	
	
	@ApiMethod(name = "petitionVotedByUser", httpMethod = HttpMethod.GET)
	public List<Entity> petitionVotedByUser(@Named("name") String name) {
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition")
				/*.addProjection(new PropertyProjection("Key", String.class))
				.addProjection(new PropertyProjection("Name", String.class))
				.addProjection(new PropertyProjection("Body", String.class))
				.addProjection(new PropertyProjection("nbvotants", Long.class))
				.addProjection(new PropertyProjection("createur", String.class))
				.addProjection(new PropertyProjection("jour", String.class))
				.addProjection(new PropertyProjection("heure", String.class))*/
				.setFilter(new FilterPredicate("votants", FilterOperator.EQUAL, name));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		
		return result;
	}
	
	@ApiMethod(name = "petitionByName", httpMethod = HttpMethod.GET)
	public List<Entity> petitionByName(@Named("name") String name) {
		System.out.println("debut de petitionByName avec nom = "+name);
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition")
				
				/*.addProjection(new PropertyProjection("Key", String.class))
				.addProjection(new PropertyProjection("Name", String.class))
				.addProjection(new PropertyProjection("Body", String.class))
				.addProjection(new PropertyProjection("nbvotants", Long.class))
				.addProjection(new PropertyProjection("createur", String.class))
				.addProjection(new PropertyProjection("jour", String.class))
				.addProjection(new PropertyProjection("heure", String.class))*/
				.setFilter(new FilterPredicate("Name", FilterOperator.EQUAL, name));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		System.out.println(result);
		/*if (result == null) {
			System.out.println("dans if");
			return ;
		}else {
			System.out.println("dans else");
			return result;
		}
		*/
		return result;
	}

	
	@ApiMethod(name = "myPetition", httpMethod = HttpMethod.GET)
	public List<Entity> myPetition(@Named("name") String name) throws UnauthorizedException {
		
		if (name == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
		
		System.out.println("test"+name);
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition")
				/*.addProjection(new PropertyProjection("Key", String.class))
				.addProjection(new PropertyProjection("Name", String.class))
				//.addProjection(new PropertyProjection("Body", String.class))
				.addProjection(new PropertyProjection("nbvotants", Long.class))
				.addProjection(new PropertyProjection("createur", String.class))
				//.addProjection(new PropertyProjection("jour", String.class))
				//.addProjection(new PropertyProjection("heure", String.class))*/
				
				.setFilter(new FilterPredicate("createur", FilterOperator.EQUAL, name));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(10));
		
		return result;
	}

	@ApiMethod(name = "BestPetition", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> BestPetition(@Nullable @Named("next") String cursorString) {
		System.out.println("BestPetition");
		System.out.println("sursor" + cursorString);
		long t1=System.currentTimeMillis();
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition")
				/*.addProjection(new PropertyProjection("Key", String.class))
				.addProjection(new PropertyProjection("Name", String.class))
				.addProjection(new PropertyProjection("Body", String.class))
				.addProjection(new PropertyProjection("nbvotants", Long.class))
				.addProjection(new PropertyProjection("createur", String.class))
				.addProjection(new PropertyProjection("jour", String.class))
				.addProjection(new PropertyProjection("heure", String.class))*/
				.addSort("nbvotants", SortDirection.DESCENDING);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);
		if (cursorString != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
	    }
		//List<Entity> result = pq.asQueryList(FetchOptions.Builder.withLimit(100));
		 QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
		cursorString = results.getCursor().toWebSafeString();
		
		long t2=System.currentTimeMillis();
		System.out.println("query all pétition:"+(t2-t1));
		System.out.println("c modifié encire une fois");
		//return results;
		return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();
	}
	
	@ApiMethod(name = "detailPetition", httpMethod = HttpMethod.GET)
	public Entity detailPetition(@Named("id") String id) {
		System.out.println("detail");
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition").setFilter(new FilterPredicate("Key", FilterOperator.EQUAL, id));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		System.out.println(result);
		return result;
	}
	
	@ApiMethod(name = "dejavote", httpMethod = HttpMethod.GET)
	public Entity dejavote(@Named("id") String id, @Named("mail") String mail) {
		System.out.println("id : "+ id);
		System.out.println("mail : "+ mail);
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition").setFilter(CompositeFilterOperator.and(
					new FilterPredicate("Key", FilterOperator.EQUAL, id), 
					new FilterPredicate("votants", FilterOperator.EQUAL, mail)
				));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		Entity entity = pq.asSingleEntity();
		
		System.out.println("entity : "+ entity);
		return entity;
	}
	
	
	@ApiMethod(name = "votePetition", httpMethod = HttpMethod.POST)
	public Entity votePetition(@Named("id") String id, @Named("mail") String mail) {
		System.out.println("id : "+ id);
		System.out.println("mail : "+ mail);
		//Query q = new Query("Petition").setFilter(new FilterPredicate("__key__", FilterOperator.GREATER_THAN, last.getKey())); 
		Query q = new Query("Petition").setFilter(CompositeFilterOperator.and(
					new FilterPredicate("Key", FilterOperator.EQUAL, id), 
					new FilterPredicate("votants", FilterOperator.EQUAL, mail)
				));
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		Entity entity = pq.asSingleEntity();
		
		System.out.println("entity : "+ entity);
		
		if (entity == null) {
			
			
			Query query = new Query("Petition").setFilter(new FilterPredicate("Key", FilterOperator.EQUAL, id));
			PreparedQuery prq = datastore.prepare(query);
			Entity entitymodif = prq.asSingleEntity();
			
			long nbVote = (long)entitymodif.getProperty("nbvotants");
			entitymodif.setProperty("nbvotants", nbVote + 1);
			System.out.println("entity a modifier: "+ entitymodif);
			ArrayList<String> listvotants = new ArrayList();
			if(entitymodif.getProperty("votants") != null) {
				listvotants = (ArrayList<String>)entitymodif.getProperty("votants");
			}
			listvotants.add(mail);
			System.out.println("list votants : "+ listvotants);
			entitymodif.setProperty("votants", listvotants);
			datastore.put(entitymodif);
		}
		return entity;
		
	}

}
