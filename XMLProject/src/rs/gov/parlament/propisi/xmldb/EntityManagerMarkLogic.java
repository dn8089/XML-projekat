package rs.gov.parlament.propisi.xmldb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.ExtractedItem;
import com.marklogic.client.query.ExtractedResult;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

import rs.gov.parlament.propisi.entities.Act;
import rs.gov.parlament.propisi.util.MyValidationEventHandler;
import rs.gov.parlament.propisi.util.SearchResult;
import rs.gov.parlament.propisi.util.Util;
import rs.gov.parlament.propisi.util.Util.ConnectionProperties;

public class EntityManagerMarkLogic<T, ID extends Serializable> {
	
	private static DatabaseClient client;
	private ConnectionProperties props;
	private XMLDocumentManager xmlManager;
	private JAXBContext context;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	
	public EntityManagerMarkLogic() throws IOException, JAXBException, SAXException {
		props = Util.loadProperties();
		
		// Definise se JAXB kontekst (putanja do paketa sa JAXB bean-ovima)
		context = JAXBContext.newInstance("rs.gov.parlament.propisi.entities");
		// Marshaller je objekat zaduzen za konverziju iz objektnog u XML model
		marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		// Unmarshaller je objekat zaduzen za konverziju iz XML-a u objektni model
		unmarshaller = context.createUnmarshaller();
		
		// XML schema validacija
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new File(System.getProperty("catalina.base")+"/webapps/XMLProject/act.xsd"));
		// Podešavanje marshaller-a za XML schema validaciju
		marshaller.setSchema(schema);
		marshaller.setEventHandler(new MyValidationEventHandler());
		// Podešavanje unmarshaller-a za XML schema validaciju
		unmarshaller.setSchema(schema);
		unmarshaller.setEventHandler(new MyValidationEventHandler());
	}
	
	public T findById(String docId) throws JAXBException {
		T entity = null;
		
		System.out.println("[INFO] Using \"" + props.database + "\" database.");
		client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		
		// Create a document manager to work with XML files.
		xmlManager = client.newXMLDocumentManager();
		
		// A handle to receive the document's content.
		DOMHandle content = new DOMHandle();
		
		// Read the document from the database
		System.out.println("[INFO] Reading document from \"" + props.database + "\" database.");
		xmlManager.read(docId, content);
		// Release the client
		client.release();
				
		// Retrieving a document node form DOM handle.
		Document doc = content.get();
				        
		unmarshaller.setEventHandler(new MyValidationEventHandler());
		
		entity = (T) unmarshaller.unmarshal(doc);				
		System.out.println("[INFO] End.");
		
		return entity;
	}
	
	public void persist(T entity) throws JAXBException, IOException, SAXException {
		File f = new File(System.getProperty("catalina.base")+"/webapps/XMLProject/act.xml");
		FileOutputStream out = new FileOutputStream(f);
		
		marshaller.marshal(entity, out);
		
		System.out.println("[INFO] Using \"" + props.database + "\" database.");
		client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		
		// Create a document manager to work with XML files.
		xmlManager = client.newXMLDocumentManager();
		
		// Insert a document with generated URI (specifying the suffix and prefix)
		DocumentUriTemplate template = xmlManager.newDocumentUriTemplate("xml");
		template.setDirectory("/acts/decisions/");
		
		// Create an input stream handle to hold XML content.
		InputStreamHandle handle = new InputStreamHandle(new FileInputStream(System.getProperty("catalina.base")+"/webapps/XMLProject/act.xml"));
		
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().add("/parliament/acts/proposed");
		
		// Write the document to the database
		System.out.println("[INFO] Inserting document to \"" + props.database + "\" database.");
		DocumentDescriptor desc = xmlManager.create(template, metadata, handle);
		
		System.out.print("[INFO] Verify the content at: ");
		System.out.println("http://" + props.host + ":8000/v1/documents?database=" + props.database + "&uri=" + desc.getUri());
		
		// Release the client
		client.release();
		
		System.out.println("[INFO] End.");
	}
	
	public void delete(String docId) {
		System.out.println("[INFO] Using \"" + props.database + "\" database.");
		client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		
		// Create a document manager to work with XML files.
		xmlManager = client.newXMLDocumentManager();
		
		// Document deletion
		System.out.println("[INFO] Removing \"" + docId + "\" from \"" + props.database + "\" database.");
		xmlManager.delete(docId);
		
		// Release the client
		client.release();
		
		System.out.println("[INFO] End.");
	}
	
	public List<SearchResult> search(String criteria) {
		List<SearchResult> resultList = new ArrayList<SearchResult>();
		
		System.out.println("[INFO] Using \"" + props.database + "\" database.");
		client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		
		// Initialize query manager
		QueryManager queryManager = client.newQueryManager();
		
		// Query definition is used to specify Google-style query string
		StringQueryDefinition queryDefinition = queryManager.newStringDefinition();
		
		// Set the criteria
		queryDefinition.setCriteria(criteria);
		
		// Perform search
		SearchHandle results = queryManager.search(queryDefinition, new SearchHandle());
		
		// Serialize search results to the standard output
		MatchDocumentSummary matches[] = results.getMatchResults();
		System.out.println("[INFO] Showing the results for: " + criteria + "\n");

		MatchDocumentSummary result;
		
		for (int i = 0; i < matches.length; i++) {
			result = matches[i];
			
			String title = getDocumentTitle(result.getUri());
			resultList.add(new SearchResult(result.getUri(), title));
			
			
			System.out.println((i+1) + ". RESULT DETAILS: ");
			System.out.println("Result URI: " + result.getUri());
			System.out.println("Document title: " + title);
			System.out.println();
		}
		
		// Release the client
		client.release();
		
		System.out.println("[INFO] End.");
		
		return resultList;
	}
	
	public List<SearchResult> findByCollection(String collId) {
		List<SearchResult> resultList = new ArrayList<SearchResult>();
		
		System.out.println("[INFO] Using \"" + props.database + "\" database.");
		client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		
		// Initialize query manager
		QueryManager queryManager = client.newQueryManager();
		
		// Query definition is used to specify Google-style query string
		StringQueryDefinition queryDefinition = queryManager.newStringDefinition();
		
		// Set the criteria
		queryDefinition.setCollections(collId);
		
		// Perform search
		SearchHandle results = queryManager.search(queryDefinition, new SearchHandle());
		
		// Serialize search results to the standard output
		MatchDocumentSummary matches[] = results.getMatchResults();
		System.out.println("[INFO] Showing the results for collection: " + collId + "\n");

		MatchDocumentSummary result;
		
		for (int i = 0; i < matches.length; i++) {
			result = matches[i];
			
			String title = getDocumentTitle(result.getUri());
			resultList.add(new SearchResult(result.getUri(), title));
			
			System.out.println((i+1) + ". RESULT DETAILS: ");
			System.out.println("Result URI: " + result.getUri());
			System.out.println("Document title: " + title);
			System.out.println();
		}
		
		// Release the client
		client.release();
		
		System.out.println("[INFO] End.");
		
		return resultList;
	}
	
	public void acceptAct(String docId) {
		System.out.println("[INFO] Using \"" + props.database + "\" database.");
		client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		
		// Create a document manager to work with XML files.
		xmlManager = client.newXMLDocumentManager();
		
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		
		// Read the document from the database
		System.out.println("[INFO] Reading document from \"" + props.database + "\" database.");
		xmlManager.readMetadata(docId, metadata);
		
		DocumentCollections collections = metadata.getCollections();
		collections.remove("/parliament/acts/proposed");
		collections.add("/parliament/acts/accepted");
		
		// Write the document to the database
		System.out.println("[INFO] Inserting document to \"" + props.database + "\" database.");
		xmlManager.writeMetadata(docId, metadata);
		
		// Release the client
		client.release();
		
		System.out.println("[INFO] End.");
	}
	
	public String getDocumentTitle(String docId) {
		String title = "";
		// Create a document manager to work with XML files.
		xmlManager = client.newXMLDocumentManager();
		
		// A handle to receive the document's content.
		DOMHandle content = new DOMHandle();
		
		// Read the document from the database
		//System.out.println("[INFO] Reading document from \"" + props.database + "\" database.");
		xmlManager.read(docId, content);
				
		// Retrieving a document node form DOM handle.
		Document doc = content.get();
		
		Element act = doc.getDocumentElement();
		NodeList titles = act.getElementsByTagName("ns1:Title");
		
		if (titles.getLength() !=0) {
			title = titles.item(0).getTextContent();
		}
		
		return title;
	}

}
