import java.util.Date;

public class Match {
	private int[] step = new int[] { 0, 10, 14, 18, 23, 29, 37, 47, 59, 74, 93, 117, 147,10000 };
	private long startTime, endTime;

	public void distributeLength(String table) {
		// 题目选项初始化
		SqlDao sqlDao = new SqlDao();
		String items_sql = "select questionId,thulac_content" + " from " + table.replace("content", "item")
				+ " order by thulac_content";
		String[] args = { "questionId", "thulac_content" };
		System.out.println("初始化题目选项");
		new Init().itemsMapInit(sqlDao.getQuery(items_sql, args, 1));
		// 删除数据库旧匹配结果
		Application.getUpdateFiles().deleteOldMatch(sqlDao);
		// 删除本地旧匹配结果
		Application.getUpdateFiles().rewriteMatchDatas();
		sqlDao.closecon();
		startTime = System.currentTimeMillis();
		// 分辨是否使用提前结束
		if (Application.isUseImport())
			FileOperation.getInstance().Writer(Application.getUseTime(), "with:\n");
		else
			FileOperation.getInstance().Writer(Application.getUseTime(), "without:\n");
		System.out.println("服务器多线程匹配开始时间:" + Application.getShanghaiZone().format(new Date(startTime)));
		// 匹配长度相似的题库,不使用多线程,避免线程内开线程
		new MatchSimLength(table, "1", step[0], step[2], step[0]).run();
		new MatchSimLength(table, "0", step[0], step[2], step[0]).run();
		for (int i = 1; i < step.length - 2; i++) {
			new MatchSimLength(table, "1", step[i], step[i + 2], step[i + 1]).run();
			new MatchSimLength(table, "0", step[i], step[i + 2], step[i + 1]).run();
		}
		endTime = System.currentTimeMillis();
		System.out.println("服务器多线程匹配结束时间:" + Application.getShanghaiZone().format(new Date(endTime)));
		System.out.println("服务器多线程匹配耗时:" + Application.getInitialZone().format(new Date(endTime - startTime)));
		System.out.println("合计:" + (endTime - startTime) / 1000 + "秒");
		FileOperation.getInstance().Writer(Application.getUseTime(),
				"\nUseTime:" + Application.getInitialZone().format(new Date(endTime - startTime)) + "\n");
		FileOperation.getInstance().Writer(Application.getUseTime(),
				"\nUseTime:" + (endTime - startTime) / 1000 + "秒\n");
		;
	}
}
