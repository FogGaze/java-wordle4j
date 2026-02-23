package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.GameException;
import ru.yandex.practicum.exceptions.GameOverException;
import ru.yandex.practicum.exceptions.InvalidWordException;

import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private String answer;
    private WordleDictionary dictionary;
    private int maxAttempts = 6;
    private int attemptsMade;
    private boolean isWin;
    private List<String> guesses;
    private List<String> results;
    private HashSet<String> possibleLetters = new HashSet<>();
    private HashSet<String> wrongLetters = new HashSet<>();
    private HashSet<String> possibleWords = new HashSet<>();
    private PrintWriter log;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
    }

    public void startNewGame() {
        answer = dictionary.getRandomWord();
        attemptsMade = 0;
        guesses = new ArrayList<>();
        results = new ArrayList<>();
        isWin = false;
        possibleWords = new HashSet<>(dictionary.getWordSet());
        wrongLetters = new HashSet<>();
        possibleLetters = new HashSet<>();

        log.println("Начата новая игра");
        log.println("Загадано слово: " + answer);
    }

    public String normalizeInputWord(String word) {
        return word.toLowerCase().replace("ё", "е");
    }

    public boolean isValidWord(String word) {
        return (word.length() == 5 && dictionary.contains(word));
    }

    public boolean isFinished() {
        return isWin || attemptsMade >= maxAttempts;
    }

    public String makeGuess(String word) throws GameException {
        if (isFinished()) {
            throw new GameOverException("Попытка ввода когда игра окончена");
        }

        word = normalizeInputWord(word);

        if (!isValidWord(word)) {
            throw new InvalidWordException("Слова нет в словаре, либо слово некорректной длины. Ввод: " + word);
        }
        attemptsMade++;
        guesses.add(word);
        String analyzeResult = analyzeGuess(word);
        results.add(analyzeResult);
        if (analyzeResult.equals("+++++")) {
            isWin = true;
        }
        log.println("Ввод пользователя: " + word);
        log.println("Проверка слова: " + analyzeResult);
        return analyzeResult;
    }

    public String makeGuessHint(String word) throws GameException {
        word = normalizeInputWord(word);

        if (!isValidWord(word)) {
            throw new InvalidWordException("Слова нет в словаре, либо слово некорректной длины. Ввод: " + word);
        }
        guesses.add(word);
        String analyzeResult = analyzeGuess(word);
        results.add(analyzeResult);
        if (analyzeResult.equals("+++++")) {
            isWin = true;
        }
        log.println("Введена подсказка: " + word);
        log.println("Проверка слова: " + analyzeResult);
        return analyzeResult;
    }

    public String analyzeGuess(String guess) {
        char[] result = {'-', '-', '-', '-', '-',};
        StringBuilder sbAnswer = new StringBuilder(answer);

        for (int i = 0; i < answer.length(); i++) {
            if (sbAnswer.charAt(i) == guess.charAt(i)) {
                result[i] = '+';
                possibleLetters.add(String.valueOf(guess.charAt(i)));
                sbAnswer.setCharAt(i, '*');
            }
        }
        for (int i = 0; i < answer.length(); i++) {
            if (result[i] != '+') {
                char g = guess.charAt(i);
                for (int j = 0; j < answer.length(); j++) {
                    if (sbAnswer.charAt(j) == g) {
                        result[i] = '^';
                        possibleLetters.add(String.valueOf(g));
                        sbAnswer.setCharAt(j, '*');
                    }
                }
            }
        }
        for (int i = 0; i < answer.length(); i++) {
            if (result[i] == '-') {
                if (!possibleLetters.contains(String.valueOf(guess.charAt(i)))) {
                    wrongLetters.add(String.valueOf(guess.charAt(i)));
                }
            }
        }
        return new String(result);
    }

    public String getHint() {
        String hint;

        if (guesses.isEmpty()) {
            hint = dictionary.getRandomWord();
            return hint;
        }

        for (String word : new ArrayList<>(possibleWords)) {
            for (String letter : wrongLetters) {
                if (word.contains(letter)) {
                    possibleWords.remove(word);
                    break;
                }
            }
        }

        for (String word : new ArrayList<>(possibleWords)) {
            for (String letter : possibleLetters) {
                if (!word.contains(letter)) {
                    possibleWords.remove(word);
                }
            }
        }

        possibleWords.removeAll(guesses);

        if (possibleWords.isEmpty()) {
            return hint = dictionary.getRandomWord();
        }

        List<String> list = new ArrayList<>(possibleWords);
        Random random = new Random();
        hint = list.get(random.nextInt(list.size()));
        return hint;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean getIsWin() {
        return isWin;
    }

    public int getAttempts() {
        return maxAttempts - attemptsMade;
    }

    public List<String> getGuesses() {
        return guesses;
    }

    public int getAttemptsMade() {
        return attemptsMade;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
