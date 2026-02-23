package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.GameException;
import ru.yandex.practicum.exceptions.GameOverException;
import ru.yandex.practicum.exceptions.InvalidWordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private Random random = new Random();

    @BeforeEach
    void setTest() {
        log = new PrintWriter(new StringWriter(), true);
        testWD = new WordleDictionary(testWords, log);
        testWG = new WordleGame(testWD, log);
        testWG.startNewGame();
    }

    @Test
    @DisplayName("Проверка, что словарь words_ru.txt корректного формата")
    void testLoadDictionaryCorrectFormat() throws IOException {
        WordleDictionaryLoader wdl = new WordleDictionaryLoader(new PrintWriter(log));
        for (String str : wdl.readWordleDictionary()) {
            assertEquals(5, str.length());
            assertFalse(str.contains("ё"));
        }
    }

    @Test
    @DisplayName("Проверка стартовых условий игры")
    void testStartNewGame() {
        testWG.startNewGame();
        assertFalse(testWG.getIsWin());
        assertEquals(6, testWG.getAttempts());
        assertTrue(testWords.contains(testWG.getAnswer()));
        assertTrue(testWG.getGuesses().isEmpty());
    }

    @Test
    @DisplayName("Проверка корректной обработки ввода")
    void testIsValidWord() {
        assertTrue(testWG.isValidWord(testWords.get(random.nextInt(testWords.size()))));
        assertFalse(testWG.isValidWord("аббат"));
        assertFalse(testWG.isValidWord("аббатство"));
    }

    @Test
    @DisplayName("Проверка форматирования ввода")
    void testNormalizeInputWord() {
        assertTrue(testWD.contains(testWG.normalizeInputWord("котёл")));
        assertTrue(testWD.contains(testWG.normalizeInputWord("ГОРОД")));
    }

    @Test
    @DisplayName("Проверка изменения переменных")
    void testMakeGuessValidWord() throws GameException {
        assertEquals(6, testWG.getAttempts());
        assertEquals(0, testWG.getAttemptsMade());
        assertEquals(0, testWG.getGuesses().size());
        testWG.setAnswer("лесок");
        testWG.makeGuess("город");
        assertEquals(5, testWG.getAttempts());
        assertEquals(1, testWG.getAttemptsMade());
        assertEquals(1, testWG.getGuesses().size());
        testWG.makeGuess("речка");
        assertEquals(4, testWG.getAttempts());
        assertEquals(2, testWG.getAttemptsMade());
        assertEquals(2, testWG.getGuesses().size());
    }

    @Test
    @DisplayName("Проверка условий победы")
    void testMakeGuessWin() throws GameException {
        assertFalse(testWG.getIsWin());
        testWG.setAnswer("город");
        assertEquals("+++++", testWG.makeGuess("город"));
        assertTrue(testWG.getIsWin());
    }

    @Test
    @DisplayName("Проверка окончания игры")
    void testMakeGuessGameOver() throws GameException {
        assertFalse(testWG.isFinished());
        testWG.setAnswer("город");
        for (int i = 0; i < 6; i++) {
            testWG.makeGuess("лесок");
        }
        assertTrue(testWG.isFinished());
        assertThrows(GameOverException.class, () -> testWG.makeGuess("речка"));
    }

    @Test
    @DisplayName("Проверка не учёта некорректного слова")
    void testMakeGuessInvalidWord() {
        assertEquals(6, testWG.getAttempts());
        assertThrows(InvalidWordException.class, () -> testWG.makeGuess("неверное"));
        assertEquals(6, testWG.getAttempts());
    }

    @Test
    @DisplayName("Проверка на корректный анализ слов")
    void testAnalyzeGuess() throws GameException {
        testWG.setAnswer("город");
        assertEquals("-+-+-", testWG.makeGuess("божок"));
        assertEquals("--^-^", testWG.makeGuess("авгур"));
        assertEquals("+++++", testWG.makeGuess("город"));
    }

    @Test
    @DisplayName("Проверка корректной работы подсказки")
    void testGetHintWithoutGuesses() {
        assertEquals(6, testWG.getAttempts());
        assertEquals(0, testWG.getGuesses().size());
        assertTrue(testWD.contains(testWG.getHint()));
        assertEquals(6, testWG.getAttempts());
    }

    @Test
    @DisplayName("Проверка корректной победы через подсказки")
    void testGetWinWithHint() throws GameException {
        int i = 0;

        assertEquals(6, testWG.getAttempts());
        assertEquals(i, testWG.getGuesses().size());
        assertFalse(testWG.isFinished());

        while (!testWG.getIsWin()) {
            testWG.makeGuessHint(testWG.getHint());
            i++;
        }

        assertTrue(testWG.isFinished());
        assertEquals(6, testWG.getAttempts());
        assertEquals(i, testWG.getGuesses().size());

        System.out.println(testWG.getGuesses());
    }
}
