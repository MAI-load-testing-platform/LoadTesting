package org.ka1amoor;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class JtlImporter {

    private static final String JDBC_URL =
            "jdbc:postgresql://localhost:5431/jmeter";
    private static final String USER = "jmeter";
    private static final String PASSWORD = "jmeter";

    private static final String SQL = """
        INSERT INTO jmeter_result (
            time_stamp, elapsed, label, response_code, response_message,
            thread_name, data_type, success, failure_message,
            bytes, sent_bytes, grp_threads, all_threads, url,
            latency, idle_time, connect
        ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """;

    public static void importJtl(Path jtl) throws Exception {

        try (
                Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                CSVReader reader = new CSVReader(new FileReader(jtl.toFile()));
                PreparedStatement ps = conn.prepareStatement(SQL)
        ) {

            String[] row;
            reader.readNext(); // skip header

            int batch = 0;

            while ((row = reader.readNext()) != null) {

                ps.setLong(1, Long.parseLong(row[0]));
                ps.setInt(2, Integer.parseInt(row[1]));
                ps.setString(3, row[2]);
                ps.setString(4, row[3]);
                ps.setString(5, row[4]);
                ps.setString(6, row[5]);
                ps.setString(7, row[6]);
                ps.setBoolean(8, Boolean.parseBoolean(row[7]));
                ps.setString(9, row[8]);
                ps.setLong(10, Long.parseLong(row[9]));
                ps.setLong(11, Long.parseLong(row[10]));
                ps.setInt(12, Integer.parseInt(row[11]));
                ps.setInt(13, Integer.parseInt(row[12]));
                ps.setString(14, row[13]);
                ps.setInt(15, Integer.parseInt(row[14]));
                ps.setInt(16, Integer.parseInt(row[15]));
                ps.setInt(17, Integer.parseInt(row[16]));

                ps.addBatch();

                if (++batch % 1000 == 0) {
                    ps.executeBatch();
                }
            }

            ps.executeBatch();
        }

        System.out.println("JTL imported to Postgres ");
    }
}
