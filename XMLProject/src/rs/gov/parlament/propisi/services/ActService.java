package rs.gov.parlament.propisi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import rs.gov.parlament.propisi.entities.Act;
import rs.gov.parlament.propisi.sessionbeans.ActDaoLocal;
import rs.gov.parlament.propisi.util.SearchResult;

@Path("/act")
public class ActService {
	
	@EJB
	private ActDaoLocal actDao;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Act findById(@QueryParam("docId") String docId) {
		System.out.println("service findById");
		Act retVal = null;
		
		try {
			retVal = actDao.findById(docId);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Act create(Act a) {
		System.out.println("service createAct");
		Act retVal = null;
		
		try {
			retVal = actDao.persist(a);
		} catch (JAXBException | IOException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	@DELETE
	@Produces(MediaType.TEXT_HTML)
	public String delete(@QueryParam("docId") String docId) {
		System.out.println("service delete");
		actDao.delete(docId);
		return "ok";
	}
	
	@GET
	@Path("/search/{criteria}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SearchResult> search(@PathParam("criteria") String criteria) {
		System.out.println("search service");
		List<SearchResult> retVal = null;
		retVal = actDao.search(criteria);
		return retVal;
	}
	
	@GET
	@Path("/collection")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SearchResult> findByCollection(@QueryParam("collId") String collId) {
		System.out.println("findByCollection service");
		List<SearchResult> retVal = null;
		retVal = actDao.findByCollection(collId);
		return retVal;
	}
	
	@PUT
	@Path("/acceptAct")
	@Produces(MediaType.TEXT_HTML)
	public String acceptAct(@QueryParam("docId") String docId) {
		actDao.acceptAct(docId);
		return "ok";
	}

}
