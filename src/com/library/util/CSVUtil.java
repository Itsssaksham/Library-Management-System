package com.library.util;
import java.io.*; import java.nio.file.*; import java.util.*; import java.util.stream.Collectors;
public class CSVUtil {
    public static List<String[]> readAll(String path) throws IOException {
        File f = new File(path);
        if (!f.exists()) { f.getParentFile().mkdirs(); f.createNewFile(); return new ArrayList<>(); }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            return br.lines().map(String::trim).filter(line->!line.isEmpty() && !line.startsWith("#")).map(line->line.split(",", -1)).collect(Collectors.toList());
        }
    }
    public static void writeAll(String path, List<String> lines) throws IOException {
        Path p = Paths.get(path); Files.createDirectories(p.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(p)) { for (String l: lines) { bw.write(l); bw.newLine(); } }
    }
}
