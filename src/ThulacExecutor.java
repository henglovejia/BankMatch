import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThulacExecutor implements Runnable {
	private String querysql;
	private String insertsql;
	private String beginThulac;

	public ThulacExecutor(String table, int startindex) {
		// TODO Auto-generated constructor stub
		if (Application.isShowLog()) {
			if (table.indexOf("content") != -1)
				beginThulac = startindex + "到" + (startindex + Application.getThulacStep()) + "问题开始分词……";
			else
				beginThulac = startindex + "到" + (startindex + Application.getThulacStep()) + "选项开始分词……";
		}
		querysql = "select questionId,content from " + table + " where content is not null limit " + startindex + ","
				+ Application.getThulacStep();
		insertsql = "insert into " + table + "_thulac (questionId,thulac_content,length) values (?,?,?)";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (Application.isShowLog())
			System.out.println(beginThulac);
		SqlDao sqlDao = new SqlDao();
		String[] args = { "questionId", "content" };
		ArrayList<ArrayList<String>> content_raw_datas = sqlDao.getQuery(querysql, args, 0);
		ArrayList<String> content_index = new ArrayList<>(content_raw_datas.size());
		try {
			String[] thulac_datas = new ThulacUnit().participle(content_raw_datas, content_index);
			content_raw_datas = null;
			ExecutorService exe = Executors.newSingleThreadExecutor();
			exe.execute(new InsertExecutor(insertsql, thulac_datas, content_index));
			exe.shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ExecutorThulac:" + e.toString());
		}
	}
}
