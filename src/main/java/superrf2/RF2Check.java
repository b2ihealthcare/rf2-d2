package superrf2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
	name = "check",
	description = { "Checks a set of RF2 files and/or archives against the current RF2 specification" }
)
public class RF2Check implements Runnable {

	@Parameters(arity = "1..*", description = { "RF2 source files to check. Accepted files are *.txt and *.zip files" })
	List<String> paths;
	
	@Override
	public void run() {
		for (String path : paths) {
			try {
				check(Paths.get(path));
			} catch (Exception e) {
				System.err.println("Failed to read path: " + path);
				e.printStackTrace();
			}
		}
	}
	
	private void check(final Path path) throws IOException {
		if (Files.isDirectory(path)) {
			System.err.println("Directories are not supported yet! Path: " + path);
		} else {
			String fileName = path.getFileName().toString();
			if (fileName.endsWith("txt")) {
				long numberOfLines = Files.lines(path, Charset.forName("UTF-8")).count();
				System.out.println("File: " + fileName);
				System.out.println("Path: " + path);
				System.out.println("Number of lines: " + numberOfLines);
			} else {
				System.err.println("Unsupported file type! Path: " + path);
			}
		}
	}

}
