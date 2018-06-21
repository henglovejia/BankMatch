import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import manage.NegWord;
import manage.Postprocesser;
import manage.Preprocesser;
import manage.Punctuation;
import manage.VerbWord;

import base.POCGraph;
import base.TaggedSentence;

import character.CBTaggingDecoder;

public class ThulacUnit {
	// private boolean seg_only;
	private int maxLength;
	private String prefix;
	private Character separator = '_';
	private CBTaggingDecoder tagging_decoder;
	private Vector<String> vec = null;
	private Preprocesser preprocesser;
	private POCGraph poc_cands;
	private TaggedSentence tagged;
	private Postprocesser nsDict;
	private Postprocesser userDict;
	private Postprocesser idiomDict;
	private Punctuation punctuation;
	private NegWord negword;
	private VerbWord verbword;

	public ThulacUnit() {
		maxLength = 10000;
		prefix = "models/";
		try {
			tagging_decoder = new CBTaggingDecoder();
			tagging_decoder.init((prefix + "model_c_model.bin"), (prefix + "model_c_dat.bin"),
					(prefix + "model_c_label.txt"));
			preprocesser = new Preprocesser();
			poc_cands = new POCGraph();
			tagged = new TaggedSentence();
			nsDict = new Postprocesser((prefix + "ns.dat"), "ns", false);
			idiomDict = new Postprocesser((prefix + "idiom.dat"), "i", false);
			punctuation = new Punctuation((prefix + "singlepun.dat"));
			negword = new NegWord((prefix + "neg.dat"));
			verbword = new VerbWord((prefix + "vM.dat"), (prefix + "vD.dat"));
			preprocesser.setT2SMap((prefix + "t2s.dat"));
			userDict = new Postprocesser(prefix + "specialist.dat", "uw", true);
			tagging_decoder.threshold = 10000;
			tagging_decoder.separator = separator;
			tagging_decoder.setLabelTrans();
		} catch (Exception e) {
			System.out.println("ThulacUnit:" + e.toString());
		}
	}

	public String[] participle(ArrayList<ArrayList<String>> words, ArrayList<String> content_index) throws Exception {
		String oiraw;
		String raw = new String();
		StringBuilder sBuilder = new StringBuilder();
		ArrayList<String> list = new ArrayList<>(words.size());
		for (int k = 0; k < words.size(); k++) {
			vec = getRaw(words.get(k), maxLength);
			if (vec.size() == 0)
				break;
			for (int i = 0; i < vec.size(); i++) {
				oiraw = vec.get(i);
				raw = preprocesser.clean(oiraw, poc_cands);
				if (raw.length() > 0) {
					tagging_decoder.segment(raw, poc_cands, tagged);
					nsDict.adjust(tagged);
					idiomDict.adjust(tagged);
					punctuation.adjust(tagged);
					userDict.adjust(tagged);
					// timeword.adjustDouble(tagged);
					negword.adjust(tagged);
					verbword.adjust(tagged);
					for (int j = 0; j < tagged.size(); j++)
						if (!(tagged.get(j).tag.equals("w") || tagged.get(j).tag.equals("o")
								|| tagged.get(j).tag.equals("e") || tagged.get(j).tag.equals("c")
								|| tagged.get(j).tag.equals("u") || tagged.get(j).tag.equals("y"))) {
							sBuilder.append(tagged.get(j).word + " ");
							if (tagged.get(j).word != null) {
								if (Application.getWordCount().get(tagged.get(j).word) == null)
									Application.getWordCount().put(tagged.get(j).word, 1);
								else
									Application.getWordCount().put(tagged.get(j).word,
											Application.getWordCount().get(tagged.get(j).word) + 1);
							}
						}
					if (i == vec.size() - 1) {
						if (sBuilder.length() > 0) {
							list.add(sBuilder.toString());
							if (words.get(k).get(0) != null)
								content_index.add(words.get(k).get(0));
						}
						sBuilder = new StringBuilder();
					} else
						sBuilder.append(" ");
				}
			}
		}
		int size = list.size();
		return (String[]) list.toArray(new String[size]);
	}

	public static Vector<String> getRaw(ArrayList<String> word, int maxLength) {
		StringBuilder sBuilder = new StringBuilder();
		for (int i = 1; i < word.size(); i++)
			sBuilder.append(word.get(i) + " ");
		String ans = sBuilder.toString();
		Vector<String> ans_vec = new Vector<String>();
		if (ans == null)
			return ans_vec;
		if (ans.length() < maxLength) {
			ans_vec.add(ans);
		} else {
			Pattern p = Pattern.compile(".*?[。？！；;;!?]");
			Matcher m = p.matcher(ans);
			int num = 0, pos = 0;
			String tmp;
			while (m.find()) {
				tmp = m.group(0);
				if (num + tmp.length() > maxLength) {
					ans_vec.add(ans.substring(pos, pos + num));
					pos += num;
					num = tmp.length();
				} else {
					num += tmp.length();
				}
			}
			if (pos != ans.length())
				ans_vec.add(ans.substring(pos));
		}
		return ans_vec;
	}
}
