package flashcards;
import java.io.*;
import java.util.*;

public class Main {
    public static Log log = new Log(new ArrayList<>());
    public static Map<String, String> map = new LinkedHashMap<>();
    public static Map<String, Integer> hardestTerm = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException {

        int times = 0;
        String logFileName = "";
        String importFile = "";
        String exportFile = "";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-export")) {
                exportFile = args[i + 1];
            } else if (args[i].equals("-import")) {
                importFile = args[i + 1];
                importFile(importFile);
            }
        }

        while (true) {
            log.outputMsg("Input the action " +
                "(add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            switch (log.getStringInput()) {
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "import":
                    log.outputMsg("File name:");
                    importFile(log.getStringInput());
                    break;
                case "export":
                    log.outputMsg("File name:");
                    exportFile(log.getStringInput());
                    break;
                case "ask":
                    log.outputMsg("How many times to ask?");
                    times = Integer.parseInt(log.getStringInput());
                    for (int i = 0; i < times; i++) {
                        ask();
                    }
                    break;
                case "exit":
                    log.outputMsg("Bye bye!");
                    if (!exportFile.equals("")) {
                        exportFile(exportFile);
                    }
                    System.exit(0);
                    break;
                case "log":
                    log.outputMsg("File name:");
                    logFileName = log.getStringInput();
                    log.saveLog(logFileName);
                    break;
                case "hardest card":
                    log.outputMsg(hardestCards());
                    break;
                case "reset stats":
                    hardestTerm.replaceAll((i, v) -> 0);
                    log.outputMsg("Card statistics have been reset.");
                    break;
                default:
                    break;
            }
        }
    }

    public static void addCard() {
        String key = "";
        String value = "";

        log.outputMsg("The card:");
        key = log.getStringInput();

        if (map.containsKey(key)) {
            log.outputMsg("The card \"" + key + "\" already exists.");
            return;
        }

        log.outputMsg("The definition of the card:");
        value = log.getStringInput();

        if (map.containsValue(value)) {
            log.outputMsg("The definition \"" + value + "\" already exists.");
        } else {
            map.put(key, value);
            log.outputMsg("The pair (\"" + key + "\":\"" + value + "\") has been added.");
        }
    }

    public static void removeCard() {
        log.outputMsg("Which card?");
        String card = log.getStringInput();
        if (map.remove(card) == null) {
            log.outputMsg("Can't remove \"" + card + "\": there is no such card.");
        } else {
            log.outputMsg("The card has been removed.");
        }

    }

    public static void importFile(String fileName) {
        int counter = 0;
        String line = "";
        String[] pair = new String[3];

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fileReader);
            line = br.readLine();

            while (line != null) {
                pair = line.split(":");
                map.put(pair[0], pair[1]);
                hardestTerm.put(pair[0], Integer.parseInt(pair[2]));
                counter++;
                line = br.readLine();
            }

            log.outputMsg(counter + " cards have been loaded.");
            fileReader.close();
            br.close();
        } catch (FileNotFoundException e) {
            log.outputMsg("File not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void ask() {
        Random r = new Random();
        int random = 0;
        String answer = "";
        String question = "";
        random = r.nextInt(map.size());
        int counter = 0;

        for (String i : map.keySet()) {
            if (counter == random) {
                question = i;
            }
            counter++;
        }

        log.outputMsg("Print the definition of \"" + question + "\":");
        answer = log.getStringInput();

        if (answer.equals(map.get(question))) {
            log.outputMsg("Correct!");
        } else if (map.containsValue(answer)) {
            for (var entry : map.entrySet()) {
                if (entry.getValue().equals(answer)) {
                    log.outputMsg("Wrong. The right answer is \"" + map.get(question) + "\"," +
                        " but your definition is correct for \"" + entry.getKey() + "\".");
                    if (hardestTerm.put(question, hardestTerm.getOrDefault(question, 0) + 1) != null) {
                        hardestTerm.replace(question, hardestTerm.get(question) + 1);
                    }
                }
            }
        } else {
            log.outputMsg("Wrong. The right answer is \"" + map.get(question) + "\".");
            hardestTerm.put(question, hardestTerm.getOrDefault(question, 0) + 1);
        }
    }

    public static void exportFile(String fileName) throws IOException {
        File file = new File(fileName);
        boolean yesOrNo = file.createNewFile();
        int counter = 0;

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (String i : map.keySet()) {
                printWriter.println(i + ":" + map.get(i) + ":" + hardestTerm.getOrDefault(i, 0));
                counter++;
            }
            log.outputMsg(counter + " cards have been saved.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String hardestCards() {
        List<String> hardestCards = new ArrayList<>(10);
        hardestCards.add(0, "bla");
        StringBuilder stringBuilder = new StringBuilder();
        int maximum = 0;

        for (String i : hardestTerm.keySet()) {
            if (hardestTerm.getOrDefault(i, 0) > maximum) {
                maximum = hardestTerm.get(i);
                hardestCards.set(0, i);
            } else if (hardestTerm.getOrDefault(i, 0) == maximum && hardestTerm.getOrDefault(i, 0) != 0) {
                if (!hardestCards.contains(i)) {
                    hardestCards.add(i);
                }
            }
        }

        if (maximum == 0) {
            stringBuilder.append("There are no cards with errors.");
        } else if (hardestCards.size() <= 1) {
            stringBuilder.append("The hardest card is \"").append(hardestCards.get(0)).append("\". You have ")
                .append(hardestTerm.get(hardestCards.get(0))).append(" errors answering it.");
        } else {
            stringBuilder.append("The hardest cards are ");
            for (int i = 0; i < hardestCards.size(); i++) {
                stringBuilder.append("\"").append(hardestCards.get(i)).append("\"");
                if (i != hardestCards.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(". You have ").append(hardestTerm.get(hardestCards.get(0))).append(" errors answering them.");
        }
        return stringBuilder.toString();
    }
}

