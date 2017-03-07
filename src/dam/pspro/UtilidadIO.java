package dam.pspro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class UtilidadIO {
	
	protected static final File SRV_DIRECTORIO_PDF = new File("./pdf/");
	protected static final File CLI_DIRECTORIO_PDF = new File("./pdfcli/");

	
	public static boolean pdfExists(String nombre) {
		
		boolean result = false;
		
		File[] list = SRV_DIRECTORIO_PDF.listFiles();
        if(list!=null)
        for (File archivo : list)
        {
        	if (archivo.getName().equals(nombre)) {
        		result = true;
        	} 	
        }
        
        return result;
	}
	
	public static byte[] leerBytesDesdeArchivo(String src) {

        FileInputStream fis = null;
        byte[] bytesArray = null;

        try {

            File archivo = new File(src);
            bytesArray = new byte[(int) archivo.length()];

            fis = new FileInputStream(archivo);
            fis.read(bytesArray);

        } catch (IOException e) {
        	bytesArray = null;           
        } finally {
            if (fis != null) {
                try {
                	fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }

}
