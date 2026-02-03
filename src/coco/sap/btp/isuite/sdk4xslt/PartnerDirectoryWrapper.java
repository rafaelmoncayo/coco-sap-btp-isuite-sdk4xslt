package coco.sap.btp.isuite.sdk4xslt;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sap.it.api.ITApiFactory;
import com.sap.it.api.exception.InvalidContextException;
import com.sap.it.api.pd.BinaryData;
import com.sap.it.api.pd.PartnerDirectoryService;
import com.sap.it.api.pd.exception.PartnerDirectoryException;

import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;

/**
 * PartnerDirectoryWrapper provides access to the <strong>Partner Directory
 * </strong> of SAP Cloud Integration. This class encapsulates the 
 * PartnerDirectoryService in a Map interface. 
 * <p>
 * In order to call PartnerDirectoryWrapper from Saxon EE XSLT processor do:
 * <p>
 * <strong>1. Define the prefix of namespace that reference Map interface</strong>
 * <pre>
 * {@code xmlns:map="java:java.util.Map"}
 * </pre>
 * <p>
 * <strong>2. Define the parameter COCO_PartnerDirectory. </strong> Script 
 * "COCO_AddJavaSDKExtensionsForXSLT.groovy" of Script Collection "COCO Cloud 
 * Integration SDK extensions for XSLT" sets this property in the iflow. So 
 * call this groovy script before your XSLT.   
 * <pre>
 * {@code <xsl:param name="COCO_PartnerDirectory" />}
 * </pre>
 * <strong>3. Create a binded instance of PartnerDirectoryWrapper (optional).</strong>
 * <pre>
 * {@code <xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, 'S4H400')" />}</pre>
 * or create instance using alternative partnerId 
 * <pre>
 * {@code <xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, ('PartnerIdInstance', 'Landscape', 'S4HANA', 'DEV')) />"}</pre>
 * or create instance by authorized user 
 * <pre>
 * {@code <xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, ('PartnerIdInstanceOfAuthorizedUser', 'user1'))" />}</pre>
 * <strong>4. Call Partner Directory methods </strong>. The first parameter should the name of the method to call: PartnerId, PartnerIdInstance, AlternativePartnerId, AuthorizedUsers, Parameter, PartnerIdOfAuthorizedUser, PartnerIdInstanceOfAuthorizedUser
 * <ul>
 *  <li>Create instanced variables
 *   <pre>
 *    {@code <xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, 'S4H400')" />}
 *    {@code <xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, ('PartnerIdInstance', 'Landscape', 'S4HANA', 'DEV'))" />}
 *    {@code <xsl:variable name="pdS4" select="map:get($COCO_PartnerDirectory, ('PartnerIdInstanceOfAuthorizedUser', 'user1'))" />}</pre>
 *  </li>
 *  <li>Get a string parameter
 *   <pre>
 *    {@code <xsl:value-of select="map:get($COCO_PartnerDirectory, ('Parameter', 'S4H400', 'hostname'))" />}
 *    {@code <xsl:value-of select="map:get(map:get($pdS4, ('Parameter', 'hostname'))" />}</pre>
 *   sample result: <code>s4h.somedomain.corp</code>
 *  </li>
 *  <li>Get a binary parameter
 *   <pre>
 *    {@code <xsl:value-of select="map:get($COCO_PartnerDirectory, ('Parameter', 'S4H400', 'SSL_Cert', 'BINARYDATA'))" />}
 *    {@code <xsl:value-of select="map:get($pdS4, ('Parameter', 'SSL_Cert', 'BINARYDATA'))" />}</pre>
 *   sample result (octect sequence): <code>45 45 45 45 45 66 69 71 73</code>...
 *   <pre>
 *    {@code <xsl:value-of select="map:get($COCO_PartnerDirectory, ('Parameter', 'S4H400', 'SSL_Cert', 'BINARYDATAB64'))" />}
 *    {@code <xsl:value-of select="map:get($pdS4, ('Parameter', 'SSL_Cert', 'BINARYDATAB64'))" />}</pre>
 *   sample result (base64): <code>LS0tLS1CRUdJ</code>...
 *  </li>
 *  <li>Get Alternative Partner Id
 *   <pre>
 *    {@code <xsl:value-of select="map:get($COCO_PartnerDirectory, ('AlternativePartnerId', 'S4H400', 'Landscape', 'S4HANA'))" />}
 *    {@code <xsl:value-of select="map:get($pdS4, ('AlternativePartnerId', 'Landscape', 'S4HANA'))" />}</pre>
 *   sample result: <code>s4h.somedomain.corp</code>
 *  </li>
 *  <li>Get Authorized Users
 *   <pre>
 *    {@code <xsl:value-of select="map:get($COCO_PartnerDirectory, ('AuthorizedUsers', 'S4H400'))" />}
 *    {@code <xsl:value-of select="map:get($pdS4, ('AuthorizedUsers'))" />}</pre>
 *   sample result: <code>payrollextension user1 user2</code>
 *  </li>
 *  <li>Get PartnerId from Authorized Users
 *   <pre>
 *    {@code <xsl:value-of select="map:get($COCO_PartnerDirectory, ('PartnerIdOfAuthorizedUser', 'user1'))" />}</pre>
 *   sample result: <code>S4H400</code>
 *  </li>
 * </ul>
 * 
 */
public class PartnerDirectoryWrapper extends Sdk4XsltAbstract implements Map<Object,Object> {
	private static final String TOSTRING_BINDED = "PartnerDirectoryWrapper (%s)";
	private static final String TOSTRING_NOTBINDED = "PartnerDirectoryWrapper (no binded to partnerId)";

	private static String ERROR_MSG_NULL_PARAMETER = "ERROR: Please, pass arguments a XSLT sequence. Received null value";
	private static String ERROR_MSG_NOT_A_SEQUENCE = "ERROR: Please, pass arguments a XSLT sequence. Type received is %s";
	private static String ERROR_MSG_EMTPY_SEQUENCE = "ERROR: Empty sequence received"; 
	private static String ERROR_MSG_NO_SUCH_METHOD = "ERROR: No method '%s' exists. Use one of the followings: 'PartnerId', 'PartnerIdInstance', 'AlternativePartnerId', 'AuthorizedUsers', 'Parameter', 'PartnerIdOfAuthorizedUser' or 'PartnerIdInstanceOfAuthorizedUser'"; 
	private static String ERROR_MSG_WRONG_NUMBER_OF_PARAMS = 
			"ERROR: Wrong number of parameters for method '%1$s'. Add the following values after '%1$s': %2$s";
	private static String ERROR_MSG_EXCEPTION = "Exception calling method '%s' of PartnerDirectoryService: %s";

	private PartnerDirectoryService pdService = null;
	private String partnerId = null;
	
	/**
	 * Constructs a not binded instance of PartnerDirectoryWrapper
	 */
	public PartnerDirectoryWrapper() {
		super();
		try {
			pdService = ITApiFactory.getService(PartnerDirectoryService.class, null);
		} catch (InvalidContextException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates a binded instance of PartnerDirectoryWrapper
	 * @param partnerId partnerId name
	 */
	public PartnerDirectoryWrapper(String partnerId) {
		super();
		try {
			pdService = ITApiFactory.getService(PartnerDirectoryService.class, null);
		} catch (InvalidContextException e) {
			throw new RuntimeException(e);
		}
		this.partnerId = partnerId;
	}
	
	/**
	 * @return String representation of this instance
	 */
	@Override
	public String toString() {
		return String.format(partnerId == null ? TOSTRING_NOTBINDED : TOSTRING_BINDED, partnerId);			
	}
	
	/**
	 * Calls the methods of the Partner Directory Service and return its value
	 */
	@Override
	public Object get(Object key) {
		Object value = null;
		try {
			if (key == null) {
				value = String.format(ERROR_MSG_NULL_PARAMETER);
				return value;
			}
			
			ArrayList<String> args = new ArrayList<String>();

			if (key instanceof String) {
				args.add(PartnerDirectoryMethod.INSTANCE.getName());
				args.add((String)key);
			} else if (key instanceof Sequence) {
				// put items of sequence in an ArrayList 
				SequenceIterator i = ((Sequence)key).iterate();
				Object p;
				while ((p = i.next()) != null) {
					args.add(stringValue(p));
				}				
			} else {
				value = String.format(ERROR_MSG_NOT_A_SEQUENCE, key.getClass().getTypeName());
				return value;				
			}
						
			if (args.size()==0) {
				value = String.format(ERROR_MSG_EMTPY_SEQUENCE);
				return value;
			} 
			
			// get method enum
			PartnerDirectoryMethod method = null;
			try {
				method = PartnerDirectoryMethod.valueOf(args.get(0).toUpperCase());			
			} catch (IllegalArgumentException e) {
				value = String.format(ERROR_MSG_NO_SUCH_METHOD, args.get(0));
				return value;
			}

			// validate required number of parameters
			if (method.getRequiredParameters() > (args.size() - (partnerId==null ? 1 : 0))) {
				String parameterNames = method.getRequiredParameterNames();
				if (partnerId == null) {
					parameterNames = "partnerId, " + parameterNames;
				}
				// value = "Wrong number of parameters: " + method.getName() + ", " + parameterNames;
				value = String.format(ERROR_MSG_WRONG_NUMBER_OF_PARAMS, method.getName(), parameterNames);
				return value;
			}
			
			// call partner directory method
			value = callPDMethod(method, args);
		} catch (Exception e) {
			value = String.format(ERROR_MSG_EXCEPTION, e.getClass().getName(), e.getMessage());
		}
		
		
		return value;

	}
	
	private Object callPDMethod(PartnerDirectoryMethod method, ArrayList<String> args) {
		Object value = null;
		int parameterOffset = partnerId != null ? 0 : 1;
		try {
			switch(method) {
			case INSTANCE: {
				String pid = args.get(1);
				PartnerDirectoryWrapper bindedPD = new PartnerDirectoryWrapper(pid);
				value = bindedPD;	
				break;
			}
			case PARTNERID: {
				String agency = args.get(1);
				String scheme = args.get(2);
				String alternativeParameterId = args.get(3);
				value = pdService.getPartnerId(agency, scheme, alternativeParameterId);
				break;
			}
			case PARTNERIDINSTANCE: {
				String agency = args.get(1);
				String scheme = args.get(2);
				String alternativeParameterId = args.get(3);
				String pid = pdService.getPartnerId(agency, scheme, alternativeParameterId);
				PartnerDirectoryWrapper bindedPD = new PartnerDirectoryWrapper(pid);
				value = bindedPD;
				break;
			}
			case ALTERNATIVEPARTNERID: {
				String pid = partnerId != null ? this.partnerId : args.get(1);
				String agency = args.get(1 + parameterOffset);
				String scheme = args.get(2 + parameterOffset);				
				value = pdService.getAlternativePartnerId(agency, scheme, pid);
				break;
			}
			case AUTHORIZEDUSERS: {
				String pid = partnerId != null ? this.partnerId : args.get(1);
				value = pdService.getAuthorizedUsers(pid);				
				break;
			}
			case PARAMETER: {
				String pid = partnerId != null ? this.partnerId : args.get(1);
				String parameterId = args.get(1 + parameterOffset);
				String contentType = "STRING";
				Class<?> type = String.class;
				
				if (args.size() > 2 + parameterOffset) {
					contentType = args.get(2 + parameterOffset).toUpperCase();
				}
				if ("BINARYDATA".compareTo(contentType) == 0
					|| "BINARYDATAB64".compareTo(contentType) == 0) 
				{
					type = BinaryData.class;
				}
				
				// Get value
				value = pdService.getParameter(parameterId, pid, type);
				
				if (value instanceof BinaryData) {
					value = ((BinaryData)value).getData();
					if ("BINARYDATAB64".compareTo(contentType) == 0) {
						value = Base64.getEncoder().encodeToString((byte[])value);
					}
				}
				break;
			}
			case PARTNERIDOFAUTHORIZEDUSER: {
				String authorizedUser = args.get(1);
				value = pdService.getPartnerIdOfAuthorizedUser(authorizedUser);
				break;
			}
			case PARTNERIDINSTANCEOFAUTHORIZEDUSER: {
				String authorizedUser = args.get(1);
				String pid = pdService.getPartnerIdOfAuthorizedUser(authorizedUser);
				PartnerDirectoryWrapper bindedPD = new PartnerDirectoryWrapper(pid);
				value = bindedPD;
				break;				
			}
			default:
				value = String.format(ERROR_MSG_NO_SUCH_METHOD, args.get(0));
			}			
		} catch (PartnerDirectoryException e) {
			value = String.format(ERROR_MSG_EXCEPTION, method.getName(), e.getMessage());
		}
		
		return value;

	}
	
	//
	// Not implemented methods
	// 


	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public int size() {
		return 0;
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public Object put(Object key, Object value) {
		return null;
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public String remove(Object key) {
		return null;
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public void putAll(Map<? extends Object, ? extends Object> m) {
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public void clear() {
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public Set<Object> keySet() {
		return null;
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public Collection<Object> values() {
		return null;
	}

	/**
	 * Dummy implementation. Avoid calling it
	 */
	@Override
	public Set<Entry<Object, Object>> entrySet() {
		return null;
	}	


}
