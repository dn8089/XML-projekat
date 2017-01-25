package rs.gov.parlament.propisi.sessionbeans;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import rs.gov.parlament.propisi.util.SearchResult;

public interface GenericDaoLocal<T, ID extends Serializable> {
	
	public T findById(String docId) throws JAXBException;
	
	public T persist(T entity) throws JAXBException, IOException, SAXException;
	
	public void delete(String docId);
	
	public List<SearchResult> findByCollection(String collId);

}
