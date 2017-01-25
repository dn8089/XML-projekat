package rs.gov.parlament.propisi.sessionbeans;

import java.util.List;

import rs.gov.parlament.propisi.entities.Act;
import rs.gov.parlament.propisi.util.SearchResult;

public interface ActDaoLocal extends GenericDaoLocal<Act, String> {
	
	public List<SearchResult> search(String criteria);
	
	public void acceptAct(String docId);

}
