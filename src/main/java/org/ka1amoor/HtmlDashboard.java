package org.ka1amoor;



import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class HtmlDashboard {

    public static void generate(String jmeterHome, Path jtl, Path outDir) throws IOException, InterruptedException {
        // для jmeter требуется пустая output-папка для -o
        if (Files.exists(outDir)) {
            try (var s = Files.walk(outDir)) {
                s.sorted((a,b) -> b.compareTo(a)).forEach(p -> {
                    try { Files.delete(p); } catch (IOException ignored) {}
                });
            }
        }
        Files.createDirectories(outDir);

        List<String> cmd = List.of(
                jmeterHome + "/bin/jmeter",
                "-g", jtl.toString(),
                "-o", outDir.toString()
        );

        Process p = new ProcessBuilder(cmd).inheritIO().start();
        int code = p.waitFor();
        if (code != 0) throw new RuntimeException("HTML report generation failed, exit code=" + code);
    }
}

