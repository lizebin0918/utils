package com.talent.common.util.xml.convertion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 处理CDATA嵌套的情况
 * */
public class CDATAUtil {

	private static final String CDATA_PREFIX = "<![CDATA[";
	private static final String CDATA_END = "]]>";
	private static final String REPLACE_CDATA = "]]]]><![CDATA[>";
	
	/*@Test
	public void testCDATA() {
		String s = "<PlainText><![CDATA[a<![CDATA[b1<![CDATA[<![CDATA[b3]]>b2]]>]]><![CDATA[d]]>]]><![CDATA[e]]></PlainText><Content><![CDATA[f<![CDATA[f2]]>]]><![CDATA[g]]></Content>";
		String correctStr = "<PlainText><![CDATA[a<![CDATA[b1<![CDATA[<![CDATA[b3]]]]><![CDATA[>b2]]]]><![CDATA[>]]]]><![CDATA[><![CDATA[d]]]]><![CDATA[>]]><![CDATA[e]]></PlainText><Content><![CDATA[f<![CDATA[f2]]]]><![CDATA[>]]><![CDATA[g]]></Content>";
		System.out.println(executeCDATA(s));
		System.out.println(executeCDATA(s).equals(correctStr));
	}*/
	
	public static String executeCDATA(String retStr) {
		int length = retStr.length();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		//获取CDATA开始坐标与结束坐标
		map = getCDATAStartEnd(retStr, map);
		Map<Integer, Integer> retMap = new HashMap<Integer, Integer>();
		//获得每个区间的最外围区间
		retMap = getZone(map, retMap);
		Set<Map.Entry<Integer, Integer>> entrySet = retMap.entrySet();
		ArrayList<Map.Entry<Integer, Integer>> l = new ArrayList<Map.Entry<Integer, Integer>>(
				entrySet);
		Collections.sort(l, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1,
					Map.Entry<Integer, Integer> o2) {
				return (o1.getKey() - o2.getKey());
			}
		});
		Iterator<Map.Entry<Integer, Integer>> i = l.iterator();
		List<String> strs = new ArrayList<String>();
		//从字符串的首字节开始
		int startIndex = 0;
		while (i.hasNext()) {
			Map.Entry<Integer, Integer> entry = i.next();
			//对最外围区间的所有CDATA结束符替换
			String _str = CDATA_PREFIX
					+ retStr.substring(entry.getKey() + CDATA_PREFIX.length(),
							entry.getValue() - CDATA_END.length()).replaceAll(
							CDATA_END, REPLACE_CDATA) + CDATA_END;
			//最外围区间左边的文本
			strs.add(retStr.substring(startIndex, entry.getKey()));
			strs.add(_str);
			startIndex = entry.getValue();
		}
		StringBuffer _s = new StringBuffer("");
		for (String s : strs) {
			_s.append(s);
		}
		//最外围区间右边的文本
		_s.append(retStr.substring(startIndex, length));
		return _s.toString();
	}
	
	//获得Map的并集区间:如有数轴(0,10),现在有区间(map的值)(1,5),(2,3),(3,4),(5,6),(7,8);retMap的值则返回区间:(1,5),(5,6),(7,8)
	public static Map<Integer, Integer> getZone(Map<Integer, Integer> map, Map<Integer, Integer> retMap) {
		// 第一个CDATA的起始位置
		int start = 0;
		int end = 0;
		if (map.size() != 0) {
			Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();
			ArrayList<Map.Entry<Integer, Integer>> l = new ArrayList<Map.Entry<Integer, Integer>>(entrySet);
			Collections.sort(l, new Comparator<Map.Entry<Integer, Integer>>() {
				public int compare(Map.Entry<Integer, Integer> o1,
						Map.Entry<Integer, Integer> o2) {
					return (o1.getKey() - o2.getKey());
				}
			});
			start = l.get(0).getKey();
			end = l.get(0).getValue();
			entrySet.remove(l.get(0));
			retMap.put(start, end);
			// 在此区间嵌套的所有CDATA坐标区间都删除
			Iterator<Map.Entry<Integer, Integer>> j = entrySet.iterator();
			while (j.hasNext()) {
				Map.Entry<Integer, Integer> jentry = j.next();
				if (jentry.getKey() > start && jentry.getValue() < end) {
					j.remove();
				}
			}
			return getZone(map, retMap);
		}
		return retMap;
	}

	//记录每个CDATA的起始位置和结束位置<startIndex,endIndex+CDATA_END.length()>等价于<'<![CDATA[', ']]>'>
	public static Map<Integer, Integer> getCDATAStartEnd(String str, Map<Integer, Integer> startEnd) {
		int endIndex = str.indexOf(CDATA_END, 0);
		//返回的就是<![CDATA的"<"位置
		int startIndex = str.lastIndexOf(CDATA_PREFIX, endIndex);
		if(endIndex == -1) {
			return startEnd;
		}
		String _str1 = str.substring(0, startIndex);
		String _str2 = str.substring(endIndex + CDATA_END.length());
		StringBuffer _strAppend = new StringBuffer();
		for(int i=0; i<endIndex + CDATA_END.length() - startIndex; i++) {
			//只是一个占位符，数据里面含有|，也没问题
			_strAppend.append("|");
		}
		startEnd.put(startIndex, endIndex + CDATA_END.length());
		str = _str1 + _strAppend.toString() + _str2;
		return getCDATAStartEnd(str, startEnd);
	}
}