package org.nlp2rdf.tools.corpus.conll;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.nlp2rdf.core.NIFParameters;
import org.nlp2rdf.core.SPARQLValidator;
import org.nlp2rdf.core.urischemes.URIScheme;
import org.nlp2rdf.core.vocab.NIFDatatypeProperties;
import org.nlp2rdf.core.vocab.NIFObjectProperties;
import org.nlp2rdf.core.vocab.NIFOntClasses;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.ResourceUtils;

/**
 * Converter to transform ConLL dependency format into NIF
 * 
 * TODO: How to map the different Tagsets
 * TODO: import NIF model
 * @author Martin Bruemmer
 * This software is part of the nlp2rdf project. Find out more at nlp2rdf.org
 *
 */

public class ConLLToNIF {
	
	private String tempSentence = "";
	private String prefix = "";
	
	public ConLLToNIF(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Central method of the class, reading the corpus file and filling an OntModel for output
	 * 
	 * TODO: parse Tree
	 * 
	 * @param fileIn
	 * @param inputModel
	 * @param outputModel
	 */
	
	public void transformConLL(String fileIn, OntModel inputModel, OntModel outputModel){
	
		BufferedReader reader = null; 
		
		try {
			reader = new BufferedReader(new FileReader(fileIn));

			String line;
			List<String> sentence = new ArrayList<String>();
			int offset = 0;
			List<ConLLWord> wordObjectsOfSentence = new ArrayList<ConLLWord>();
			String contextString = "";
		//adding the context resource here
			//this is crude
			String uri = this.prefix + fileIn.substring(fileIn.lastIndexOf("/")+1)+"#char=0,";
			//only supporting RFC5147 string atm
			Individual contextResource = outputModel.createIndividual(uri, outputModel.createClass(NIFOntClasses.RFC5147String.getUri()));
      contextResource.addOntClass(NIFOntClasses.Context.getOntClass(outputModel));
      contextResource.addOntClass(NIFOntClasses.String.getOntClass(outputModel));
      contextResource.addProperty(NIFDatatypeProperties.beginIndex.getDatatypeProperty(outputModel), "0");
      
			while((line = reader.readLine()) != null) {
				if(!line.isEmpty()) {
					sentence.add(line);
				}
				else {
	
					if(!sentence.isEmpty()) {
						wordObjectsOfSentence = this.transformSentenceToObjects(sentence, offset);
						
						Individual sentenceResource = this.addSentenceResourceToModel(outputModel, tempSentence+"\n", offset, contextResource);
						contextString+=tempSentence+"\n";
						this.tempSentence = "";
						//getting the endOffset from the last word of the sentence and adding 1 to account for newline
						offset = wordObjectsOfSentence.get(wordObjectsOfSentence.size()-1).getEnd()+1;
						
						//maybe there should be a jena model where this method just adds phrases to
						this.addWordResourcesToModel(outputModel, wordObjectsOfSentence, sentenceResource, contextResource);
						this.parseDependencyTree(wordObjectsOfSentence, outputModel, contextResource);
					}
					sentence = new ArrayList<String>();
				}
			}
//			this.addContextResourceToModel(outputModel, contextString);
		//finish context resource here
			contextResource.addProperty(NIFDatatypeProperties.endIndex.getDatatypeProperty(outputModel), offset+"");
			contextResource.addLiteral(NIFDatatypeProperties.isString.getDatatypeProperty(outputModel), outputModel.createLiteral(contextString));
			ResourceUtils.renameResource(contextResource, contextResource.getURI()+offset);
			
			reader.close();
		} catch(FileNotFoundException fnf) {
			System.out.println("File not found "+fileIn);
		} catch(IOException ioe) {
			System.out.println("Could not read file "+fileIn);
		}

		return;
	}
	
	private void addWordResourcesToModel(OntModel outputModel, List<ConLLWord> wordObjectsOfSentence, Individual sentenceResource, Individual contextResource) {
	 for(ConLLWord word : wordObjectsOfSentence) {
		 String uri = contextResource.getURI().substring(0,contextResource.getURI().lastIndexOf("=")+1)+word.getStart()+","+word.getEnd();
		 Individual wordResource = outputModel.createIndividual(uri, outputModel.createClass(NIFOntClasses.RFC5147String.getUri()));
		 wordResource.addOntClass(NIFOntClasses.Word.getOntClass(outputModel));
		 wordResource.addOntClass(NIFOntClasses.String.getOntClass(outputModel));
		 wordResource.addProperty(NIFDatatypeProperties.beginIndex.getDatatypeProperty(outputModel), word.getStart()+"");
		 wordResource.addProperty(NIFDatatypeProperties.endIndex.getDatatypeProperty(outputModel), word.getEnd()+"");
		 wordResource.addLiteral(NIFDatatypeProperties.anchorOf.getDatatypeProperty(outputModel), outputModel.createLiteral(word.getWordString()));
		 wordResource.addProperty(NIFDatatypeProperties.posTag.getDatatypeProperty(outputModel), word.getPos());
		 //TODO: we may need something different here
		 wordResource.addProperty(NIFDatatypeProperties.posTag.getDatatypeProperty(outputModel), word.getPosFine());
		 if(!word.getLemma().equals("_"))
			 wordResource.addProperty(NIFDatatypeProperties.lemma.getDatatypeProperty(outputModel), word.getLemma());
		 //TODO: add genus, numerus etc here
		 wordResource.addProperty(NIFObjectProperties.sentence.getObjectProperty(outputModel), sentenceResource.getURI());
		 sentenceResource.addProperty(NIFObjectProperties.word.getObjectProperty(outputModel), wordResource.getURI());
	 }
	  
  }

	private Individual addSentenceResourceToModel(OntModel outputModel, String sentence, int startOffset, Individual context) {
		int endOffset = startOffset+sentence.length();
		String uri = context.getURI().substring(0,context.getURI().lastIndexOf("=")+1)+startOffset+","+endOffset;
		//only supporting RFC5147 string atm
		Individual sentenceResource = outputModel.createIndividual(uri, outputModel.createClass(NIFOntClasses.RFC5147String.getUri()));
		sentenceResource.addOntClass(NIFOntClasses.Sentence.getOntClass(outputModel));
		sentenceResource.addOntClass(NIFOntClasses.String.getOntClass(outputModel));
		sentenceResource.addProperty(NIFDatatypeProperties.beginIndex.getDatatypeProperty(outputModel), startOffset+"");
		sentenceResource.addProperty(NIFDatatypeProperties.endIndex.getDatatypeProperty(outputModel), endOffset+"");
		sentenceResource.addLiteral(NIFDatatypeProperties.anchorOf.getDatatypeProperty(outputModel), outputModel.createLiteral(sentence));
		return sentenceResource;
  }
	
	/**
	 * Parse a dependency tree from generated objects
	 * TODO: implement this thing
	 * @param sentenceObjects
	 */

	private void parseDependencyTree(List<ConLLWord> sentenceObjects, OntModel inputModel, Individual context) {
		//create the tree
		for(ConLLWord word : sentenceObjects) {
			//test output
			
		}
	}
	
	/**
	 * Parse a sentence into a list of ConLLWord objects
	 * @param sentence list of line strings from the ConLL file 
	 * @return
	 */

	private List<ConLLWord> transformSentenceToObjects(List<String> sentence, int startOffset) {

		String sentenceString = "";
		List<ConLLWord> sentenceObjects = new ArrayList<ConLLWord>();
		int offset = startOffset;
		
		boolean addSpaceToOffset = false;
		for(String line : sentence) {
		
			ConLLWord word = getWordFromCorpusLine(line);
			
			//TODO: check if word is punctuation class word instead of this static check
			if(word.getWordString().matches("[\\.!?,;:)]")) {
				//remove whitespace before punctuation mark annd decrement offset
				if(sentenceString.endsWith(" ")) {
					sentenceString=sentenceString.substring(0,sentenceString.length()-1);
					offset = offset-1;
				}
				
				sentenceString+=word.getWordString();			
		  //no space here
			} else if(word.getWordString().matches("[(]")) {			
				sentenceString+=word.getWordString();	
		  //add a space after the word
			} else {			
				sentenceString+=word.getWordString()+" ";
				addSpaceToOffset = true;
			}
			
			word.setStart(offset);
			int wordEnd = offset+word.getWordString().length();
			word.setEnd(wordEnd);
			offset = wordEnd;
			if(addSpaceToOffset) {
				offset++;
				addSpaceToOffset = false;
			}
			
			sentenceObjects.add(word);
		}
		this.tempSentence = sentenceString.trim();
		
		//return the length of the sentence added to the offset
		return sentenceObjects;
	}
	
	private ConLLWord getWordFromCorpusLine(String line) {
		ConLLWord word = new ConLLWord();
		
		String[] conllFields = line.split(" ");
		word.setWordId(Integer.parseInt(conllFields[0]));
		word.setWordString(conllFields[1]);
		word.setLemma(conllFields[2]);
		word.setPos(conllFields[3]);
		word.setPosFine(conllFields[4]);
		word.setMorphs(conllFields[5]);
		word.setPhraseHeadId(Integer.parseInt(conllFields[6]));
		word.setPhraseType(conllFields[7]);
		return word;
	}
	
}
