package rs.gov.parlament.propisi.util;

public class SearchResult {
	private String docUri;
	private String docTitle;
	
	public SearchResult(String uri, String title) {
		docUri = uri;
		docTitle = title;
	}

	public String getDocUri() {
		return docUri;
	}
	
	public void setDocUri(String docUri) {
		this.docUri = docUri;
	}
	
	public String getDocTitle() {
		return docTitle;
	}
	
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Uri dokumenta: " + docUri + ", naslov: " + docTitle;
	}
}
