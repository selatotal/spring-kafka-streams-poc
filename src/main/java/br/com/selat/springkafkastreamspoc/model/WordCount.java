package br.com.selat.springkafkastreamspoc.model;

import java.util.Date;

public class WordCount {

    public String word;
    private Long value;
    private Date start;
    private Date end;

    public WordCount() {
    }

    public WordCount(String key, Long value, Date start, Date end) {
        this.word = key;
        this.value = value;
        this.start = start;
        this.end = end;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
