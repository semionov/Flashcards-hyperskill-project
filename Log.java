package flashcards;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class Log {
    public static Scanner scanner = new Scanner(System.in);
    public List<String> log;

    public Log(List<String> log) {
        this.log = log;
    }

    public void outputMsg(String msg) {
        System.out.println(msg);
        log.add(msg);
    }

    public String getStringInput() {
        String input = scanner.nextLine();
        log.add(input);
        return input;
    }

    public void saveLog(String fileName) throws IOException {
        File file = new File(fileName);
        boolean yesOrNo = file.createNewFile();

        outputMsg("The log has been saved.");
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.println("");
            for (String i : log) {
                printWriter.println(i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
