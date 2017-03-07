package dam.pspro;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

	public static final int PUERTO = 5555;
	
	public Servidor() {
		try {
			System.out.println("Creando socket del servidor...");
			ServerSocket socket = new ServerSocket();			
			InetSocketAddress address = new InetSocketAddress("localhost", PUERTO);	
			
			System.out.println("Realizando bind...");
			socket.bind(address);
			
			while (socket != null) {				
				System.out.println("\nAceptando conexiones...");
				Socket newSocket = socket.accept();
				System.out.println("Conexión realizada con el cliente...");
				new SrvHilo(newSocket).start();
			}
			
			System.out.println("El servidor deja de escuchar...");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Servidor();
	}
}
