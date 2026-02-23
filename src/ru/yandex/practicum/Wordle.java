package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {
    public static void main(String[] args) {

        try (PrintWriter log = new PrintWriter(new FileWriter("gameLog.txt", StandardCharsets.UTF_8, true))) {

            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary;
            try {
                dictionary = loader.loadWordleDictionary();
            } catch (DictionaryLoadException e) {
                e.printStackTrace(log);
                return;
            }

            WordleGame game = new WordleGame(dictionary, log);
            game.startNewGame();
            printHello();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();

                try {

                    if (game.isFinished()) {
                        if (game.getAttemptsMade() >= 6) {
                            log.println("Пользователь исчерпал попытки. Игра окончена");
                            System.out.println("Попытки кончились. Выберете следующее действие");
                        }
                        if (input.equals("1") || input.equals("2")) {
                            if (input.equals("1")) {
                                game.startNewGame();
                                printHello();
                            } else {
                                System.out.println("Загаданное слово было: " + game.getAnswer());
                                System.out.println("До свидания!");
                                return;
                            }
                        } else {
                            System.out.println("Если хотите начать сначала введите - 1");
                            System.out.println("Если хотите закончить игру и выйти введите - 2");
                        }

                    } else {
                        if (!input.isEmpty()) {
                            System.out.println(game.makeGuess(input));
                        } else {
                            String hint = game.getHint();
                            System.out.println(hint);
                            System.out.println(game.makeGuessHint(hint));
                        }
                        System.out.println("Осталось попыток: " + game.getAttempts());
                        log.println("Осталось попыток: " + game.getAttempts());
                    }
                    if (game.getIsWin()) {
                        log.println("Пользователь ответил верно");
                        System.out.println("Поздравляем! Вы угадали слово!");
                        System.out.println("Если хотите начать сначала введите - 1");
                        System.out.println("Если хотите закончить игру и выйти введите - 2");
                    }

                } catch (InvalidWordException e) {
                    log.println(e.getMessage());
                    System.out.println("Введите другое слово из 5 букв");
                } catch (GameOverException e) {
                    log.println(e.getMessage());
                } catch (GameException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось создать лог-файл: " + e.getMessage());
        }
    }

    public static void printHello() {
        System.out.println("Мы начинаем игру Wordle");
        System.out.println("Вам необходимо отгадать слово из 5 букв за 6 попыток");
        System.out.println("Если введенное слово не будет соответствовать правилам игры, то попытка потрачена не будет");
        System.out.println("Для получения подсказки, нажмите Enter");
    }
}