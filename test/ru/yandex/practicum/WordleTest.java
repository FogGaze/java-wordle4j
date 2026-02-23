package ru.yandex.practicum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private WordleGame testWG;
    private WordleDictionary testWD;
    private PrintWriter log;
    private final List<String> testWords = new ArrayList<>(List.of("котел", "домен", "лесок", "город", "речка", "божок", "авгур"));

    @BeforeEach
    void setTest() {
        log = new PrintWriter(new StringWriter(), true);
        testWD = new WordleDictionary(testWords, log);
        testWG = new WordleGame(testWD, log);
        testWG.startNewGame();
    }

    //Проверка, что словарь words_ru.txt корректного формата
    @Test
    void testLoadDictionaryCorrectFormat() throws IOException {
        WordleDictionaryLoader wdl = new WordleDictionaryLoader(new PrintWriter(log));
        for (String str : wdl.readWordleDictionary()) {
            assertEquals(5, str.length());
            Assertions.assertFalse(str.contains("ё"));
        }
    }

    //Проверка стартовых условий игры
    @Test
    void testStartNewGame() {
        testWG.startNewGame();
        Assertions.assertFalse(testWG.getIsWin());
        Assertions.assertEquals(6, testWG.getAttempts());
        Assertions.assertTrue(testWords.contains(testWG.getAnswer()));
        Assertions.assertTrue(testWG.getGuesses().isEmpty());
    }

    @Test
    void testIsValidWord() {
        Random random = new Random();
        Assertions.assertTrue(testWG.isValidWord(testWords.get(random.nextInt(testWords.size()))));
        Assertions.assertFalse(testWG.isValidWord("аббат"));
        Assertions.assertFalse(testWG.isValidWord("аббатство"));
    }

    @Test //Тест на проверку работы метода
    void testNormalizeInputWord() {
        Assertions.assertTrue(testWD.contains(testWG.normalizeInputWord("котёл")));
        Assertions.assertTrue(testWD.contains(testWG.normalizeInputWord("ГОРОД")));
    }

    //тест на проверку изменения переменных
    @Test
    void testMakeGuessValidWord() throws GameException {
        Assertions.assertEquals(6, testWG.getAttempts());
        Assertions.assertEquals(0, testWG.getAttemptsMade());
        Assertions.assertEquals(0, testWG.getGuesses().size());
        testWG.setAnswer("лесок");
        testWG.makeGuess("город");
        Assertions.assertEquals(5, testWG.getAttempts());
        Assertions.assertEquals(1, testWG.getAttemptsMade());
        Assertions.assertEquals(1, testWG.getGuesses().size());
        testWG.makeGuess("речка");
        Assertions.assertEquals(4, testWG.getAttempts());
        Assertions.assertEquals(2, testWG.getAttemptsMade());
        Assertions.assertEquals(2, testWG.getGuesses().size());
    }

    //тест на проверку победы
    @Test
    void testMakeGuessWin() throws GameException {
        Assertions.assertFalse(testWG.getIsWin());
        testWG.setAnswer("город");
        Assertions.assertEquals("+++++", testWG.makeGuess("город"));
        Assertions.assertTrue(testWG.getIsWin());
    }

    //Тест на окончание игры
    @Test
    void testMakeGuessGameOver() throws GameException {
        assertFalse(testWG.isFinished());
        testWG.setAnswer("город");
        for (int i = 0; i < 6; i++) {
            testWG.makeGuess("лесок");
        }
        assertTrue(testWG.isFinished());
        assertThrows(GameOverException.class, () -> testWG.makeGuess("речка"));
    }

    // тест на некорректное слово
    @Test
    void testMakeGuessInvalidWord() {
        Assertions.assertEquals(6, testWG.getAttempts());
        assertThrows(InvalidWordException.class, () -> testWG.makeGuess("неверное"));
        Assertions.assertEquals(6, testWG.getAttempts());
    }

    //тест на корректный анализ слов
    @Test
    void testAnalyzeGuess() throws GameException {
        testWG.setAnswer("город");
        Assertions.assertEquals("-+-+-", testWG.makeGuess("божок"));
        Assertions.assertEquals("--^-^", testWG.makeGuess("авгур"));
        Assertions.assertEquals("+++++", testWG.makeGuess("город"));
    }

    // тест на проверку слова из подсказки
    @Test
    void testGetHintWithoutGuesses() {
        Assertions.assertEquals(6, testWG.getAttempts());
        Assertions.assertEquals(0, testWG.getGuesses().size());
        assertTrue(testWD.contains(testWG.getHint()));
        Assertions.assertEquals(6, testWG.getAttempts());
    }

    // тест на победу подсказки
    @Test
    void testGetWinWithHint() throws GameException {
        int i = 0;

        Assertions.assertEquals(6, testWG.getAttempts());
        Assertions.assertEquals(i, testWG.getGuesses().size());
        assertFalse(testWG.isFinished());

        while (!testWG.getIsWin()) {
            testWG.makeGuessHint(testWG.getHint());
            i++;
        }

        assertTrue(testWG.isFinished());
        Assertions.assertEquals(6, testWG.getAttempts());
        Assertions.assertEquals(i, testWG.getGuesses().size());

        System.out.println(testWG.getGuesses());
    }
}
