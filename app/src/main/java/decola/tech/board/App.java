/*
 * This source file was generated by the Gradle 'init' task
 */
package decola.tech.board;

import static decola.tech.board.persistence.config.ConnectionConfig.getConnection;

import java.sql.SQLException;

import decola.tech.board.persistence.migration.MigrationStrategy;
import decola.tech.board.ui.MainMenu;

public class App {

    public static void main(String[] args) throws SQLException {
        try (var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }
        new MainMenu().execute();
    }
}
