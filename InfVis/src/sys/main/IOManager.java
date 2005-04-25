/**
 * Created on 21.03.2005
 * Project: InfVis
 * @author: harald
 * Class-Design Copyright by Harald Meyer
 */

package sys.main;

import java.io.*;
import java.util.zip.*;

public class IOManager implements java.io.Serializable{
    
    /** Creates a new instance of IOManager */
    public IOManager() {
    }
    
    /** Writes an object to a file */
    public void saveobject(String sfile, Object obj) throws InfVisException {
        try {
            FileOutputStream fs = new FileOutputStream(sfile);
			GZIPOutputStream gz = new GZIPOutputStream(fs);
            ObjectOutputStream os = new ObjectOutputStream(gz);

            os.writeObject(obj);
            os.close();
            gz.close();
            fs.close();
        } catch (FileNotFoundException e) {
            //System.out.println("File not found!");
            throw new InfVisException("File " + sfile + " not found!",false);
        } catch (IOException e) {
            throw new InfVisException("Fehler" + e,false);
            //System.out.println("Fehler"+e);
        }
    }
    
    /** Loades an object from a file */
    public Object loadobject(String sfile) throws InfVisException {	
        try {
            FileInputStream fs = new FileInputStream(sfile);
			GZIPInputStream gz = new GZIPInputStream(fs);
            ObjectInputStream is = new ObjectInputStream(gz);

            Object obj = is.readObject();
            is.close();
            gz.close();
            fs.close();

            return obj;
        } catch (FileNotFoundException e) {
            throw new InfVisException("File " + sfile + " not found!",false);
        } catch (IOException e) {
            throw new InfVisException("Error: " + e.getMessage(),false);
        } catch (ClassNotFoundException e) {
            throw new InfVisException("Error: " + e.getMessage(),false);
        }

   } 
   
   public boolean writeTextfile(String text, String loc) throws InfVisException {
		try {
			FileOutputStream out;
			out = new FileOutputStream(loc);
									
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			osw.write(text, 0, text.length());
			osw.close();
									
			return true;
		} 
		catch (Exception e){
		    throw new InfVisException("Error: " + e.getMessage(),false);
		}
		
	}
    
}
