package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;


public class Repl {
    private static String RESET;
    Repl(){
        RESET = RESET_BG_COLOR + RESET_TEXT_COLOR;
    }
    public void start(){
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();

            try {
                result = client.eval(input);
                System.out.print(result);
            } catch (Throwable e){
                var msg = e.getMessage();
                System.out.print(SET_TEXT_COLOR_RED + msg);
            }
        }
    }

    private void loggedIn(){

    }

    private void printPrompt(){
        System.out.print("\n" + RESET + ">>>" + SET_TEXT_COLOR_GREEN);
    }

}
