package Rwriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.EmpiricalStudy;
import es.us.isa.sedl.core.analysis.datasetspecification.DatasetSpecification;
import es.us.isa.sedl.core.analysis.datasetspecification.GroupingProjection;
import es.us.isa.sedl.core.analysis.datasetspecification.Projection;
import es.us.isa.sedl.core.analysis.statistic.Nhst;
import es.us.isa.sedl.core.analysis.statistic.Statistic;
import es.us.isa.sedl.core.analysis.statistic.StatisticalAnalysisSpec;
import es.us.isa.sedl.core.configuration.Configuration;
import es.us.isa.sedl.core.configuration.ExperimentalOutputs;
import es.us.isa.sedl.core.configuration.OutputDataSource;
import es.us.isa.sedl.core.configuration.OutputDataSourceRole;
import es.us.isa.sedl.core.design.AnalysisSpecificationGroup;
import es.us.isa.sedl.core.util.SEDLMarshaller;
import es.us.isa.sedl.jlibsedl.JLibSEDL;
import es.us.isa.sedl.jlibsedl.SEDLDocument;
import es.us.isa.sedl.jlibsedl.SerializationFormat;
import es.us.isa.sedl.marshaller.SEDL4PeopleMarshaller;
import es.us.isa.sedl.marshaller.SEDL4PeopleUnmarshaller;

public class testSEDLcreator {
	static String basePath = "./src/main/resources/SEDLfiles";
	static String sourcePath =" C:/Users/user/Desktop/examples/dataset.csv";
	
	//al generar archivos el unmarshaller provoca errores en
//	Groups : 
//		by *falta sex* sizing 25
//		...]
//		Avg(where sex= ) *mala sintaxis*
//	Y al añadir el dataset
//  no reconoce el archivo Bernarde2014 como SEDL
	public static void main(String[] args) throws IOException {
		File f = new File("./src/main/resources/SEDLfiles/HeightExperimentNoNhst.SEDL");

//		File f = new File("C:/Users/user/git/sedl/modules/R4SEDL/src/main/resources/SEDLfiles/test1.sedl");
		System.out.println(f.getAbsolutePath());
		System.out.println(es.us.isa.sedl.jlibsedl.JLibSEDL.isSEDL(f)+"\n");
		SEDLDocument result = null;
		result = es.us.isa.sedl.jlibsedl.JLibSEDL.readDocument(f);
        
		SEDL4PeopleUnmarshaller unmash = new SEDL4PeopleUnmarshaller();
		ControlledExperiment experiment = (ControlledExperiment) unmash.fromString(usingBufferedReader(f.getPath()));
		
//        ControlledExperiment experiment = (ControlledExperiment) result.getExperiment();
        
        ControlledExperiment experiment1 = addDataset(experiment, sourcePath);      
        ControlledExperiment experiment2 = addTStudent(experiment1);
        ControlledExperiment experiment3 = addANOVA(experiment2);
        
        experiment1.setName("HeightDatasetAdded");
        generateSEDLfile(experiment1);
        experiment3.setName("HeightNhstAdded");
        generateSEDLfile(experiment3);
        
        System.out.println("Archivos generados");
        
        File f2=new File(basePath+File.separator+experiment3.getName()+".sedl");
        ControlledExperiment experiment4 = (ControlledExperiment) unmash.fromString(usingBufferedReader(f2.getPath()));
        System.out.println("Unmarshalled equals original:"+experiment3.equals(experiment4));
	}
	public static ControlledExperiment addDataset (ControlledExperiment e, String Path) {
		//El marshaller solo Imprime el nombre del Archivo, no el Path
		es.us.isa.sedl.core.configuration.File source= new es.us.isa.sedl.core.configuration.File();
        source.setPath(Path);
        source.setName(Path);
        
        OutputDataSource fuente = new OutputDataSource();
        fuente.setFile(source);
        fuente.setFileSpecification(null);
        fuente.setRole(OutputDataSourceRole.MAIN_RESULT);
        
        List<OutputDataSource> outputDataSources = new ArrayList<OutputDataSource>();
        outputDataSources.add(fuente);
        
        ExperimentalOutputs experimentaloutputs=new ExperimentalOutputs();
        experimentaloutputs.getOutputDataSources().add(fuente);
        
        Configuration config=new Configuration();
        config.setExperimentalOutputs(experimentaloutputs);
        
        e.getConfigurations().add(config);
        
		return e;
	}
	public static ControlledExperiment addTStudent (ControlledExperiment e) {
		
		ControlledExperiment e1 = (ControlledExperiment) e.clone();
		Nhst n = new Nhst();
		n.setAlpha(0.05);
		n.setName("T-student");
		
		GroupingProjection gp = new GroupingProjection();
		
		gp.getProjectedVariables().add("sex");
		
		DatasetSpecification ds = new DatasetSpecification();
//no se puede añadir a las groupingProjections. El getGrouping crea una copia (snapshot)
		ds.getProjections().add(gp);
		n.setDatasetSpecification(ds);
		
		
		StatisticalAnalysisSpec s = new StatisticalAnalysisSpec();
		
		if(e1.getDesign().getExperimentalDesign().getIntendedAnalyses().size()>1) {
			s= (StatisticalAnalysisSpec) e1.getDesign().getExperimentalDesign().getIntendedAnalyses().get(1);
			s.getStatistic().add(n);
		}else {
			s.getStatistic().add(n);
			e1.getDesign().getExperimentalDesign().getIntendedAnalyses().add(s);
		}
		
		e1.getDesign().getExperimentalDesign().getIntendedAnalyses().get(1).setId("NHST");
		
		return e1;
	}
	public static ControlledExperiment addANOVA (ControlledExperiment e) {
		ControlledExperiment e1 = (ControlledExperiment) e.clone();
		Nhst n = new Nhst();
		n.setAlpha(0.05);
		n.setName("ANOVA");
		
		GroupingProjection gp = new GroupingProjection();
		
		gp.getProjectedVariables().add("sex");
		
		DatasetSpecification ds = new DatasetSpecification();
//no se puede añadir a las groupingProjections. El getGrouping crea una copia (snapshot)
		
		
		Projection pr = new Projection();
		pr.getProjectedVariables().add("height");
		ds.getProjections().add(pr);
		ds.getProjections().add(gp);
		n.setDatasetSpecification(ds);
		
		StatisticalAnalysisSpec s = new StatisticalAnalysisSpec();
		
		if(e1.getDesign().getExperimentalDesign().getIntendedAnalyses().size()>1) {
			s= (StatisticalAnalysisSpec) e1.getDesign().getExperimentalDesign().getIntendedAnalyses().get(1);
			s.getStatistic().add(n);
		}else {
			s.getStatistic().add(n);
			e1.getDesign().getExperimentalDesign().getIntendedAnalyses().add(s);
		}
		e1.getDesign().getExperimentalDesign().getIntendedAnalyses().get(1).setId("NHST");
		return e1;
	}
	public static void generateSEDLfile (ControlledExperiment e) throws IOException {
		File f=new File(basePath+File.separator+e.getName()+".sedl");
		SEDL4PeopleMarshaller mash = new SEDL4PeopleMarshaller();
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write(mash.asString(e));
	    writer.close();
	}
	private static String usingBufferedReader(String filePath) 
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) 
        {
 
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) 
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
