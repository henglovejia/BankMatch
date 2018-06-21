import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchSimLength {
	private String item, table;
	private int minlength, maxlength, startlength;

	public MatchSimLength(String table, String item, int minlength, int maxlength, int startlength) {
		this.item = item;
		this.table = table;
		this.startlength = startlength;
		this.minlength = minlength;
		this.maxlength = maxlength;
	}

	public void run() {
		// TODO Auto-generated method stub
		// 下载固定长度的试题
		SqlDao sqlDao = new SqlDao();
		String content_sql = "select questionId,thulac_content" + " from " + table + " where ( item =" + item
				+ " ) and ( length between " + minlength + " and " + maxlength + " ) and ( id < "+Application.getLimit()+" ) order by length,questionId";
		String[] args = { "questionId", "thulac_content" };
		ArrayList<ArrayList<String>> datas = sqlDao.getQuery(content_sql, args, 1);
		sqlDao.closecon();
		// 分配本地cpu核心数的线程
		ExecutorService exe = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
		// 将选择题与选项拼接存入Application.getCaches()中,并将结果以词频重新排序存入Application.getSortCaches()中
		int startIndex = -1;
		for (int i = 0; i < datas.size(); i++) {
			if (startIndex == -1 && datas.get(i).size() > startlength)
				startIndex = i;
			ArrayList<String> value;
			String key = datas.get(i).get(0);
			if (item.equals("1") && Application.getCaches().get(key) == null) {
				ArrayList<String> ivalue = Application.getItems().get(datas.get(i).get(0));
				value = combine(datas.get(i), ivalue);
				Application.getCaches().put(key, value);
				unsortToSort(key, value);
			} else if (item.equals("0") && Application.getSortCaches().get(key) == null) {
				unsortToSort(key, datas.get(i));
			}
		}
		Application.getWith().set(0);
		Application.getWithout().set(0);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < datas.size(); i += Application.getMatchstep())
			for (int j = i; j < datas.size(); j += Application.getMatchstep())
				if (startIndex <= (j + Application.getMatchstep()))
					exe.execute(new MatchExecutor(datas, i, j, item, startIndex));
		exe.shutdown();
		while (!exe.isTerminated())
			Application.Sleep(200);
		long endTime = System.currentTimeMillis();
		if (Application.isShowLog()) {
			if (item.equals("1"))
				System.out.println(minlength + "到" + maxlength + "长度的选择题匹配" + datas.size() + "个耗时:"
						+ Application.getInitialZone().format(new Date(endTime - startTime)));
			else
				System.out.println(minlength + "到" + maxlength + "长度的填空题匹配" + datas.size() + "个耗时:"
						+ Application.getInitialZone().format(new Date(endTime - startTime)));
		}
		if (item.equals("1")) {
			FileOperation.getInstance().Writer(Application.getUseTime(),
					minlength + "~" + maxlength + "|XZ|" + datas.size() + "|" + Application.getWith().get() + "|"
							+ Application.getWithout().get() + "|" + Application.getInitialZone().format(new Date(endTime - startTime)));
		} else {
			FileOperation.getInstance().Writer(Application.getUseTime(),
					minlength + "~" + maxlength + "|TK|" + datas.size() + "|" + Application.getWith().get() + "|"
							+ Application.getWithout().get() + "|" + Application.getInitialZone().format(new Date(endTime - startTime)));
		}
	}

	// 题目与选项拼接
	private ArrayList<String> combine(ArrayList<String> list1, ArrayList<String> list2) {
		if (list2 == null)
			return list1;
		list2.remove(0);
		list1.addAll(list2);
		return list1;
	}

	// 将未排序的题目进行排序
	private void unsortToSort(String key, ArrayList<String> unSortValue) {
		ArrayList<String> value = new ArrayList<>();
		value.addAll(unSortValue);
		Collections.sort(value, new Comparator<String>() {
			@Override
			public int compare(String obj1, String obj2) {
				// TODO Auto-generated method stub
				if (obj1.equals(value.get(0)) || obj2.equals(value.get(0)))
					return 0;
				return (Application.getWordCount().containsKey(obj1) ? Application.getWordCount().get(obj1) : 0)
						- (Application.getWordCount().containsKey(obj2) ? Application.getWordCount().get(obj2) : 0);
			}
		});
		Application.getSortCaches().put(key, value);
	}
}
