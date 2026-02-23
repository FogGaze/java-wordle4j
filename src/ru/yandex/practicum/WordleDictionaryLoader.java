package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.DictionaryLoadException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public List<String> readWordleDictionary() throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader("words_ru.txt", StandardCharsets.UTF_8))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                line = line.toLowerCase().replace("ё", "е");
                if (line.length() == 5) {
                    words.add(line);
                }
            }
        }
        return words;
    }

    public WordleDictionary loadWordleDictionary() throws DictionaryLoadException {
        try {
            List<String> words = readWordleDictionary();
            if (words.isEmpty()) {
                log.println("Ошибка загрузки словаря. Словарь пуст");
                throw new DictionaryLoadException("Словарь пуст");
            }
            return new WordleDictionary(words, log);
        } catch (IOException e) {
            log.println("Ошибка загрузки словаря");
            throw new DictionaryLoadException("Ошибка загрузки словаря: " + e.getMessage(), e);
        }
    }
}
