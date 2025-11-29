package coco.sap.btp.isuite.sdk4xslt;

/**
 * Auxiliary class for SDK 4 XSLT Wrappers
 */
public abstract class Sdk4XsltAbstract {
	
	/**
	 * Auxiliary function that retrieves the string value of a parameter send 
	 * by Saxon. Saxon may send a value wrapped in a class as NodeInfo when 
	 * at compile time the parameters is an Object. Because Saxon doesn't 
	 * support java generics typed parameters are handled as Object.  
	 *  
	 * @param value	Object sended by a XSLT call. Can be wrapped by a Saxon Class.
	 * @return base64 encoded QR image that correspond to value
	 */
	public static String stringValue(Object value) {
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
}
