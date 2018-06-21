import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Thulac {
	private long startTime, endTime;

	public void thulacInService(int maxThreads) {
		ExecutorService exe = Executors.newFixedThreadPool(maxThreads); // 由于每个线程占据内存较多，所以线程开启数量由不同机器决定，所以线程数量由命令行决定
		SqlDao sqlDao = new SqlDao("create table if not exists");
		// 删除题目和选项库以及词频库
		Application.getUpdateFiles().deleteServiceThulacDatas(sqlDao);
		startTime = System.currentTimeMillis();
		System.out.println("分词开始时间:" + Application.getShanghaiZone().format(new Date(startTime)));
		// new FileOperation().Writer(Application.getUseTime(), "服务器多线程初始化开始时间:"+
		// sFormat.format(new Date(startTime))+"\n");
		int max = sqlDao.getCountInTable(); // 获取题目的数量
		System.out.println("一共分词:"+max+"个");
		for (int j = 0; j < max; j += Application.getThulacStep()) { // 每个线程分配固定数量的题目进行匹配
			exe.execute(new ThulacExecutor(Application.getTable(), j));
			exe.execute(new ThulacExecutor(Application.getTable().replace("content", "item"), j));
		}
		exe.shutdown();
		while (!exe.isTerminated()) // 判断线程是否运行结束
			Application.Sleep(4000);
		Application.Sleep(3000); // 由于此线程只管理分词，而分词结果下放到另一个线程，所以往往分词结束，结果还没写入数据库
		// 所以需要再等待3秒，等数据写入之后，在将数据更新
		endTime = System.currentTimeMillis();
		System.out.println("分词结束时间:" + Application.getShanghaiZone().format(new Date(endTime)));
		System.out.println("分词" + max + "个耗时:" + Application.getInitialZone().format(new Date(endTime - startTime)));
		// new FileOperation().Writer(Application.getUseTime(), "服务器多线程分词结束时间:"+
		// sFormat.format(new Date(endTime))+"\n");
		FileOperation.getInstance().Writer(Application.getUseTime().replace("csv", "txt"),
				"分词" + max + "个耗时:" + Application.getInitialZone().format(new Date(endTime - startTime)) + "\n");
		Application.getUpdateFiles().updateThulac(sqlDao); // 将有选项的题目和无选项的题目分开，方便之后的计算
		sqlDao.closecon();
	}
}
