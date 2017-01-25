package rs.gov.parlament.propisi.sessionbeans;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.print.DocFlavor.STRING;

import rs.gov.parlament.propisi.entities.Act;
import rs.gov.parlament.propisi.util.SearchResult;

@Stateless
@Local(ActDaoLocal.class)
public class ActDao extends GenericDao<Act, String> implements ActDaoLocal {

	public ActDao() {
		super();
	}

	@Override
	public List<SearchResult> search(String criteria) {
		List<SearchResult> retVal = null;
		retVal = em.search(criteria);
		return retVal;
	}

	@Override
	public void acceptAct(String docId) {
		em.acceptAct(docId);
	}

}
