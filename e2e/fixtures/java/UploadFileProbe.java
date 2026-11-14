import java.nio.file.Files;
import java.nio.file.Path;

/** Test-only probe for the JRE-only app image's private tmpfs uploads. */
public class UploadFileProbe {
  public static void main(String[] args) throws Exception {
    Path file = Path.of("/tmp/uploads").resolve(args[1]);
    switch (args[0]) {
      case "exists" -> System.out.println(Files.exists(file));
      case "block" -> {
        Files.delete(file);
        Files.createDirectory(file);
        Files.writeString(file.resolve("blocker"), "test");
      }
      case "unblock" -> {
        Files.delete(file.resolve("blocker"));
        Files.delete(file);
      }
      default -> throw new IllegalArgumentException(args[0]);
    }
  }
}
