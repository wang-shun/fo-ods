package com.foods.measure;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.foods.lifeloader.lifeloaderdummy.Publisher.TrackLog;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;

public class CSALogMapListWriter {
	public static String separator = ";";

	
	public static String writeCsvfromListLC(List<LCTrackLog> l) {
		StringBuffer sb = new StringBuffer();

		if (l.size() == 0) {
			return "";
		}
		LCTrackLog tl = l.get(0);
		Map<String, String> m = tl.LogtoMap(tl);
		sb.append(writeCSV(m, true));

		for (int i = 1; i < l.size(); i++) {
			tl = l.get(i);
			m = tl.LogtoMap(tl);
			sb.append(writeCSV(m, false));
		}

		return sb.toString();
	}
	
	public static String writeCsvfromList(List<TrackLog> l) {
		StringBuffer sb = new StringBuffer();

		if (l.size() == 0) {
			return "";
		}
		TrackLog tl = l.get(0);
		Map<String, String> m = tl.LogtoMap();
		sb.append(writeCSV(m, true));

		for (int i = 1; i < l.size(); i++) {
			tl = l.get(i);
			m = tl.LogtoMap();
			sb.append(writeCSV(m, false));
		}

		return sb.toString();
	}

	public static String writeCSV(Map<String, String> map, boolean header) {
		StringBuffer sb = new StringBuffer();

		// Write header
		Set<String> s = map.keySet();

		int i = 0;
		if (header) {

			for (String col : s) {
				sb.append(col);
				if (++i < s.size()) {
					sb.append(separator);
				}
			}
			sb.append("\n");
		}

		// Write Content
		i = 0;
		for (String col : s) {
			if (map.get(col) != null) {
				// System.out.println(String.valueOf(map.get(col)));
				sb.append(String.valueOf(map.get(col)));
			} else {
				continue;
			}
			if (++i < s.size()) {
				sb.append(separator);
			}
		}
		sb.append("\n");

		return sb.toString();
	}
}
