import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Init {
	
	public void itemsMapInit(ArrayList<ArrayList<String>> items_datas) {
		Map<String, ArrayList<String>> items = Application.getItems();
		for(int j=0;j<items_datas.size();j++) {
			ArrayList<String> item = items_datas.get(j);
			String key = item.get(0);
			if(items.get(key) == null) {
				items.put(key,item);
			}
			else {
				item.remove(0);
				items.get(key).addAll(item);
			}
		}
	}
	
	public void synonymInit(){
		System.out.println("程序初始化……");
		//遍历读取HCTC文件中的数据
		String[] lines = new FileOperation("GBK").Reader("HCTC.txt");
		int linenumber = 0;
		for(String line :lines) {
			if (line.charAt(7) == '=') {
				String[] keys = line.split(" ");
				if(keys.length>2) {
					for (int i = 1; i < keys.length; i++) {
						Set<String> set = Application.getSynonym().get(keys[i]) == null ? new HashSet<>() : Application.getSynonym().get(keys[i]);
						set.add((linenumber++)+"");
						Application.getSynonym().put(keys[i] , set);
					}
				}
			}
		}
	}
}
