package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private List<String> words;
    private HashSet<String> wordSet;
    private PrintWriter log;

    Random random = new Random();

    public WordleDictionary(List<String> words, PrintWriter log) {
        this.words = words;
        this.wordSet = new HashSet<>(words);
        this.log = log;
    }

    public boolean contains(String word) {
        return wordSet.contains(word);
    }

    public String getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }

    public HashSet<String> getWordSet() {
        return wordSet;
    }

}
