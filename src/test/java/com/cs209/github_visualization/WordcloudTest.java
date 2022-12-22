package com.cs209.github_visualization;

import com.cs209.github_visualization.utils.WordCloudUtil;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizer.api.WordTokenizer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;

import java.io.IOException;
import java.util.List;

public class WordcloudTest {


    public static void main(String[] args) throws IOException {
        System.out.println("init");
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setStopWords(WordCloudUtil.STOP_WORDS);
        System.out.println("start");
        long time = System.currentTimeMillis();
        List<WordFrequency> output = frequencyAnalyzer.load("src/test/java/com/cs209/github_visualization/test.txt");
        System.out.println("end");
        for (WordFrequency w : output) {
            System.out.println(w);
        }
        System.out.println(System.currentTimeMillis() - time);
    }
}
