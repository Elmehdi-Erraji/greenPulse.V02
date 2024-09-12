import views.MainMenu;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        MainMenu.displayMainMenu(scanner);

        scanner.close();
    }
}
