package dam.pspro;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

public class UtilidadFirma {
	
	protected static final String CERTIFICADO = "RUTA DEL CERTIFICADO FNMT";
	protected static final String PASSWORD = "CONTRASEÑA DEL ARCHIVO DEL CERTIFICADO QUE LE PUSISTE CUANDO LO DESCARGASTE"; 
	private String src;
	private String dest;
	private Certificate[] chain;
	private PrivateKey pk;
	private String alias;
	private BouncyCastleProvider provider;
	
	public String getDestino() {
		return this.dest;
	}
	
	public UtilidadFirma(String src) throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyStoreException, UnrecoverableKeyException {
		this.src = src;
		this.dest = src.substring(0, src.lastIndexOf(".")) + "_signed.pdf";
		provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		

		//KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		
		ks.load(new FileInputStream(CERTIFICADO), PASSWORD.toCharArray());
		
		alias = (String)ks.aliases().nextElement();			
		pk = (PrivateKey) ks.getKey(alias, PASSWORD.toCharArray());		
		chain = ks.getCertificateChain(alias);
	}	
	
	public void sign(String reason, String location) throws IOException, DocumentException, GeneralSecurityException {
		
		// Creamos el lector de pdf y el sello
		PdfReader reader = new PdfReader(src);
		FileOutputStream os = new FileOutputStream(dest);
		
		PdfStamper sello = PdfStamper.createSignature(reader, os, '\0');
		
		//Creamos el sello y su localización.
		PdfSignatureAppearance apariencia = sello.getSignatureAppearance();
		apariencia.setReason(reason);
		apariencia.setLocation(location);
		apariencia.setVisibleSignature(new Rectangle(36, 748, 144, 700), 1, "sig");
		
		//Creamos la firma.
		ExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, provider.getName());
		
		ExternalDigest digest = new BouncyCastleDigest();
		
		MakeSignature.signDetached(apariencia, digest, pks, chain, 
				null, null, null, 0, CryptoStandard.CMS);
	}
	
	private static PdfPKCS7 verifySignature(AcroFields fields, String name) throws GeneralSecurityException, IOException {
		System.out.println("La firma cubre todo el documento: " + fields.signatureCoversWholeDocument(name));
		System.out.println("Revisión #: " + fields.getRevision(name) + " de " + fields.getTotalRevisions());
		
		PdfPKCS7 pkcs7 = fields.verifySignature(name);
		System.out.println("Verificado? " + pkcs7.verify());
		
		return pkcs7;
	}
	
	
	public static void verifySignatures(String path) throws IOException, GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());
		System.out.println("\nDocumento PDF: " + path);
		PdfReader reader = new PdfReader(path);
		
		AcroFields fields = reader.getAcroFields();
		ArrayList<String> names = fields.getSignatureNames();
		
		for (String name : names) {
			System.out.println("===== " + name + " =====");
			verifySignature(fields, name);
		}
		
		System.out.println();
	}
}
