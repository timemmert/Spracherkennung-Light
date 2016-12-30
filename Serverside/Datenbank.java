import java.sql.*;

public class Datenbank {

    //Attribut: Verbindung zur Datenbank
    Connection connection;

    //Konstruktor
    public Datenbank() {

    }


    /**
     * Verbindung zur Datenbank herstellen.
     */
    public void connect() {
        // Treiber laden. Muss im Klassenpfad sein (im gleichen Ordner wie der Projektordner). Hier fuer eine eingebettete Datenbank!
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        } catch (Exception ex) {
            System.out.println("Der JDBC-Treiber konnte nicht "
                + "geladen werden.");
            //System.exit(1);
        }
        /**
         *  Verbindung herstellen. Erstellt beim ersten Aufruf eine neue Datenbank (hier z. B.: "patientendaten")
         *  Um eine neue Datenbank anzulegen, muss nur ein neuer Name (fuer "AdresslisteQ11") eingegeben werden!
         */
        try {
            connection = DriverManager.getConnection("jdbc:derby:Wortliste;create=true");
        } catch (SQLException ex) {
            System.out.println("Die Verbindung zur Datenbank konnte "
                + "nicht hergestellt werden. "
                + "Die Fehlermeldung lautet: " + ex.getMessage());
            //System.exit(1);
        }
    }

    /**
     * Verbindung trennen
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                //connection = null;
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            }
        } catch (SQLException ex) {
            System.out.println("Die Verbindung zur Datenbank "
                + "konnte nicht geschlossen werden. "
                + "Die Fehlermeldung lautet: " + ex.getMessage());
            //System.exit(1);
        }
    }

    //Tabellen mit Beispielwerten in die Datenbank einfuegen
    public void initTables() {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT id FROM words");
            statement.close();
        } catch (SQLException e) {
           
            try {
                Statement statement = connection.createStatement();
                statement.execute("CREATE TABLE words (id INTEGER GENERATED ALWAYS AS IDENTITY,wort VARCHAR(100),PRIMARY KEY(id))");
                //statement.execute("CREATE TABLE albums (id INTEGER GENERATED ALWAYS AS IDENTITY, title VARCHAR(100), tracks VARCHAR(10), cds VARCHAR(10), yearesultSet VARCHAR(10), genre INTEGER, PRIMARY KEY(id), FOREIGN KEY(genre) REFERENCES adressen (id))");

                
                statement.close();
            } catch (SQLException ex) {
                System.out.println("Die Fehlermeldung lautet: " + ex.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Alle Tabellen anzeigen
     */
    public void showTables() {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Benoetigte Ressourcen fuer eine SQL-Anweisung bereitstellen
            statement = connection.createStatement();
            // Select-Anweisung ausfuehren
            resultSet = statement.executeQuery("SELECT id,wort FROM words");

            System.out.println("Der Inhalt der Datenbank:");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1)+" "+resultSet.getString(2));
            }
        } catch (SQLException ex) {
            System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
        } finally {

            // Alle Ressourcen wieder freigeben
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
                }
            }
        }
    }
	
    public ResultSet getResultSet() {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Benoetigte Ressourcen fuer eine SQL-Anweisung bereitstellen
            statement = connection.createStatement();
            // Select-Anweisung ausfuehren
            resultSet = statement.executeQuery("SELECT id,wort FROM words");

        } catch (SQLException ex) {
            System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
        } 
        return resultSet;
    }


    public void abfrageAusfuehren() {
        connect();
        initTables();
        showTables();
        //disconnect();
    }
	public int getLength()
	{
		
		Statement statement;
		try{
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = statement.executeQuery("SELECT COUNT(*) AS length FROM words");	
			if (rs.next()) {
				return rs.getInt("length");
			}
		return 0;
			
		}
		catch(SQLException q)
		{
			System.out.println("Exception! "+q.getMessage());
			return 0;
		}
	}
	
	public boolean inTabel(String s)
	{
		Statement statement;
		try{
		statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT wort FROM words WHERE wort= '"+s+"'");	
		if(rs.next())
		{
			rs.close();
			return true;
		}
			
		else 
		{
			rs.close();
			return false;
		}
			
		}
		catch(SQLException q)
		{
			System.out.println("Exception! "+q.getMessage());
			return false;
		}
		
		
	}
    public void update(int id, String wort) {
        PreparedStatement statement = null;
        try{
            statement = connection.prepareStatement("UPDATE words SET wort=? WHERE id=?");
            statement.setString(1, wort);
            statement.setInt(2, id);
            statement.execute();
            showTables();
        } catch (SQLException ex) {
            System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
                }
            }
        }

    }

    public void insert(String wort) {
        //PreparedStatement statement = connection.prepareStatement("INSERT INTO adressen (name, vorname, strasse, plz, Ort) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement statement = null;
        //Statement statement = null;
        ResultSet resultSet = null;
        try {
            //    statement = connection.createStatement();
            //    statement.execute("INSERT INTO adressen (name) VALUES('" + pat.getName() + "')", Statement.RETURN_GENERATED_KEYS);
            statement = connection.prepareStatement("INSERT INTO words (wort) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, wort);
            statement.execute();

            showTables();
            //             resultSet = statement.getGeneratedKeys();
            //             resultSet.next();
            //             pat.setId(resultSet.getInt(1));
        } catch (SQLException ex) {
            System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
        } finally {

            // Alle Ressourcen wieder freigeben
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
                }
            }
        }
    }

    public void delete(String wort) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("DELETE FROM words WHERE wort = ?");
            statement.setString(1, wort); //pat.getId());
            statement.execute();
            showTables();
        } catch (SQLException ex) {
            System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    System.out.println("Datenbankfehler. Die Fehlermeldung lautet: " + ex.getMessage());
                }
            }
        }
    }

}
