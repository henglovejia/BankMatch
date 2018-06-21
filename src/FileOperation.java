import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileOperation {
	private String character = "UTF-8";
	private static FileOperation fileOperation = new FileOperation();

	private FileOperation() {
		// TODO Auto-generated constructor stub
	}

	public FileOperation(String character) {
		// TODO Auto-generated constructor stub
		this.character = character;
	}
	
	public static FileOperation getInstance() {
		return fileOperation;
	}

	public String[] Reader(String path) {
		StringBuffer sBuffer = new StringBuffer();
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), character));
			String line;

			while ((line = bReader.readLine()) != null) {
				sBuffer.append(line + "\n");
			}
			bReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sBuffer.toString().split("\n");
	}

	public void Writer(String path, String word) {
		try {
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(path), true));
			bWriter.write(word);
			bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Writer(String path) {
		try {
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(new File(path)));
			bWriter.write("");
			bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
