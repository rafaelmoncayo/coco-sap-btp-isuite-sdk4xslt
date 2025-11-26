package coco.sap.btp.isuite.sdk4xslt.sample;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.WordUtils;

/**
 * SplitTextWrapper is a sample class that splits a text into lines with a 
 * specified max length.
 * <p>
 * It uses Map interface to expose method get(key) that can be called from 
 * XSLT. java.util.Map is a SDK interface that can be accessed by Saxon at XSLT
 * compile time. 
 */
public class SplitTextWrapper implements Map<Object, Object> {
	
	static int DEFAULT_MAX_LENGTH = 40;
	private int maxLength = DEFAULT_MAX_LENGTH;
	
	/**
	 * Auxiliary function that retrieves the string value of a parameter send 
	 * by Saxon. Saxon may send a value wrapped in a class as NodeInfo when 
	 * at compile time the parameters is an Object. Because Saxon doesn't 
	 * support java generics typed parameters are handled as Object.  
	 *  
	 * @param value	Object sended by a XSLT call. Can be wrapped by a Saxon Class.
	 * @return base64 encoded QR image that correspond to value
	 */
	private static String strValue(Object value) {
		if (value!=null) {
			if (value instanceof net.sf.saxon.om.Item) {
				// Recommended to convert to String. Its possible that Saxon wraps values with its classes
				return ((net.sf.saxon.om.Item)value).getStringValue();
			} else {
				return value.toString();
			}
		}
		return "";
	}

	/**
	 * Splits the text in lines no longer that specified max length.
	 *  
	 * @param key	text to be splitted in lines
	 * @return String array with the lines
	 */
	@Override
	public String[] get(Object key) {		
		String[] lines = WordUtils.wrap(strValue(key), maxLength).split("\r?\n");
		return lines;
	}

	/**
	 * If "maxlength" is used as key, then it sets its value
	 * 
	 * @param key	set "maxlength" as a fixed value
	 * @param value	max lenth of lines
	 * @return return the same value passed as second argument
	 */
	@Override
	public Object put(Object key, Object value) {
		if (key!=null && value!=null) {
			switch(strValue(key).toLowerCase()) {
			case "maxlength":
				try {
					maxLength = Integer.valueOf(strValue(value));
				} catch (NumberFormatException e) {
					// do nothing
				}
				break;
			}			
		}
		return value;			
	}

	//
	// Not implemented methods
	// 

	@Override
	public int size() {	return 0; }

	@Override
	public boolean isEmpty() { return false; }

	@Override
	public boolean containsKey(Object key) {return false;}

	@Override
	public boolean containsValue(Object value) {return false;}

	@Override
	public String[] remove(Object key) {return null;}

	@Override
	public void clear() {}

	@Override
	public Set keySet() {return null;}

	@Override
	public void putAll(Map<? extends Object, ? extends Object> m) {}

	@Override
	public Collection<Object> values() {return null;}

	@Override
	public Set<Entry<Object, Object>> entrySet() {return null;}

}
