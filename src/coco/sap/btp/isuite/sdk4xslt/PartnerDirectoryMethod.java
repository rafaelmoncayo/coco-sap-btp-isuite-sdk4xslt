package coco.sap.btp.isuite.sdk4xslt;

/**
 * Methods of the Partner Directory Service implemented by PartnerDirectoryWrapper
 */
public enum PartnerDirectoryMethod {
	/**
	 * Create binded instance with partnerId name
	 */
	INSTANCE("Instance", 1, "partnerId"),
	/**
	 * Return the partnerId using alternative partnerId
	 */
	PARTNERID("PartnerId", 3, "agency, scheme, alternativePartnerId"),
	/**
	 * Create binded instance using alternative partnerId
	 */
	PARTNERIDINSTANCE("PartnerIdInstance", 3, "agency, scheme, alternativePartnerId"),
	/**
	 * Alternative Partner Id 
	 */
	ALTERNATIVEPARTNERID("AlternativePartnerId", 2, "agency, scheme"),
	/**
	 * Return the list of authorized users
	 */
	AUTHORIZEDUSERS("AuthorizedUsers", 0, ""),
	/**
	 * Return a parameter value
	 */
	PARAMETER("Parameter", 1 , "parameterId, type (optional 'String', 'BinaryData', 'BinaryDataB64')"),
	/**
	 * Return the partnerId of the authorized user
	 */
	PARTNERIDOFAUTHORIZEDUSER("PartnerIdOfAuthorizedUser", 1, "authorizedUser"),
	/**
	 * Create binded instance using the authorized user
	 */
	PARTNERIDINSTANCEOFAUTHORIZEDUSER("PartnerIdInstanceOfAuthorizedUser", 1, "authorizedUser");
	
	private String name;
	private int requiredParameters;
	private String requiredParameterNames;
	
	PartnerDirectoryMethod(String name, int requiredParmeters, String requiredParameterNames) {
		this.name = name;
		this.requiredParameters = requiredParmeters;
		this.requiredParameterNames = requiredParameterNames;
	}
	
	/**
	 * Name of the method of Partner Directory 
	 * @return method name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Minimum number of parameters
	 * @return minimum number of parameters
	 */
	public int getRequiredParameters() {
		return requiredParameters;
	}
	
	/**
	 * Names of the parameters required for this method
	 * @return list of parameters
	 */
	public String getRequiredParameterNames() {
		return requiredParameterNames;
	}
}
