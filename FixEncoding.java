import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
public class FixEncoding {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting FixEncoding process...");
        Path root = Paths.get("D:/ITPROGRAM/intelidea/SchoolSystem/src");
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                String name = file.toString();
                if (name.endsWith(".java") || name.endsWith(".html") || name.endsWith(".properties")) {
                    try {
                        byte[] bytes = Files.readAllBytes(file);
                        String content = new String(bytes, StandardCharsets.UTF_8);
                        if (content.contains("РЎ") || content.contains("рџ") || content.contains("вњ") || content.contains("вќ") || content.contains("Рѕ")) {
                            byte[] originalUtf8Bytes = content.getBytes("windows-1251");
                            String restored = new String(originalUtf8Bytes, StandardCharsets.UTF_8);
                            System.out.println("Checking file: " + file);
                            // To be safer, just replace all if the mojibake is present
                            if (restored.length() > 0) {
                                Files.write(file, restored.getBytes(StandardCharsets.UTF_8));
                                System.out.println("Fixed encoding in: " + file);
                                content = restored; // for next step
                            }
                        }
                        // For HTML files, ensure <meta charset="UTF-8"> exists
                        if (name.endsWith(".html")) {
                            if (!content.contains("<meta charset=\"UTF-8\">") && content.contains("<head>")) {
                                content = content.replace("<head>", "<head>\n    <meta charset=\"UTF-8\">");
                                Files.write(file, content.getBytes(StandardCharsets.UTF_8));
                                System.out.println("Added <meta charset=\"UTF-8\"> to: " + file);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing " + file + ": " + e.getMessage());
                    }
                }
            });
        }
        System.out.println("Finished.");
    }
}