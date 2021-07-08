package Rwriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.EmpiricalStudy;
import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisSpec;
import es.us.isa.sedl.core.design.Design;
import es.us.isa.sedl.core.design.FullySpecifiedExperimentalDesign;
import es.us.isa.sedl.core.design.Outcome;
import es.us.isa.sedl.core.design.Variable;
import es.us.isa.sedl.core.design.Variables;
import es.us.isa.sedl.jlibsedl.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class test {
	static String fileName = "C:\\Users\\user\\git\\sedl\\modules\\R4SEDL\\src\\main\\resources\\scriptsR\\test.r";
	
	public static void main(String[] args) throws IOException {
		test6();
		
	}
	public static void test3() throws IOException {
		String text=
              "data = read.csv(\"C:/Users/user/Desktop/examples/dataset.csv\", header=T, sep=\";\")"+ "\n"+
              "mean(data$height)"+"\n" +
              "mean(data[ which(data$sex=='men'),]$height)" + "\n"+		
              "median(data$height)"+"\n"+
              "datagroupby <- aggregate(data$height, by=list(data$sex), FUN=mean)"+"\n"+
              "datagroupby"+"\n"+
              "sd(data$height,na.rm = FALSE)"+"\n";
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
	    writer.write(text);
	    writer.close();
	}
	public static void test5() throws IOException {
		// Load the file
		final STGroup stGroup = new STGroupFile("exampleTemplate.stg");

		// Pick the correct template
		final ST templateExample = stGroup.getInstanceOf("templateExample");

		// Pass on values to use when rendering
		templateExample.add("param", "Test");
		templateExample.add("condition", false);

		// Render
		final String render = templateExample.render();

		// Print
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
	    writer.write(render);
	    writer.close();
	}
	public static void test4() throws IOException {
		
		File f = new File("C:/Users/user/Desktop/examples/SEDL-Example-WS/Experiments/HeightExperiment/HeightExperiment.SEDL");
        System.out.println(es.us.isa.sedl.jlibsedl.JLibSEDL.isSEDL(f)+"\n");
        SEDLDocument result = null;
        result = es.us.isa.sedl.jlibsedl.JLibSEDL.readDocument(f);
		
        ControlledExperiment experiment = (ControlledExperiment) result.getExperiment();
        
        
//        ---------------------------- Variables ---------------------------
        
        Design design= experiment.getDesign();
        
        List<String> vars= new ArrayList<String>();
        
        List<Variable>lista = design.getVariables().getVariables();
        for (int i=0;i<lista.size();i++) {
        	vars.add(lista.get(i).getName());
        }
        
        List<String> outvars= new ArrayList<String>();
        
        //lista entera
        List<Outcome>mainVars = design.getVariables().getOutcomes();
        for (int i=0;i<mainVars.size();i++) {
        	outvars.add(mainVars.get(i).getName());
        }
        
//        System.out.println("Variables de Salida: " + outvars);
//        System.out.println("Variables: " + vars);
        
//        ----------------------------- Funciones --------------------------- 
        
        StatisticalAnalysisSpec a=(StatisticalAnalysisSpec) design.getExperimentalDesign().getIntendedAnalyses().get(0);
        
//        List<String> funs= new ArrayList<String>();
        List<String> rfuns= new ArrayList<String>();
        List<Statistic>lista2 = a.getStatistic();
        
        for (int i=0;i<lista2.size();i++) {
        	String fun= lista2.get(i).getClass().getSimpleName().toLowerCase();
        	fun =fun.replace("standarddeviation","sd");
        	String varfun="";
        	String rvar="";
        	String rfun="";
        	if (!lista2.get(i).getDatasetSpecification().getValuationFilters().isEmpty()) {
        		varfun = lista2.get(i).getDatasetSpecification().getValuationFilters().get(0).getVariableValuations().toString();
        		varfun =varfun.replace('[', '(');
        		varfun =varfun.replace(']', ')');
        		varfun =varfun.replace("<-null", "");
        		varfun =varfun.replace("<-", "==");
        		
        		if(varfun.contains("=")) {
        			rvar=varfun.replace(")", "')");
        			rvar =rvar.replace("==", "=='");
        			rvar =rvar.replace("(", "");
        			rvar="[ which(data$"+rvar+",]$"+outvars.get(0);
        			rfun = fun+"(data"+rvar+")";
        		}else {
        			varfun =varfun.replace("(", "");
        			varfun =varfun.replace(")", "");
        			rfun="datagroupby <- aggregate(data$"+outvars.get(0)+", by=list(data$"+varfun+"), FUN="+fun+")\ndatagroupby";
        		}
        	}else {
        		rvar = "$"+outvars.get(0);
        		rfun = fun+"(data"+rvar+")";
        	}
        	rfuns.add(rfun);
//        	fun += varfun;
//        	funs.add(fun);
        }
//        System.out.println("Funciones: " + funs);
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write("data = read.csv(\"C:/Users/user/Desktop/examples/dataset.csv\", header=T, sep=\";\")"+ "\n");
	    
        for (int i=0;i<rfuns.size();i++) {
        	writer.write(rfuns.get(i)+"\n");
        }
        writer.close();
        System.out.println("Archivo R creado");
        
	}
	public static void test6() throws IOException {
		
		File f = new File("C:/Users/user/Desktop/examples/SEDL-Example-WS/Experiments/HeightExperiment/HeightExperiment.SEDL");
        System.out.println(es.us.isa.sedl.jlibsedl.JLibSEDL.isSEDL(f)+"\n");
        SEDLDocument result = null;
        result = es.us.isa.sedl.jlibsedl.JLibSEDL.readDocument(f);
		
        ControlledExperiment experiment = (ControlledExperiment) result.getExperiment();
        
        
//        ---------------------------- Variables ---------------------------
        
        Design design= experiment.getDesign();
        
        List<String> vars= new ArrayList<String>();
        
        List<Variable>lista = design.getVariables().getVariables();
        for (int i=0;i<lista.size();i++) {
        	vars.add(lista.get(i).getName());
        }
        
        List<String> outvars= new ArrayList<String>();
        
        //lista entera
        List<Outcome>mainVars = design.getVariables().getOutcomes();
        for (int i=0;i<mainVars.size();i++) {
        	outvars.add(mainVars.get(i).getName());
        }
        
//        System.out.println("Variables de Salida: " + outvars);
//        System.out.println("Variables: " + vars);
        
//        ----------------------------- Funciones --------------------------- 
        
        StatisticalAnalysisSpec a=(StatisticalAnalysisSpec) design.getExperimentalDesign().getIntendedAnalyses().get(0);
        
//        List<String> funs= new ArrayList<String>();
        List<String> rfuns= new ArrayList<String>();
        List<Statistic>lista2 = a.getStatistic();
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
	    
        final STGroup stGroup = new STGroupFile("templates/exampleTemplate.stg");

        writer.write("data = read.csv(\"C:/Users/user/Desktop/examples/dataset.csv\", header=T, sep=\";\")\n");
        for (int i=0;i<lista2.size();i++) {
        	String fun= lista2.get(i).getClass().getSimpleName().toLowerCase();
        	fun =fun.replace("standarddeviation","sd");
        	String varfun="";
        	String rvar="";
        	String rfun="";
        	// Funciones con filtros
        	if (!lista2.get(i).getDatasetSpecification().getValuationFilters().isEmpty()) {
        		varfun = lista2.get(i).getDatasetSpecification().getValuationFilters().get(0).getVariableValuations().toString();
        		varfun =varfun.replace('[', '(');
        		varfun =varfun.replace(']', ')');
        		varfun =varfun.replace("<-null", "");
        		varfun =varfun.replace("<-", "==");
        		// Filtros simples Which clause
        		if(varfun.contains("=")) {
        			rvar=varfun.replace(")", "')");
        			rvar =rvar.replace("==", "=='");
        			rvar =rvar.replace("(", "");
//        			rvar="[ which(data$"+rvar+",]$"+outvars.get(0);
//        			rfun = fun+"(data"+rvar+")\n";
        			
        			// Pick the correct template
        			final ST simpleWhich = stGroup.getInstanceOf("simpleWhich");

        			// Pass on values to use when rendering
        			simpleWhich.add("fun", fun);
        			simpleWhich.add("rvar", rvar);
        			simpleWhich.add("outvar", outvars.get(0));

        			// Render
        			final String render = simpleWhich.render();

        			// Print
        		    writer.write(render);
        			
        		//Filtros complejos. Agregados
        		}else {
        			varfun =varfun.replace("(", "");
        			varfun =varfun.replace(")", "");
//        			rfun="datagroupby <- aggregate(data$"+outvars.get(0)+", by=list(data$"+varfun+"), FUN="+fun+")\ndatagroupby\n";
        		
        			// Pick the correct template
        			final ST complexAggr = stGroup.getInstanceOf("complexAggr");

        			// Pass on values to use when rendering
        			complexAggr.add("fun", fun);
        			complexAggr.add("varfun", varfun);
        			complexAggr.add("outvar", outvars.get(0));

        			// Render
        			final String render = complexAggr.render();

        			// Print
        		    writer.write(render);
        		}
        	//Sin filtros, la funci�n se aplica sobre la variable de Salida
        	}else {
//        		rvar = "$"+outvars.get(0);
//        		rfun = fun+"(data"+rvar+")"+"\n";
        		
        		// Pick the correct template
    			final ST noFilter = stGroup.getInstanceOf("noFilter");

    			// Pass on values to use when rendering
    			noFilter.add("fun", fun);
    			noFilter.add("outvar", outvars.get(0));

    			// Render
    			final String render = noFilter.render();

    			// Print
    		    writer.write(render);
        	}
        	rfuns.add(rfun);
//        	fun += varfun;
//        	funs.add(fun);
        }
//        System.out.println("Funciones: " + funs);
        
        
	    writer.close();
	    System.out.println("Archivo R creado");
	}
	public static void test2() throws FileNotFoundException {
		File f = new File("C:/Users/user/Desktop/examples/SEDL-Example-WS/Experiments/HeightExperiment/HeightExperiment.SEDL");
        System.out.println(es.us.isa.sedl.jlibsedl.JLibSEDL.isSEDL(f)+"\n");
        SEDLDocument result = null;
        result = es.us.isa.sedl.jlibsedl.JLibSEDL.readDocument(f);
        
        ControlledExperiment experiment;
        experiment = (ControlledExperiment) result.getExperiment();
        
        Design design= experiment.getDesign();
        //System.out.println(experiment.getConfigurations());
        System.out.println(design.getVariables().getVariables());
        design.getDesignParameters();
        System.out.println(design.getDesignParameters());
        design.getExperimentalDesign();
        StatisticalAnalysisSpec a=(StatisticalAnalysisSpec) design.getExperimentalDesign().getIntendedAnalyses().get(0);
       
        System.out.println(design.getPossibleTreatments());
        System.out.println(a.getStatistic().get(0).getClass().getSimpleName());
//        design.getPopulation();
//        System.out.println(design.getPopulation());
//        design.getSamplingMethod();
//        System.out.println(design.getSamplingMethod().getDescription());
        
	}
	public static void test1(String[] args) throws IOException {
		    String str = "print(\"Hola mundo\")";
		    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		    writer.write(str);
		    writer.close();
		    
		    es.us.isa.sedl.jlibsedl.App.main(args);
		    
		    ST template = new ST("Template, <var>");
	        template.add("var", "Test");
	        System.out.println(template.render());
	        
	        File f = new File("C:/Users/user/Desktop/examples/SimpleExperiment.json");
	        System.out.println(es.us.isa.sedl.jlibsedl.JLibSEDL.isSEDL(f)+"\n");
	        
	        SEDLDocument result = null;
	        result = es.us.isa.sedl.jlibsedl.JLibSEDL.readDocument(f);
	        
	        ControlledExperiment experiment;
	        experiment = (ControlledExperiment) result.getExperiment();
	        System.out.println(experiment.getNotes());
	        System.out.println(experiment.getAnnotations());
	        System.out.println(experiment.getId());
	        System.out.println(experiment.getGoal());
	        System.out.println(experiment.getContext());
	        System.out.println(experiment.getMetaid());
	        System.out.println(experiment.getName());
	        System.out.println(experiment.getVersion()); 
	        
	        System.out.println(experiment.getDesign());
//	        Design design= experiment.getDesign();
//	        design.getVariables();
//	        design.getDesignParameters();
//	        design.getExperimentalDesign();
//	        design.getPopulation();
//	        design.getSamplingMethod();
	        
	        System.out.println(experiment.getConfigurations());
	        System.out.println(experiment.getHypotheses());
	        System.out.println(experiment.getAbstract());
	        
	        
//	        "@type" : "ControlledExperiment",
//	        "notes" : [ ],
//	        "annotations" : [ ],
//	        "id" : "SimpleExperiment",
//	        "goal" : null,
//	        "context" : null,
//	        "metaid" : null,
//	        "name" : "SimpleExperiment",
//	        "version" : "1.0.0-SNAPSHOT",
//	        "design" : null,
//	        "configurations" : [ ],
//	        "hypotheses" : [ ],
//	        "abstract" : null
	}
}
