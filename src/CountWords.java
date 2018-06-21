import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountWords {
	private String table = Application.getTable();
	public void write() {
		System.out.println("正在写入"+Application.getWordCount().size()+"个词的词频");
		String[][] datas = new String[Application.getWordCount().size()][2];
		int i = 0;
		//遍历词频map并写入数组
		for (Map.Entry<String, Integer> entry : Application.getWordCount().entrySet()) {
			try {
				if(entry.getKey() == null)
					continue;
				datas[i][0] = entry.getKey();
				datas[i++][1] = entry.getValue()+"";
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(i+"");
			}
		}
		String insertsql = "insert into "+ table +"_count(word,wordcount) values(?,?)";
		//将统计好的词频写入数据库
		ExecutorService exe = Executors.newSingleThreadExecutor();
		exe.execute(new InsertExecutor(insertsql, datas));
		exe.shutdown();
		while (!exe.isTerminated()) // 判断线程是否运行结束
			Application.Sleep(1000);
	}
	public void downloadWordCount() {
		//若程序为进行分词则重新下载词频
		if(Application.getWordCount().size() == 0) {
			String sql = "select word,wordcount from "
					+ table +"_count"
					+ " where word is not null";
			SqlDao sqlDao = new SqlDao();
			ArrayList<ArrayList<String>> datas = sqlDao.getQuery(sql, new String[] {"word","wordcount"}, 1);
			for (ArrayList<String> data : datas)
				Application.getWordCount().put(data.get(0), Integer.valueOf(data.get(1)));
		}
		System.out.println("词频下载完成");
	}
}
