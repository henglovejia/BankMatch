import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CollectResult {
	private long startTime,endTime;
	public void collectResultInLocalhsot() {
		startTime = System.currentTimeMillis();
		System.out.println("服务器结果统计开始时间:"+ Application.getShanghaiZone().format(new Date(startTime)));
		int id = 0;
		Map<Integer, String> result = new HashMap<>();
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(Application.getResult())));
			String line1,line2;
			Map<String, Integer> save = new HashMap<>();
			while((line1 = bReader.readLine()) != null) {
				line2 = bReader.readLine();
				String[] temp1 = line1.replace("[", "").replace("]", "").split(", ");
				String[] temp2 = line2.replace("[", "").replace("]", "").split(", ");
				if(save.get(temp1[0]) != null || save.get(temp2[0]) != null) {
					if(save.get(temp1[0]) != null) {
						if(save.get(temp2[0]) == null) {
							save.put(temp2[0],save.get(temp1[0]));
							result.put(save.get(temp1[0]), result.get(save.get(temp1[0]))+line2+"\n");
						}
					}else if(save.get(temp2[0]) != null) {
						if(save.get(temp1[0]) == null) {
							save.put(temp1[0],save.get(temp2[0]));
							result.put(save.get(temp2[0]), result.get(save.get(temp2[0]))+line1+"\n");
						}
					}
				}else {
					save.put(temp1[0],id);
					save.put(temp2[0],id);
					result.put(id, line1+"\n"+line2+"\n");
					id++;
				}
				bReader.readLine();
			}
			bReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> words = new ArrayList<>(id);
		for(int i = 0 ; i < id ; i++)
			words.add(result.get(i));
		Collections.sort(words,new Comparator<String>() {
			@Override
			public int compare(String obj1, String obj2) {
				// TODO Auto-generated method stub
				return obj2.split("\n").length - obj1.split("\n").length;
			}
		});
		StringBuilder sBuilder = new StringBuilder();
		for(int i=0;i<id;i++) {
			String[] items = words.get(i).split("\n");
			FileOperation.getInstance().Writer(Application.getRange(), "第"+(i+1)+"个重复评价\n"+words.get(i) +"出现:" + items.length+"次\n");
			for(int j = 1 ; j < items.length ; j++) 
				sBuilder.append("\""+items[j].replace("[", "").split(",")[0]+"\",");
		}
		if(Application.isDeleteInService() && sBuilder.length() > 1) {
			SqlDao sqlDao = new SqlDao();
			sqlDao.deleteById(sBuilder.toString().substring(0, sBuilder.length()-1));
			sqlDao.closecon();
		}
		endTime = System.currentTimeMillis();
		System.out.println("服务器多线程结果统计结束时间:"+ Application.getShanghaiZone().format(new Date(endTime)));
		System.out.println("服务器多线程结果统计个耗时:"+ Application.getInitialZone().format(new Date(endTime - startTime)));
	}
}
