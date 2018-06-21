import java.util.ArrayList;

public class MatchExecutor implements Runnable {
	private ArrayList<ArrayList<String>> datas;
	private int blockOneStart, blockTwoStart, maxlength;
	private String item;
	private int startIndex;

	public MatchExecutor(ArrayList<ArrayList<String>> datas, int blockOneStart, int blockTwoStart, String item,
			int startIndex) {
		// TODO Auto-generated constructor stub
		this.datas = datas;
		this.blockOneStart = blockOneStart;
		this.blockTwoStart = blockTwoStart;
		this.item = item;
		this.maxlength = datas.size();
		this.startIndex = startIndex;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		StringBuilder sBuilder = new StringBuilder();
		StringBuilder sWriter = new StringBuilder();
		int jStart = 0;
		// 判断是否是相同块以及需要省略的长度
		if (startIndex > blockTwoStart)
			jStart = startIndex % Application.getMatchstep();
		if (blockOneStart == blockTwoStart)
			jStart++;
		long startTime = System.currentTimeMillis();
		for (int i = 0; (i + blockOneStart) < maxlength && i < Application.getMatchstep(); i++) {
			ArrayList<String> c1 = Application.getSortCaches().get(datas.get(i + blockOneStart).get(0));
			for (int j = (jStart > i ? jStart : i + 1); (j + blockTwoStart) < maxlength
					&& j < Application.getMatchstep(); j++) {
				ArrayList<String> c2 = Application.getSortCaches().get(datas.get(j + blockTwoStart).get(0));
				if (Application.getDeleteDatas().get(c2.get(0)) != null)
					continue;
				if (item.equals("1")) {
					// 当待匹配的文本长度大于匹配文本的1.25倍，则认为两个文本不会相似
					if (Application.getCaches().get(c1.get(0)).size() > 11 && Application.getCaches().get(c2.get(0))
							.size() >= (int) (Application.getCaches().get(c1.get(0)).size() * 1.25))
						break;
				} else {
					if (c1.size() > 11 && c2.size() > (int) (c1.size() * 1.25))
						break;
				}
				int result = Levenshtein.Calculate(c1, c2);
				if (result > Application.getRate()) {
					sBuilder.append(c1.get(0) + ":" + c2.get(0) + ":" + result + "\n");
					Application.getDeleteDatas().put(c2.get(0), "");
					if (item.equals("1"))
						sWriter.append(Application.getCaches().get(c1.get(0)) + "\n"
								+ Application.getCaches().get(c2.get(0)) + "\n\n");
					else
						sWriter.append(datas.get(i + blockOneStart) + "\n" + datas.get(j + blockTwoStart) + "\n\n");
				}
			}
		}
		long endTime = System.currentTimeMillis();
		if (Application.isShowDetailLog()) {
			if (item.equals("1"))
				System.out.println("选择题:第" + (blockOneStart / Application.getMatchstep()) + "个和第"
						+ (blockTwoStart / Application.getMatchstep()) + "个匹配需要:" + (endTime - startTime) / 1000 + "秒");
			else
				System.out.println(
						"填空题:第" + blockOneStart + "个到" + blockTwoStart + "个匹配需要:" + (endTime - startTime) / 1000 + "秒");
		}
		if (Application.isUseImport())
			insertServiceAndFile(sBuilder, sWriter);
	}

	private void insertServiceAndFile(StringBuilder sBuilder, StringBuilder sWriter) {
		if (sBuilder.toString().length() > 1) { // 当sBuilder长度大于1时，表明有需要写入的数据
			String insertsql = null;
			if (item.equals("0"))
				insertsql = "insert into " + Application.getTable()
						+ "_result(questionId1,questionId2,content_similarity) values(?,?,?)";
			else
				insertsql = "insert into " + Application.getTable()
						+ "_result(questionId1,questionId2,content_similarity,item) values(?,?,?,1)";
			Application.getApplicationExecutor()
					.execute(new InsertExecutor(insertsql, sBuilder.toString().split("\n"), sWriter.toString()));
		}
	}
}
