package dam.pspro;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.itextpdf.text.DocumentException;

public class SrvHilo extends Thread {
	
	private Socket socket;
	private SecretKey claveSimetrica;
	private KeyPair parClave;
	private PrintWriter pw;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	
	public SrvHilo(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
			// Genera el par de claves
			parClave = UtilidadRSA.generarKeyPair();
			
			// Comparte la clave Pública con el cliente
			compartirClavePublica();
			
			// Recibe la clave Simétrica generada por el cliente
			getClaveSimetrica();
		
			// Pregunta si hay petición de pdf y responde a la petición
			respuestaPdf(getRequestPdf());
			
			System.out.println("Cerrando la conexión con el cliente");
			oos.close();
			ois.close();
			socket.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getRequestPdf() throws Exception {
		String mensajeEnc = (String)ois.readObject();
		return UtilidadAES.desencriptar(mensajeEnc, claveSimetrica);
	}
	
	public void respuestaPdf(String nombre) throws IOException, DocumentException, GeneralSecurityException {
		
		if (UtilidadIO.pdfExists(nombre)) {
			
			// Firmado del documento
			UtilidadFirma firma = new UtilidadFirma("./pdf/" + nombre);
			firma.sign("Tarea Final PSPRO", "Málaga");
							
			// Convierte el archivo a bytes
			byte[] pdfFirmadoBytes = UtilidadIO.leerBytesDesdeArchivo(firma.getDestino());
			
			if (pdfFirmadoBytes != null) {
				oos.writeObject("OK");
				oos.flush();
				
				// Encripta el array de bytes y los envía al cliente
				oos.writeObject(UtilidadAES.encriptarBytes(pdfFirmadoBytes, claveSimetrica));
				oos.flush();
			} else {
				oos.writeObject("ERROR");
				oos.flush();
			}
			
			
		}	
	}
	
	public void compartirClavePublica() throws IOException {
		oos.writeObject(parClave.getPublic());	
		oos.flush();	
	}
	
	public void getClaveSimetrica() throws ClassNotFoundException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] claveSimetricaEncriptada = (byte[])ois.readObject();
		byte[] claveSimetricaDesencriptada = UtilidadRSA.desencriptar(claveSimetricaEncriptada, parClave.getPrivate());
		
		// Transformación de los bytes recibidos a clave simétrica
		claveSimetrica = new SecretKeySpec(claveSimetricaDesencriptada, "AES");		
	}
}
