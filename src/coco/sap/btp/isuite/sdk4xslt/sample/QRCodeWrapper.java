package coco.sap.btp.isuite.sdk4xslt.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * QRCodeWrapper is a sample class that generates a QR PNG image encoded in 
 * base64. 
 * <p>
 * It uses Map interface to expose method get(key) that can be called from 
 * XSLT. java.util.Map is a SDK interface that can be accessed by Saxon at XSLT
 * compile time. 
 */
public class QRCodeWrapper implements Map<String, String> {
	
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
	 * Generates a PNG image encoded in base64 of a QR code for the text
	 * contained in key.
	 * 
	 * @param key	text that is used to generate the QR
	 * @return the String that contains the base64 encoded QR image
	 */
	@Override
	public String get(Object key) {
		if (key == null) {
			return null;
		}
		String base64 = null;
		int size = 125;
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			// Create the ByteMatrix for the QR-Code that encodes the given String
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix;
			byteMatrix = qrCodeWriter.encode(strValue(key), BarcodeFormat.QR_CODE, size, size, hintMap);

			// Write as PNG to outputstream and then encode with base64
			MatrixToImageWriter.writeToStream(byteMatrix, "png", os);
			base64= Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
		return base64;
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
	public String put(String key, String value) {return null;}

	@Override
	public String remove(Object key) {return null;}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {}

	@Override
	public void clear() {}

	@Override
	public Set<String> keySet() {return null;}

	@Override
	public Collection<String> values() {return null;}

	@Override
	public Set<Entry<String, String>> entrySet() {return null;}
}
