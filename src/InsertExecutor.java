import java.util.ArrayList;

public class InsertExecutor implements Runnable {
	private String insertsql;
	private String textData;
	private String[] datas;
	private String[][] word_datas;
	private ArrayList<String> content_index;
	private int runWhich;

	public InsertExecutor(String insertsql, String[] datas, ArrayList<String> content_index) {
		// TODO Auto-generated constructor stub
		this.insertsql = insertsql;
		this.datas = datas;
		this.content_index = content_index;
		this.runWhich = 0;
	}

	public InsertExecutor(String insertsql, String[] datas, String textData) {
		// TODO Auto-generated constructor stub
		this.insertsql = insertsql;
		this.datas = datas;
		this.textData = textData;
		this.runWhich = 1;
	}

	public InsertExecutor(String insertsql, String[][] word_datas) {
		// TODO Auto-generated constructor stub
		this.insertsql = insertsql;
		this.word_datas = word_datas;
		this.runWhich = 2;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch (runWhich) {
		case 0:
			runInThulac();
			break;
		case 1:
			runInMatch();
			break;
		case 2:
			runInCount();
			break;
		default:
			break;
		}
	}

	private void runInMatch() {
		int i = 0, j = 0;
		String[][] insert_datas = new String[datas.length][3];
		for (String result : datas) {
			String[] items = result.split(":");
			for (String item : items) {
				insert_datas[i][j++] = item;
			}
			j = 0;
			i++;
		}
		FileOperation.getInstance().Writer(Application.getResult(), textData);
		SqlDao sqlDao = new SqlDao();
		if (Application.isTestInService())
			sqlDao.insert(insertsql, insert_datas);
		sqlDao.closecon();
	}

	private void runInThulac() {
		SqlDao sDao = new SqlDao();
		String[][] insert_datas = new String[datas.length][3];
		for (int i = 0; i < datas.length; i++) {
			insert_datas[i][0] = content_index.get(i);
			insert_datas[i][1] = datas[i];
			insert_datas[i][2] = datas[i].split(" ").length + "";
		}
		sDao.insert(insertsql, insert_datas);
		sDao.closecon();
	}

	private void runInCount() {
		SqlDao sqlDao = new SqlDao();
		sqlDao.insert(insertsql, word_datas);
		sqlDao.closecon();
	}
}
