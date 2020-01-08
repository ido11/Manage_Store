package presistence;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHandler {

    private final String DB_NAME = Repository.DB_NAME; //Database file name
    private final String CONN_URL = "jdbc:sqlite:"; //Database connection string
    private Connection conn = null; //Conenction to database

    public ConnectionHandler() {
        this.conn = Repository.conn;
        try {
            conn.setAutoCommit(true);
        } catch (SQLException s) {}
    }

    private void setConnection() throws SQLException
    {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        conn = DriverManager.getConnection(CONN_URL+DB_NAME,config.toProperties());
        conn.setAutoCommit(true);

    }


    public Connection connect()
    {
       return conn;
    }


    public void closeConnection()
    { }





}
