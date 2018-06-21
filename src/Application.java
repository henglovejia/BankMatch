import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Application {
	private static int thulacStep = 10000;
	private static int matchstep = 4000;
	private static int rate;
	private static int limit;
	private static int threads;
	private static boolean testInService = true;
	private static boolean useImprove, isThulac;
	private static boolean deleteInService = true;
	private static boolean isShowLog, isShowDetailLog;
	private static String url, user, password;
	private static AtomicLong with,without;
	private static Map<String, Set<String>> synonym;
	private static Map<String, ArrayList<String>> caches,sortCaches;
	private static Map<String, String> deleteDatas;
	private static Map<String, ArrayList<String>> items;
	private static Map<String, Integer> wordCount;
	private static ExecutorService applicationExecutor = Executors.newSingleThreadExecutor();
	private static String table;
	private static String[] tables,limits;
	private static UpdateFiles updateFiles;
	private static SimpleDateFormat initialZone,shanghaiZone;
	private static boolean isReWrite;

	static {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("jdbc.properties")));
			tables = prop.getProperty("tables", "t_question_content_chemistry").split("\\|");
			limit = Integer.parseInt(prop.getProperty("limit", "500000"));
			rate = Integer.parseInt(prop.getProperty("rate", "80"));
			threads = Integer.parseInt(prop.getProperty("threads", "8"));
			isThulac = Boolean.parseBoolean(prop.getProperty("isThulac", "false"));
			isShowLog = Boolean.parseBoolean(prop.getProperty("isShowLog", "false"));
			isShowDetailLog = Boolean.parseBoolean(prop.getProperty("isShowDetailLog", "false"));
			useImprove = Boolean.parseBoolean(prop.getProperty("useImprove", "true"));
			isReWrite = Boolean.parseBoolean(prop.getProperty("isReWrite", "true"));
			url = prop.getProperty("url",
					"jdbc:mysql://192.168.31.14:3306/testbank2?useUnicode=true&characterEncoding=UTF-8");
			user = prop.getProperty("user", "root");
			password = prop.getProperty("password", "123456");
			initialZone = new SimpleDateFormat("HH小时mm分ss秒");
			initialZone.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
			shanghaiZone = new SimpleDateFormat("HH:mm:ss");
			limits = prop.getProperty("limits", "500000").split("\\|");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Application() {
		// TODO Auto-generated constructor stub
		with = new AtomicLong(0);
		without = new AtomicLong(0);
		synonym = new HashMap<>();
		caches = new HashMap<>();
		sortCaches = new HashMap<>();
		deleteDatas = new HashMap<>();
		items = new HashMap<>();
		wordCount = new HashMap<>();
		updateFiles = new UpdateFiles();
	}

	public static int getThulacStep() {
		return thulacStep;
	}

	public static int getMatchstep() {
		return matchstep;
	}

	public static int getRate() {
		return rate;
	}

	public static boolean isTestInService() {
		return testInService;
	}

	public static Map<String, ArrayList<String>> getCaches() {
		return caches;
	}

	public static Map<String, Set<String>> getSynonym() {
		return synonym;
	}

	public static Map<String, ArrayList<String>> getItems() {
		return items;
	}

	public static String getResult() {
		return table + limit + "_result.txt";
	}

	public static String getRange() {
		return table + limit + "_range.txt";
	}

	public static Map<String, String> getDeleteDatas() {
		return deleteDatas;
	}

	public static Map<String, Integer> getWordCount() {
		return wordCount;
	}

	public static Map<String, ArrayList<String>> getSortCaches() {
		return sortCaches;
	}

	public static ExecutorService getApplicationExecutor() {
		return applicationExecutor;
	}

	public static int getLimit() {
		return limit;
	}

	public static String getUseTime() {
		return table + limit + "_useTime.csv";
	}

	public static boolean isUseImport() {
		return useImprove;
	}

	public static String getTable() {
		return table;
	}

	public static void setTable(String table) {
		Application.table = table;
	}

	public static boolean isDeleteInService() {
		return deleteInService;
	}

	public static UpdateFiles getUpdateFiles() {
		return updateFiles;
	}

	public static AtomicLong getWith() {
		return with;
	}

	public static AtomicLong getWithout() {
		return without;
	}

	public static boolean isThulac() {
		return isThulac;
	}

	public static int getThreads() {
		return threads;
	}

	public static boolean isShowLog() {
		return isShowLog;
	}

	public static boolean isShowDetailLog() {
		return isShowDetailLog;
	}

	public static SimpleDateFormat getInitialZone() {
		return initialZone;
	}

	public static String getUrl() {
		return url;
	}

	public static String getUser() {
		return user;
	}

	public static String getPassword() {
		return password;
	}

	public static String[] getTables() {
		return tables;
	}

	public static boolean isReWrite() {
		return isReWrite;
	}

	public static SimpleDateFormat getShanghaiZone() {
		return shanghaiZone;
	}

	public static String[] getLimits() {
		return limits;
	}
	
	public static void setLimit(int limit) {
		Application.limit = limit;
	}

	public static void Sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
