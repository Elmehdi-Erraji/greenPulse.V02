import config.Database;

import java.sql.Connection;
import java.sql.SQLException;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {



    public static void main(String[] args) {

        try {
            Database database = Database.getInstance();

            database.getConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Database database = Database.getInstance();

            database.getConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
