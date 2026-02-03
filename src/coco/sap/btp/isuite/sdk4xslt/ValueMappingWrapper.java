package coco.sap.btp.isuite.sdk4xslt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sap.it.api.ITApiFactory;
import com.sap.it.api.exception.InvalidContextException;
import com.sap.it.api.mapping.ValueMappingApi;

import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;

/**
 * ValueMappingWrapper provides access to Value Mapping of SAP Cloud 
 * Integration. This class encapsulates the ValueMappingApi in a Map interface. 
 * <p>
 * In order to call ValueMappingWrapper from Saxon EE XSLT processor do:
 * <p>
 * <strong>1. Define the prefix of namespace that reference Map interface</strong>
 * <pre>
 * {@code xmlns:map="java:java.util.Map"}
 * </pre>
 * <p>
 * <strong>2. Define the parameter COCO_ValueMapping. </strong> Script "COCO_AddJavaSDKExtensionsForXSLT.groovy" of Script Collection "COCO Cloud Integration SDK extensions for XSLT" sets this property in the iflow. So call this groovy script before your XSLT.   
 * <pre>
 * {@code <xsl:param name="COCO_ValueMapping" />}
 * </pre>
 * <strong>3. Create a binded instance of ValueMappingWrapper (optional).</strong>
 * <pre>
 * {@code <xsl:variable name="vmUOM" select="map:get($COCO_ValueMapping, ('EDIFACT', 'UOM_CODE', 'COCO', 'UOM_TEXT'))" />}
 * </pre>
 * <strong>4. Do value mapping with unbinded ValueMappingWrapper or with a binded one</strong>
 * <pre>
 * {@code <xsl:value-of select="map:get($COCO_ValueMapping, ('EDIFACT', 'UOM_CODE', UnitOfMeasure, 'COCO', 'UOM_TEXT'))" />}
 * {@code <xsl:value-of select="map:get($vmUOM, UnitOfMeasure)" />}
 * </pre>
 * 
 */
public class ValueMappingWrapper extends Sdk4XsltAbstract implements Map<Object,Object> {
	
	private static String TOSTRING_BINDED = "ValueMappingWrapper {%s, %s, %s, %s}";
	private static String TOSTRING_NOBINDED = "ValueMappingWrapper (no binded to  source|target-agencie|identifier)";
	private static String ERROR_MSG_NOTBINDED_SEQ1 = 
		"ERROR: This instance of ValueMappingWrapper is not binded to "
		+ "source|target-agencie|identifier. 5 values are needed to do value "
		+ "mapping or 4 values to create a binded instance of "
		+ "ValueMappingWrapper."; 
	private static String ERROR_MSG_NOTBINDED = 
		"ERROR: This instance of ValueMappingWrapper is not binded to "
		+ "source|target-agencie|identifier. Please call the method with an "
		+ "XSLT sequence with 4 values to create a binded instance of "
		+ "ValueMappingWrapper or 5 values to do value mapping. The received "
		+ "object class was %s"; 
	private static String ERROR_MSG_WRONGNUMBEROFPARAMS = 
		"ERROR: Wrong number of values in sequence (%d). Please, pass a XSLT sequence with 4 or 5 values";
	
	private ValueMappingApi mappingApi;
	private String srcAgency;
	private String srcIdentifier;
	private String trgAgency;
	private String trgIdentifier;
			
	/**
	 * Creates an instance of ValueMappingWrapper without binding it to 
	 * source/target agency/identifiers
	 */
	public ValueMappingWrapper() {
		super();
		try {
			mappingApi = ITApiFactory.getService(ValueMappingApi.class, null);
		} catch (InvalidContextException e) {
			throw new RuntimeException(e);
		}		
	}

	/**
	 * Creates an instance of ValueMappingWrapper binded to 
	 * (source|target) (agency|identifier)
	 * 
	 * @param srcAgency		source agency
	 * @param srcIdentifier	source identifier (schema)
	 * @param trgAgency		target agency
	 * @param trgIdentifier	target identifier (schema)
	 */
	public ValueMappingWrapper(String srcAgency, String srcIdentifier, String trgAgency, String trgIdentifier) {
		super();
		this.srcAgency = srcAgency;
		this.srcIdentifier = srcIdentifier;
		this.trgAgency = trgAgency;
		this.trgIdentifier = trgIdentifier;
		try {
			mappingApi = ITApiFactory.getService(ValueMappingApi.class, null);
		} catch (InvalidContextException e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * @return true if all (source|target) (agency|identifier) properties are 
	 * set
	 */
	private boolean hasAgenciesAndIdentifiers() {
		return srcAgency!=null && srcIdentifier!=null && trgAgency!=null && trgIdentifier!=null;
	}

	/**
	 * @return String representation of this instance
	 */
	@Override
	public String toString() {
		if (hasAgenciesAndIdentifiers()) {
			return String.format(TOSTRING_BINDED, srcAgency, srcIdentifier, trgAgency, trgIdentifier);			
		} else {
			return String.format(TOSTRING_NOBINDED);						
		}
	}

	/**
	 * Do value mapping of creates a new binded instance of ValueMappingWrapper.
	 * <p>
	 * If this instance is <strong>not binded</strong> to source|target-agency|identifier
	 * then depending on the number of values passed the behavior will be:
	 * <ul>
	 *  <li><strong>Case 4 values passed</strong>: returns a new binded instance of 
	 *      ValueMappingWrapper the four values will represent: sourceAgency, 
	 *      sourceIdentifier, targetAgency, targetIdentifier. e.g.
	 *      <pre>
	 *      {@code <xsl:variable name="vmUOM" select="map:get($COCO_ValueMapping, ('EDIFACT', 'UOM_CODE', 'COCO', 'UOM_TEXT'))" />" />}
	 *      returns a binded instance of ValueMappingWrapper.
	 *      </pre>
	 *  </li> 
	 *  <li><strong>Case 5 values passed</strong>: Returns the value mapping result for values:
	 *      sourceAgency, sourceIdentifier, sourceValue, targetAgency, 
	 *      targetIdentifier. e.g.
	 *      <pre>
	 *      {@code <xsl:value-of select="map:get($COCO_ValueMapping, ('EDIFACT', 'UOM_CODE', UnitOfMeasure, 'COCO', 'UOM_TEXT'))" />}
	 *      returns "piece" if UnitOfMeasure equals "PCE"
	 *      </pre>
	 *  </li> 
	 *  <li><strong>Other number of values passed</strong>: Returns an error message.</li>
	 * </ul>
	 * <p>
	 * If this instance is binded. Then it will return the mapped value of key. If key is a sequence, then it will return a sequence of each value contained in the sequence.
	 * <ul>
	 *  <li><strong>Case one value passed</strong>: returns the mapped value of key. e.g.
	 *      <pre>
	 *      {@code <xsl:value-of select="map:get($vmUOM, UnitOfMeasure)" />}
	 *      returns "piece" if UnitOfMeasure equals "PCE"
	 *      </pre>
	 *  </li>
	 *  <li><strong>A sequence of values passed</strong>: For every source value in sequence return the mapped value 
	 *      <pre>
	 *      {@code <xsl:value-of select="map:get($vmUOM, ('PCE', 'KGM', 'BX'))" separator="," />" />}
	 *      returns "piece,kg,box"
	 *      </pre>
	 *  </li>
	 *  </ul>
	 *   	 */
	@Override
	public Object get(Object key) {
		Object value = null;
		if (key != null) {
			if (key instanceof Sequence) {
				Sequence seq = (Sequence)key;
				ArrayList<String> args = new ArrayList<String>();
				SequenceIterator i = seq.iterate();
				Object p;
				while ((p = i.next()) != null) {
					args.add(stringValue(p));
				}
				
				if (hasAgenciesAndIdentifiers()) {
					// This instance is already binded to source and target agencies and identifiers
					if (args.size()==1) {
						value = mappingApi.getMappedValue(
								srcAgency, 
								srcIdentifier, 
								stringValue(args.get(0)), 
								trgAgency, 
								trgIdentifier);
					} else if (args.size() > 1) {
						ArrayList<String> results = new ArrayList<String>(args.size());
						for (String arg: args) {
							results.add(mappingApi.getMappedValue(srcAgency, 
									srcIdentifier, 
									stringValue(arg), 
									trgAgency, 
									trgIdentifier));
						}
						value = results;
					} else {
						value = String.format(ERROR_MSG_WRONGNUMBEROFPARAMS, args.size());
					}
				} else {
					switch (args.size()) {
					case 4:
						// Create an instance of ValueMappingWrapper with schemas and identifiers
						value = new ValueMappingWrapper(args.get(0), args.get(1), args.get(2), args.get(3));
						break;
					case 5:
						// Do value mapping
						value = mappingApi.getMappedValue(
								args.get(0),  // source agency 
								args.get(1),  // source identifier
								args.get(2),  // source value
								args.get(3),  // target agency
								args.get(4)); // target identifier 
						break;
					default:
						value = String.format(ERROR_MSG_WRONGNUMBEROFPARAMS, args.size());				
					}					
				}
			} else if (hasAgenciesAndIdentifiers()) {
				value = mappingApi.getMappedValue(srcAgency, srcIdentifier, stringValue(key), trgAgency, trgIdentifier);
			} else {
				value = String.format(ERROR_MSG_NOTBINDED, key.getClass().getName());				
			}			
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
