package Rwriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.us.isa.sedl.core.ControlledExperiment;
import es.us.isa.sedl.core.EmpiricalStudy;
import es.us.isa.sedl.core.configuration.Configuration;
import es.us.isa.sedl.core.configuration.ExperimentalOutputs;
import es.us.isa.sedl.core.configuration.OutputDataSource;
import es.us.isa.sedl.core.configuration.OutputDataSourceRole;
import es.us.isa.sedl.jlibsedl.SEDLDocument;
import es.us.sedl.marshaller.R4SEDLMarshaller;

public class marshallerTest {

	public static void main(String[] args) throws IOException {
		File f = new File("./src/main/resources/SEDLfiles/HeightExperimentNoNhst.SEDL");
        System.out.println(es.us.isa.sedl.jlibsedl.JLibSEDL.isSEDL(f)+"\n");
        SEDLDocument result = null;
        result = es.us.isa.sedl.jlibsedl.JLibSEDL.readDocument(f);
		
        ControlledExperiment experiment = (ControlledExperiment) result.getExperiment(); 
        
        es.us.isa.sedl.core.configuration.File source= new es.us.isa.sedl.core.configuration.File();
        source.setPath("C:/Users/user/Desktop/examples/dataset.csv");
        List<OutputDataSource> outputDataSources = new ArrayList<OutputDataSource>();
        OutputDataSource fuente = new OutputDataSource();
        fuente.setFile(source);
        fuente.setRole(OutputDataSourceRole.MAIN_RESULT);
        outputDataSources.add(fuente);
        ExperimentalOutputs experimentaloutputs=new ExperimentalOutputs();
        experimentaloutputs.getOutputDataSources().add(fuente);
        Configuration config=new Configuration();
        config.setExperimentalOutputs(experimentaloutputs);
        experiment.getConfigurations().add(config);
        
		R4SEDLMarshaller r  = new R4SEDLMarshaller();
		r.toRScript(experiment, "C:\\Users\\user\\git\\sedl\\modules\\R4SEDL\\src\\main\\resources\\test2.r", false);
	}

}
