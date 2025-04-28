import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;
import java.util.stream.*;

// Define source and output paths
var srcDir = Paths.get("src/main/java");
var outFile = Paths.get("entity-summary.txt");

// Prepare writer (creates or truncates file)
var writer = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8);

try (var stream = Files.walk(srcDir)) {
    stream.filter(p -> p.toString().endsWith(".java"))
          .forEach(path -> {
        try {
            var lines = Files.readAllLines(path);
            // Detect @Entity in source
            if (lines.stream().anyMatch(l -> l.contains("@Entity"))) {
                writer.write("=== " + srcDir.relativize(path) + " ===\n");
                // Patterns for class and field declarations
                var classPat = Pattern.compile("\\bclass\\s+(\\w+)");
                var fieldPat = Pattern.compile("\\b(private|protected|public)\\s+([\\w<>\\[\\]]+)\\s+(\\w+)\\s*;");
                for (var line : lines) {
                    var cm = classPat.matcher(line);
                    if (cm.find()) writer.write("Entity: " + cm.group(1) + "\n");
                    var fm = fieldPat.matcher(line.trim());
                    if (fm.find()) writer.write("  • " + fm.group(3) + " : " + fm.group(2) + "\n");
                }
                writer.write("\n");
            }
        } catch (Exception e) {
            // Ignore parse errors
        }
    });
} finally {
    writer.close();  // Close writer to flush output
}
print("Done. See entity-summary.txt");

/exit