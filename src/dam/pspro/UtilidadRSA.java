package dam.pspro;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class UtilidadRSA {
	
	protected static final String ALG = "RSA";
	
	public UtilidadRSA() {
		inicializar();
	}
	
	public static void inicializar() {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static KeyPair generarKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALG);
		keygen.initialize(1024);
		KeyPair clave = keygen.generateKeyPair();
		
		return clave;
	}
	
	public static byte[] encriptar(byte[] data, PublicKey clavePublica) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] criptograma = null;
		
		Cipher cipher = Cipher.getInstance(ALG);
		cipher.init(Cipher.ENCRYPT_MODE, clavePublica);
		criptograma = cipher.doFinal(data);
		
		return criptograma;
		
	}
	
	public static String encriptar(String data, PublicKey clavePublica) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		String criptograma = null;		
		byte[] criptogramaEnBytes = null;
		
		criptogramaEnBytes = encriptar(data.getBytes("utf8"), clavePublica);
		criptograma = Base64.getEncoder().encodeToString(criptogramaEnBytes);
				
		return criptograma;
		
	}
	
	public static byte[] desencriptar(byte[] criptograma, PrivateKey clavePrivada) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] data = null;
		
		Cipher cipher = Cipher.getInstance(ALG);
		cipher.init(Cipher.DECRYPT_MODE, clavePrivada);
		data = cipher.doFinal(criptograma);
		
		return data;		
	}
	
	public static String keyToString(Key clave) {
		byte[] claveBytes = clave.getEncoded();	
		return Base64.getEncoder().encodeToString(claveBytes);
	}
	
	public static PublicKey getClavePublicaDesdeString(String clave) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(ALG);
		EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(clave));
		
		return keyFactory.generatePublic(keySpec);
	}
}
