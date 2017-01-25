package rs.gov.parlament.propisi.sessionbeans;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import rs.gov.parlament.propisi.util.SearchResult;
import rs.gov.parlament.propisi.xmldb.EntityManagerMarkLogic;

public class GenericDao<T, ID extends Serializable> implements GenericDaoLocal<T, ID> {
	
	protected EntityManagerMarkLogic<T, ID> em;
	
	public GenericDao() {
		try {
			em = new EntityManagerMarkLogic<T, ID>();
		} catch (IOException | JAXBException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public T findById(String docId) throws JAXBException {
		T entity = null;
		entity = em.findById(docId);
		return entity;
	}
	
	public T persist(T entity) throws JAXBException, IOException, SAXException {
		em.persist(entity);
		return entity;
	}
	
	@Override
	public void delete(String docId) {
		em.delete(docId);
	}

	@Override
	public List<SearchResult> findByCollection(String collId) {
		List<SearchResult> retVal = null;
		retVal = em.findByCollection(collId);
		return retVal;
	}	

}
