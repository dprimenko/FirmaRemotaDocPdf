package dam.pspro;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class UtilidadAES {
	
	protected static final String ALG = "AES";
	
	public static SecretKey generarClaveAES(int longitud) throws NoSuchAlgorithmException {
		
		SecretKey clave;
		
		KeyGenerator keygen = KeyGenerator.getInstance(ALG);
		keygen.init(longitud);
		
		clave = keygen.generateKey();
		return clave;				
	}
	
	public static String encriptar(String mensaje, SecretKey clave) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cifrador   = Cipher.getInstance(ALG);
		cifrador.init(Cipher.ENCRYPT_MODE, clave);
		byte[] encVal = cifrador.doFinal(mensaje.getBytes("UTF8")); 
		byte[] criptogramaEnBytes = Base64.getEncoder().encode(encVal);
		return new String(criptogramaEnBytes);
	}
	
	public static String desencriptar(String criptograma, SecretKey clave) throws Exception{
		Cipher cifrador   = Cipher.getInstance(ALG);
		cifrador.init(Cipher.DECRYPT_MODE, clave);
		byte[] decValue = Base64.getDecoder().decode(criptograma.getBytes("UTF8"));
		byte[] decryptedVal = cifrador.doFinal(decValue); 
		
		return new String(decryptedVal);
	}
	
	public static byte[] encriptarBytes(byte[] datos, SecretKey clave) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cifrador   = Cipher.getInstance(ALG);
		cifrador.init(Cipher.ENCRYPT_MODE, clave);
		byte[] encVal = cifrador.doFinal(datos); 
		byte[] criptogramaEnBytes = Base64.getEncoder().encode(encVal);
		
		return criptogramaEnBytes;
	}
	
	public static byte[] desencriptarBytes(byte[] datos, SecretKey clave) throws Exception{
		Cipher cifrador   = Cipher.getInstance(ALG);
		cifrador.init(Cipher.DECRYPT_MODE, clave);
		byte[] decValue = Base64.getDecoder().decode(datos);
		byte[] decryptedVal = cifrador.doFinal(decValue); 
		
		return decryptedVal;
	}
}
