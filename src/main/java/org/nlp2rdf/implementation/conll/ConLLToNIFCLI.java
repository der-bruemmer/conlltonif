package org.nlp2rdf.implementation.conll;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import org.nlp2rdf.cli.ParameterException;
import org.nlp2rdf.cli.ParameterParser;
import org.nlp2rdf.core.Format;
import org.nlp2rdf.core.NIFNamespaces;
import org.nlp2rdf.core.NIFParameters;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import static java.util.Arrays.asList;

public class ConLLToNIFCLI {
	
	public static void main(String[] args) throws IOException {
    
		OptionParser parser = ParameterParser.getParser(args, "http://cli.nlp2rdf.org/conll#");
	  	ParameterParser.addCLIParameter(parser);
		parser.acceptsAll(asList("tagset"), "").withRequiredArg().ofType(String.class).describedAs("tagset name supported by olia");
		
	  	try {
	  		OptionSet options = ParameterParser.getOption(parser, args);
	  		ParameterParser.handleHelpAndWS(options, "");
	  		NIFParameters nifParameters = ParameterParser.parseOptions(options, false);
			//customize
//			OntModel model = nifParameters.getInputModel();
			ConLLToNIF converter = new ConLLToNIF();
			OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());   
			model.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
			NIFNamespaces.addNifPrefix(model);
			model.setNsPrefix("olia", "http://purl.org/olia/olia.owl#");
			model.setNsPrefix("p", nifParameters.getPrefix());
			
			converter.transform(model, model, nifParameters);
			if(!nifParameters.getOptions().has("outfile")) {
				model.write(System.out, Format.toJena(nifParameters.getOutputFormat()));
			} else {
				BufferedWriter writer = new BufferedWriter(new FileWriter(nifParameters.getOptions().valueOf("outfile").toString()));
				model.write(writer, Format.toJena(nifParameters.getOutputFormat()));
				writer.close();
			}	
		} catch (ParameterException e) {
	      ParameterParser.die(parser, e.getMessage());
	      // main script
		}
	      
		
	}
}
