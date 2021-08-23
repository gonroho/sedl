package es.us.sedl.marshaller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jvnet.jaxb2_commons.lang.EnumValue;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.EmpiricalStudy;
import es.us.isa.sedl.core.analysis.statistic.CorrelationCoeficient;
import es.us.isa.sedl.core.analysis.statistic.Nhst;
import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisSpec;
import es.us.isa.sedl.core.configuration.OutputDataSourceRole;
import es.us.isa.sedl.core.design.Design;
import es.us.isa.sedl.core.design.Outcome;
import es.us.isa.sedl.core.design.Variable;
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
		
		ControlledExperiment experiment = (ControlledExperiment) exp;
		final STGroup stGroup = new STGroupFile("templates/exampleTemplate.stg");
		String res ="";
//      ---------------------------- Variables ---------------------------
        
        Design design= experiment.getDesign();
        
        //muestra las variables, no tiene uso, seccion de control
//        List<String> vars= new ArrayList<String>();
//        
//        List<Variable>lista = design.getVariables().getVariables();
//        for (int i=0;i<lista.size();i++) {
//        	vars.add(lista.get(i).getName());
//        	//coge los valores de una variable enumerada
//        	lista.get(i).getDomain().getLevels().get(0).getValue();
//        }
        
        List<String> outvars= new ArrayList<String>();
        
        //lista entera
        List<Outcome>mainVars = design.getVariables().getOutcomes();
        for (int i=0;i<mainVars.size();i++) {
        	outvars.add(mainVars.get(i).getName());
        }
        
        //Sacar el path del dataset (Comprueba si hay experimental outputs y dentro de el DataSources de tipo Main Result)
        String path="";
        for (int i=0;i<experiment.getConfigurations().size();i++) {
        	for (int n=0;n<experiment.getConfigurations().get(i).getExperimentalOutputs().getOutputDataSources().size();n++) {
        		if (!experiment.getConfigurations().get(i).getExperimentalOutputs().getOutputDataSources().get(n).getFile().getPath().isEmpty()&&experiment.getConfigurations().get(i).getExperimentalOutputs().getOutputDataSources().get(n).getRole()==OutputDataSourceRole.MAIN_RESULT) {
            		path = experiment.getConfigurations().get(i).getExperimentalOutputs().getOutputDataSources().get(n).getFile().getPath();
                    
                    // Pick the correct template
            		final ST pathTemplate = stGroup.getInstanceOf("pathTemplate");

            		// Pass on values to use when rendering
            		pathTemplate.add("path", path);

            		// Render
            		final String render = pathTemplate.render();

            		// Print
            	    res += render;
            		
            	}
            }
        }
        
        
        
//        ----------------------------- Funciones --------------------------- 
        
        for (int i = 0; i < design.getExperimentalDesign().getIntendedAnalyses().size(); i++) {
			StatisticalAnalysisSpec a = (StatisticalAnalysisSpec) design.getExperimentalDesign().getIntendedAnalyses()
					.get(i);
			if (a!=null) {
				if (a.getStatistic().get(0).getClass().getSimpleName().equals("Nhst")) {
					res += renderNhst(a,stGroup, design, outvars);
					
				}else if (a.getStatistic().get(0).getClass().getSimpleName().equals("CorrelationCoeficient")) {
					res = "library(corrplot)\r\n"
							+ "library(RColorBrewer)\r\n"
									+ "library(\"ggpubr\")\n"+res;
					res += renderCorr(a,stGroup, design);
				}
				else {
					res += renderStatisticalAnalysis(a, stGroup, outvars);
				}
			}
		}
		return res;
	}

	public void toRScript(EmpiricalStudy exp, String fileName, Boolean run) throws IOException {
		
		
		// TODO StringTemplate exp.asString y guardar en fileName
		String text = this.asString(exp);
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
	    writer.write(text);
	    writer.close();
		
		
	    if (run) {
			// Copiado de "https://stackoverflow.com/questions/5711084/java-runtime-getruntime-getting-output-from-executing-a-command-line-program"
			Runtime rt = Runtime.getRuntime();
			String command = "C:\\Program Files\\R\\R-4.0.4\\bin\\Rscript " + fileName;
			Process proc = rt.exec(command);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			// Read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
			// Read any errors from the attempted command
			System.out.println("\n ------------------ \n Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			} 
		}else {
			System.out.println(text);
		}
	}
    
	public String renderStatisticalAnalysis(StatisticalAnalysisSpec a, STGroup stGroup, List<String> outvars){
        List<Statistic>list = a.getStatistic();
        String res="";

        for (int i=0;i<list.size();i++) {
        	String fun= list.get(i).getClass().getSimpleName().toLowerCase();
        	fun =fun.replace("standarddeviation","sd");
        	if (fun=="mode") {
        		res+="Modes <- function(x) {\r\n"
        				+ "  ux <- unique(x)\r\n"
        				+ "  tab <- tabulate(match(x, ux))\r\n"
        				+ "  ux[tab == max(tab)]\r\n"
        				+ "}";
        		fun =fun.replace("mode","Modes");
        	}
        	fun =fun.replace("interquartilerange","IQR");
        	String varfun="";
        	String rvar="";
        	// Funciones con filtros
        	if (!list.get(i).getDatasetSpecification().getValuationFilters().isEmpty()) {
        		varfun = list.get(i).getDatasetSpecification().getValuationFilters().get(0).getVariableValuations().toString();
        		varfun =varfun.replace('[', '(');
        		varfun =varfun.replace(']', ')');
        		varfun =varfun.replace("<-null", "");
        		varfun =varfun.replace("<-", "==");
        		
        		// Filtros simples Which clause
        		if(varfun.contains("=")) {
        			rvar=varfun.replace(")", "')");
        			rvar =rvar.replace("==", "=='");
        			rvar =rvar.replace("(", "");
        			
        			// Pick the correct template
        			final ST simpleWhich = stGroup.getInstanceOf("simpleWhich");

        			// Pass on values to use when rendering
        			simpleWhich.add("fun", fun);
        			simpleWhich.add("rvar", rvar);
        			simpleWhich.add("outvar", outvars.get(0));

        			// Render
        			final String render1 = simpleWhich.render();

        			// Print
        		    res += render1;
        			
        		//Filtros complejos. Agregados
        		}else {
        			varfun =varfun.replace("(", "");
        			varfun =varfun.replace(")", "");
        			
        			// Pick the correct template
        			final ST complexAggr = stGroup.getInstanceOf("complexAggr");

        			// Pass on values to use when rendering
        			complexAggr.add("fun", fun);
        			complexAggr.add("varfun", varfun);
        			complexAggr.add("outvar", outvars.get(0));

        			// Render
        			final String render2 = complexAggr.render();

        			// Print
        			res += render2;
        		}
        		
        	//Sin filtros, la funci�n se aplica sobre la variable de Salida
        	}else {      		
        		// Pick the correct template
    			final ST noFilter = stGroup.getInstanceOf("noFilter");

    			// Pass on values to use when rendering
    			noFilter.add("fun", fun);
    			noFilter.add("outvar", outvars.get(0));

    			// Render
    			final String render3 = noFilter.render();

    			// Print
    			res += render3;
        	}
        }   
	
		return res;
    }
	
	private String renderNhst(StatisticalAnalysisSpec a, STGroup stGroup, Design design, List<String> outvars) {
		List<Statistic>list = a.getStatistic();
        String res="";
        
        //muestra las variables, no tiene uso, seccion de control
        List<String> vars= new ArrayList<String>();
        
        List<Variable>lista = design.getVariables().getVariables();
        Map<String, List<String>> enumValues  = new HashMap<String, List<String>>();
        
        for (int i=0;i<lista.size();i++) {
        	if(lista.get(i).getDomain().getClass().getSimpleName().equals("ExtensionDomain")) {
        		
        		//Crea un map que asocia a cada variable sus enumerados
        		String key = lista.get(i).getName();
        		vars.add(key);
        		
        		
        		List<String> valuesList = new ArrayList<String>();
        		for (int k = 0; k < lista.get(i).getDomain().getLevels().size(); k++) {
        			valuesList.add(lista.get(i).getDomain().getLevels().get(k).getValue());
				}
        		enumValues.put(key, valuesList );
        	}
        	//coge los valores de una variable enumerada
        	//
        }
        
        String enum1 ="";
        String enum2 ="";
        //añadir error en caso de mas enumerados
        String newKey = "";
        for (int i=0;i<list.size();i++) {        	
        	Nhst nhst = (Nhst) list.get(i);
        	Double confLvl = 0.95;
        	if((Double) nhst.getAlpha() != null) {
        		confLvl = 1-nhst.getAlpha();
        	}
        	for (int j = 0; j < nhst.getDatasetSpecification().getGroupings().get(0).getProjectedVariables().size(); j++) {
				newKey = nhst.getDatasetSpecification().getGroupings().get(0).getProjectedVariables().get(j);
        		if (vars.contains(
						newKey)) {
        			
        			enum1 =enumValues.get(newKey).get(0);
        			enum2 =enumValues.get(newKey).get(1);
				}
				;
			}
        	String render="";
			if (nhst.getName().equals("T-student")) {
				final ST TStudentBy2 = stGroup.getInstanceOf("TStudentBy2");
				TStudentBy2.add("varBy", newKey);
				TStudentBy2.add("confLvl", confLvl);
				TStudentBy2.add("outvar", outvars.get(0));
				TStudentBy2.add("enum1", enum1);
				TStudentBy2.add("enum2", enum2);
				render = TStudentBy2.render();
			}else if(nhst.getName().equals("ANOVA")){
				final ST ANOVA = stGroup.getInstanceOf("ANOVA");
				ANOVA.add("varBy", newKey);
				ANOVA.add("outvar", outvars.get(0));
				render = ANOVA.render();
			}
        	res += render;
        }
		
        	
        // TODO Auto-generated method stub
		return res;
	}
	private String renderCorr(StatisticalAnalysisSpec a, STGroup stGroup, Design design) {
		List<Statistic>list = a.getStatistic();
        String res="";
        
        //muestra las variables, no tiene uso, seccion de control
        List<String> vars= new ArrayList<String>();


        for (int i=0;i<list.size();i++) {        	
        	CorrelationCoeficient corr = (CorrelationCoeficient) list.get(i);
        	vars = corr.getDatasetSpecification().getProjections().get(0).getProjectedVariables();
        	
        	String render="";
        	String plotname =design.hashCode()+corr.getName() + "0"+i;
			final ST Correlation = stGroup.getInstanceOf("CorrelationPDF");
			Correlation.add("x", vars.get(0));
			Correlation.add("y", vars.get(1));
			Correlation.add("method", corr.getName().toLowerCase());
			Correlation.add("path", "./src/main/resources/resPlots");
			Correlation.add("plotname", plotname);
			render = Correlation.render();
			
        	res += render;
        }
		
        	
        // TODO Auto-generated method stub
		return res;
	}
}
