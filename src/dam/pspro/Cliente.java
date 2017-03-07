package dam.pspro;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class Cliente {

	private Socket socket;
	private PublicKey clavePublicaServidor;
	private SecretKey claveSimetrica;
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	private String nombrePdf;
	
	public Cliente(String nombrePdf) {
		
		this.nombrePdf = nombrePdf;
		
		System.out.println("Creando socket cliente");
		
		socket = new Socket();		
		InetSocketAddress address = new InetSocketAddress("localhost", 5555);	
			
		try {
			socket.connect(address);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
			// Genera la clave de sesión
			claveSimetrica = UtilidadAES.generarClaveAES(128);	
			
			// Recibe la clave pública
			clavePublicaServidor = (PublicKey)ois.readObject();
							
			// Encripta la clave de sesión con la clave pública y se la envía al servidor
			oos.writeObject(UtilidadRSA.encriptar(claveSimetrica.getEncoded(), clavePublicaServidor));
			oos.flush();
					
			// Petición del archivo pdf encriptada
			String mensajeEnc = UtilidadAES.encriptar(nombrePdf, claveSimetrica);	
			oos.writeObject(mensajeEnc);
			oos.flush();
			
			// Recibe la respuesta desde el servidor
			String respuesta = (String)ois.readObject();
			               
	        if (respuesta.equals("OK")) {
	        	FileWriter fstream = new FileWriter(nombrePdf);  
		        BufferedWriter out = new BufferedWriter(fstream);  
	        	
		        // Desencripta el array de bytes que le llega
	        	byte[] pdfFirmadoEncriptado = (byte[])ois.readObject();
	        	byte[] pdfFirmadoBytes = UtilidadAES.desencriptarBytes(pdfFirmadoEncriptado, claveSimetrica);
		        
	        	// Convierte el array de bytes a documento PDF
		        Path path = Paths.get(nombrePdf);
	            Files.write(path, pdfFirmadoBytes);
				
	            System.out.println("\nArchivo descargado correctamente!");
	            
	            // Verifica la firma del documento
				UtilidadFirma.verifySignatures(nombrePdf);
	        }
	        						
			oos.close();
			ois.close();
			
			System.out.println("\nCerrando la conexión con el servidor...");
			socket.close();
			
		} catch (EOFException e) {
			System.out.println("\nEl archivo solicitado no existe.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {	
		System.out.println ("Firma remota de documentos PDF ");
		System.out.println ("==============================");
		System.out.println ("");
        System.out.println ("Introduzca el nombre del archivo pdf (Archivo demo \"test.pdf\"): ");
        String nombre = "";
        Scanner entradaEscaner = new Scanner (System.in);
        nombre = entradaEscaner.nextLine ();
        System.out.println ("\n");

		new Cliente(nombre);
	}
	

	public void getClavePublica() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
			
		//clavePublicaServidor = UtilidadRSA.getClavePublicaDesdeString(br.readLine());
		System.out.println(clavePublicaServidor.toString());
	}
		
	public void compartirClaveSimetrica() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String claveSimetricaString = Base64.getEncoder().encodeToString(claveSimetrica.getEncoded());
		String claveSimetricaEncriptada = Base64.getEncoder().encodeToString(UtilidadRSA.encriptar(claveSimetricaString, clavePublicaServidor).getBytes());
		System.out.println("\n" + claveSimetricaEncriptada);
	}
	
}
