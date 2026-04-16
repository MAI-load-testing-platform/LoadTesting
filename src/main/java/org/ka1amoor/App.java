package org.ka1amoor;

import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) throws Exception {

        String jmeterHome = System.getenv("JMETER_HOME");
        if (jmeterHome == null || jmeterHome.isBlank()) {
            throw new IllegalStateException("export JMETER_HOME first");
        }

        Path jmx = Path.of("src/main/resources/plans/SpikeLoad.jmx");
        Path resultsDir = Path.of("build/jmeter-results");

        Files.createDirectories(resultsDir);

        String timestamp = String.valueOf(System.currentTimeMillis());
        Path jtl = resultsDir.resolve("result-" + timestamp + ".jtl");
        Path html = resultsDir.resolve("html");

        // 🔴 УДАЛЯЕМ СТАРЫЙ JTL
        if (Files.exists(jtl)) {
            Files.delete(jtl);
        }

        JMeterEmbedded.run(
                jmeterHome,
                jmx,
                jtl,
                "localhost",
                "8081",
                "/",
                "1000",
                "1",
                "5"
        );

        JtlImporter.importJtl(jtl);

        HtmlDashboard.generate(jmeterHome, jtl, html);

        System.out.println("Done");
    }
}
