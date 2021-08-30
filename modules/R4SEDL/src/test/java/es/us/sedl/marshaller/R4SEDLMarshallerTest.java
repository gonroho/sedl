package es.us.sedl.marshaller;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.analysis.statistic.CorrelationCoeficient;
import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisSpec;
import es.us.isa.sedl.core.configuration.Configuration;
import es.us.isa.sedl.core.configuration.ExperimentalOutputs;
import es.us.isa.sedl.core.configuration.OutputDataSource;
import es.us.isa.sedl.core.configuration.OutputDataSourceRole;
import es.us.isa.sedl.core.design.Design;
import es.us.isa.sedl.jlibsedl.SEDLDocument;

public class R4SEDLMarshallerTest {
	ControlledExperiment experiment;
	ControlledExperiment experiment2;
	ControlledExperiment experiment3;
	R4SEDLMarshaller marsh;
	
	private static PrintStream originalStream; 
    private static PrintStream newStream; 
    private static ByteArrayOutputStream newConsole;
    
    final STGroup stGroup = new STGroupFile("templates/exampleTemplate.stg");

	@Before
	public void setUp() throws Exception {
		File f = new File("./src/main/resources/SEDLfiles/Basic4Tests.SEDL");

        SEDLDocument result = null;
        result = es.us.isa.sedl.jlibsedl.JLibSEDL.readDocument(f);
		
        experiment = (ControlledExperiment) result.getExperiment(); 
        
        File f2 = new File("./src/main/resources/SEDLfiles/Basic4TestsANOVA.SEDL");

        SEDLDocument result2 = null;
        result2 = es.us.isa.sedl.jlibsedl.JLibSEDL.readDocument(f2);
		
        experiment2 = (ControlledExperiment) result2.getExperiment(); 
        
        File f3 = new File("./src/main/resources/SEDLfiles/Basic4TestsCorr.SEDL");

        SEDLDocument result3 = null;
        result3 = es.us.isa.sedl.jlibsedl.JLibSEDL.readDocument(f3);
		
        experiment3 = (ControlledExperiment) result3.getExperiment(); 
        marsh = new R4SEDLMarshaller();
        
        
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testAsString() {
		String expResult = "mean(data$height)";
		String result= marsh.asString(experiment);
		assertEquals(expResult.trim(), result.trim());
	}

	@Test
	public final void testToRScript() throws IOException {
		String expResult = marsh.asString(experiment).trim();
		String result;
		newConsole = new ByteArrayOutputStream(); 

	    originalStream = System.out; 
	 
	    // set new print stream, now anything written via 
	    // System.out will be stored in the byte array 'newConsole' 
	    newStream = new PrintStream(newConsole); 
	    System.setOut(newStream); 
	    
	    marsh.toRScript(experiment, "C:\\Users\\user\\git\\sedl\\modules\\R4SEDL\\src\\main\\resources\\scriptsR\\junitest.r", false);
	    System.setOut(originalStream); 
	    result = newConsole.toString().trim();
	    
	    assertEquals(expResult, result);
	}

	@Test
	public final void testRenderStatisticalAnalysis() {
		Design design= experiment.getDesign();
		StatisticalAnalysisSpec a = (StatisticalAnalysisSpec) design.getExperimentalDesign().getIntendedAnalyses()
				.get(0);
		List<String> outvars= new ArrayList<String>();
		outvars.add("height");
		String expResult = "mean(data$height)".trim();
		String result= marsh.renderStatisticalAnalysis(a, stGroup, outvars).trim();

		assertEquals(expResult, result);
	}

	@Test
	public final void testRenderNhst() {
		Design design= experiment2.getDesign();
		StatisticalAnalysisSpec a = (StatisticalAnalysisSpec) design.getExperimentalDesign().getIntendedAnalyses()
				.get(0);
		List<String> outvars= new ArrayList<String>();
		outvars.add("height");
		String expResult = "aov(height ~ sex, data = data)".trim();
		String result= marsh.renderNhst(a, stGroup, design, outvars).trim();

		assertEquals(expResult, result);
	}

	@Test
	public final void testRenderCorr() {
		Design design= experiment3.getDesign();
		StatisticalAnalysisSpec a = (StatisticalAnalysisSpec) design.getExperimentalDesign().getIntendedAnalyses()
				.get(0);
		List<String> outvars= new ArrayList<String>();
		outvars.add("height");
		List<Statistic>list = a.getStatistic();
		CorrelationCoeficient corr = (CorrelationCoeficient) list.get(0);
	
		String plotname =design.hashCode()+corr.getName() + "0"+"0";
		
		String expResult = "pdf(file = \"./src/main/resources/resPlots/Plot"+plotname+".pdf\",\r\n"
				+ "    width = 8,\r\n"
				+ "    height = 6) \r\n"
				+ "M <-cor(data$height, data$weight, method = c(\"pearson\"))\r\n"
				+ "M\r\n"
				+ "ggscatter(data, x = \"height\", y = \"weight\", \r\n"
				+ "          add = \"reg.line\", conf.int = TRUE, \r\n"
				+ "          cor.coef = TRUE, cor.method = \"pearson\",\r\n"
				+ "          xlab = \"height\", ylab = \"weight\")\r\n"
				+ "dev.off()\r\n"
				+ "print(\"Resulting plots saved in PDF format in ./src/main/resources/resPlots\")".trim();
		String result= marsh.renderCorr(a, stGroup, design).trim();

		assertEquals(expResult, result);
	}

}
