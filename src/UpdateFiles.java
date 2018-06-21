
public class UpdateFiles {
	private void deleteOldThulac(SqlDao sqlDao) {
		System.out.println("删除旧题目和选项分词库……");
		sqlDao.deleteAll(Application.getTable() + "_thulac");
		sqlDao.deleteAll(Application.getTable().replace("content", "item") + "_thulac");
	}

	public void deleteOldMatch(SqlDao sqlDao) {
		System.out.println("删除旧匹配库……");
		if (Application.isTestInService())
			sqlDao.deleteAll(Application.getTable() + "_result");
	}

	private void deleteOldCountWord(SqlDao sqlDao) {
		System.out.println("删除旧词频库……");
		if (Application.isTestInService())
			sqlDao.deleteAll(Application.getTable() + "_count");
	}

	public void updateThulac(SqlDao sqlDao) {
		System.out.println("选择,填空进行分类……");
		sqlDao.update(Application.getTable() + "_thulac",
				Application.getTable().replace("content", "item") + "_thulac");
	}

	private void rewriteOldResult() {
		FileOperation.getInstance().Writer(Application.getResult());
	}

	private void rewriteUseTime() {
		FileOperation.getInstance().Writer(Application.getUseTime());
	}

	private void rewriteOldRange() {
		FileOperation.getInstance().Writer(Application.getRange());
	}

	public void rewriteMatchDatas() {
		if (Application.isReWrite()) {
			rewriteOldRange();
			rewriteOldResult();
			rewriteUseTime();
		}
	}

	public void deleteServiceThulacDatas(SqlDao sqlDao) {
		deleteOldThulac(sqlDao);
		deleteOldCountWord(sqlDao);
	}

	public void cleanDatas() {
		Application.getCaches().clear();
		Application.getSortCaches().clear();
		Application.getDeleteDatas().clear();
		Application.getItems().clear();
		Application.getSynonym().clear();
	}
}
