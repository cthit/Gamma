package it.chalmers.gamma.util;

public class ClassNameGeneratorUtils {

    private ClassNameGeneratorUtils() { }

    public static String classToScreamingSnakeCase(Class<?> c) {
        String className = c.getSimpleName();
        String[] camelCaseWords = className.split("(?=[A-Z])");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < camelCaseWords.length - 1; i++) {
            sb.append(camelCaseWords[i].toUpperCase());
            sb.append("_");
        }

        sb.append(camelCaseWords[camelCaseWords.length - 1].toUpperCase());

        return sb.toString();

    }

}
