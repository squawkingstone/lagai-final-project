import java.util.Random;
import java.util.Scanner;

/* This is kind of working. I'm getting in the board and and printing out the next move
 * and it's currently pretty shite. But I now have a thing that basically works. I should
 * probably break up stuff into other components (for parsing, and MCTS training and
 * move decision, etc) but I'll leave it for now.
 */

public class Bot {

    private Scanner scan = new Scanner(System.in);
    private Random rand = new Random();
    private String field[];

    public Bot()
    {
        field = new String[19 * 19];
    }

    public void run()
    {
        while (scan.hasNextLine())
        {
            String line = scan.nextLine();

            if (line.length() == 0) continue;

            String[] arg = line.split(" ");
            switch (arg[0])
            {
                case "settings":
                    break;
                case "update":
                    if (arg[1].equals("game") && arg[2].equals("field"))
                    {
                        field = arg[3].split(",").clone();
                    }
                    break;
                case "action":
                    for (int i = 0; i < field.length; i++)
                    {
                        if (field[i].equals("."))
                        {
                            System.out.println("place_move " + (i % 19) + " " + (i / 19));
                            System.err.println(i);
                            break;
                        }
                    }
                    break;
                default:
                    System.err.println("ERROR: Cmd not found \"" + line + "\"");
            }

        }
    }

    public static void main(String[] args) {
        (new Bot()).run();
    }
}
;