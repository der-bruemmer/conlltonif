package org.nlp2rdf.ConLLToNIF;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	public ConLLToNIF() {
		
	}
	
	/**
	 * Central method of the class, reading the corpus file and starting the writing process
	 * This has no output at the moment but just finishes the process by itself (reading, writing)
	 * Maybe it would be interesting to make it stream based? just call something like "getNextSentence()" that returns the resources for the next sentence?
	 * 
	 * TODO: parse Tree
	 * TODO: write Model
	 * 
	 * @param fileIn
	 * @param fileOut
	 */
	
	public void transformConLL(String fileIn, String fileOut){
		
		BufferedReader reader = null; 
		
		try {
			reader = new BufferedReader(new FileReader(fileIn));
			
			String line;
			List<String> sentence = new ArrayList<String>();
			int offset = 0;
			List<ConLLWord> sentenceObjects = new ArrayList<ConLLWord>();
		    while((line = reader.readLine()) != null) {
		    	if(!line.isEmpty()) {
		    		sentence.add(line);
		    	}
		    	else {
		    		sentenceObjects = this.transformSentenceToObjects(sentence, offset);
		    		//getting the endOffset from the last word of the sentence and adding 1 to account for space
		    		offset = sentenceObjects.get(sentenceObjects.size()-1).getEnd()+1;
		    		//maybe there should be a jena model where this method just adds phrases to
		    		this.parseDependencyTree(sentenceObjects);
		    		//write the resulting model
		    		this.writeModel(fileOut);
		    	}
		    }
		    reader.close();
		} catch(FileNotFoundException fnf) {
			System.out.println("File not found "+fileIn);
		} catch(IOException ioe) {
			System.out.println("Could not read file "+fileIn);
		} 
		
		return;
	}
	
	/**
	 * Write the generated Model to a NIF file in the desired format
	 * @param fileOut
	 */
		
	private void writeModel(String fileOut) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Parse a dependency tree from generated objects
	 * @param sentenceObjects
	 */

	private void parseDependencyTree(List<ConLLWord> sentenceObjects) {
		//create the tree
		
	}
	
	/**
	 * Parse a sentence into a list of ConLLWord objects
	 * @param sentence list of line strings from the ConLL file 
	 * @param startOffset start offset of the sentence in context to the document
	 * @return
	 */

	private List<ConLLWord> transformSentenceToObjects(List<String> sentence, int startOffset) {
		
		List<ConLLWord> sentenceObjects = new ArrayList<ConLLWord>();
		int offset = startOffset;
		for(String line : sentence) {
			ConLLWord word =  getWordFromCorpusLine(line);
			word.setStart(offset);
			int wordEnd = offset+word.getWordString().length();
			word.setEnd(wordEnd);
			
			offset = wordEnd;
			//add 1 to the offset to account for spaces
			//TODO: we have to check for punctuation class instances here, because there is no space BEFORE then
			offset ++;
			
			sentenceObjects.add(word);
		}
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
	
	public static void main(String[] args) throws Exception {
		ConLLToNIF conv = new ConLLToNIF();
		
		conv.transformConLL(args[0], args[1]);

	}
}
