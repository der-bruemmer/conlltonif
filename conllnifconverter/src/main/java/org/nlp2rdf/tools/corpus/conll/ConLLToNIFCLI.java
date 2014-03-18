package org.nlp2rdf.tools.corpus.conll;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.nlp2rdf.core.NIFNamespaces;


import java.io.IOException;

public class ConLLToNIFCLI {
	
	public static void main(String[] args) throws IOException {
    
		ConLLToNIF converter = new ConLLToNIF(args[0]);
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());     
		converter.transformConLL(args[1], model, model);

    model.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
    NIFNamespaces.addNifPrefix(model);
    model.setNsPrefix("olia", "http://purl.org/olia/olia.owl#");
        model.write(System.out, "TURTLE");
	}
}
