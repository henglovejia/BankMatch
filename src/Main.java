import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		for (String table : Application.getTables()) {
			for (String limit : Application.getLimits()) {
				System.out.println(table + ":" + limit);
				// 批量设置table
				Application.setTable(table);
				//批量设置limit
				Application.setLimit(Integer.parseInt(limit));
				// 重置Applicatin
				new Application();
				// 是否先进行分词
				if (Application.isThulac()) {
					// 题库进行分词
					new Thulac().thulacInService(Application.getThreads());
					// 统计分词后的词频
					new CountWords().write();
				}
				// 下载分词之后的词频
				new CountWords().downloadWordCount();
				// 初始化同义转换
				new Init().synonymInit();
				// 匹配试题
				new Match().distributeLength(Application.getTable() + "_thulac");
				// 只有当使用提前结束才能统计结果
				if (Application.isUseImport())
					new CollectResult().collectResultInLocalhsot();
			}
		}
		// 关闭全局线程
		Application.getApplicationExecutor().shutdown();
	}
}
