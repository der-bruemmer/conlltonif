package org.nlp2rdf.tools.corpus.conll;

public class ConLLWord {

	private int wordId = 0;
	private String wordString = "";
	private int start = 0;
	private int end = 0;
	private String pos = "";
	private String posFine = "";
	private String entity = "";
	private String lemma = "";
	private String morphs = "";
	private int phraseHeadId = 0;
	private String phraseType = "";
	
	
	public ConLLWord() {
		
	}

	public int getWordId() {
		return wordId;
	}
	
	public void setWordId(int wordId) {
		this.wordId = wordId;
	}

	public String getPosFine() {
		return posFine;
	}

	public void setPosFine(String posFine) {
		this.posFine = posFine;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getMorphs() {
		return morphs;
	}

	public void setMorphs(String morphs) {
		this.morphs = morphs;
	}

	public int getPhraseHeadId() {
		return phraseHeadId;
	}

	public void setPhraseHeadId(int phraseHeadId) {
		this.phraseHeadId = phraseHeadId;
	}

	public String getPhraseType() {
		return phraseType;
	}

	public void setPhraseType(String phraseType) {
		this.phraseType = phraseType;
	}

	public String getWordString() {
		return wordString;
	}
	
	public void setWordString(String wordString) {
		this.wordString = wordString;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public String getPos() {
		return pos;
	}
	
	public void setPos(String pos) {
		this.pos = pos;
	}
	
	public String getEntity() {
		return entity;
	}
	
	public void setEntity(String entity) {
		this.entity = entity;
	}
}
