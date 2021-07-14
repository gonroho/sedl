package es.us.sedl.marshaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import es.us.isa.sedl.core.EmpiricalStudy;
import es.us.isa.sedl.core.util.SEDLMarshaller;

public class R4SEDLMarshaller implements SEDLMarshaller{

	@Override
	public List<String> write(EmpiricalStudy exp, OutputStream os) 
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String asString(EmpiricalStudy exp) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void toRScript(EmpiricalStudy exp, String fileName) throws IOException {
		
		
		// TODO StringTemplate exp.asString y guardar en fileName
		
		
	// Copiado de "https://stackoverflow.com/questions/5711084/java-runtime-getruntime-getting-output-from-executing-a-command-line-program"
		Runtime rt = Runtime.getRuntime();
		String command = "C:\\Program Files\\R\\R-4.0.4\\bin\\Rscript " +fileName;
		Process proc = rt.exec(command);

		BufferedReader stdInput = new BufferedReader(new 
		     InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new 
		     InputStreamReader(proc.getErrorStream()));

		// Read the output from the command
		System.out.println("Here is the standard output of the command:\n");
		String s = null;
		while ((s = stdInput.readLine()) != null) {
		    System.out.println(s);
		}

		// Read any errors from the attempted command
		System.out.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) {
		    System.out.println(s);
		}
	}

}
