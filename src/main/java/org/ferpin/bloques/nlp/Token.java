package org.ferpin.bloques.nlp;

public class Token {
    private String word;
    private String posTag;

    public Token(String word, String posTag) {
        this.word = word;
        this.posTag = posTag;
    }

    public Token(String word) {
        this.word = word;
    }

    public Token() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    @Override
    public String toString() {
        return word + " (" + posTag + ")";
    }
}
