package com.mall.base.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 需要在spring.xml的容器中声明bean:
 * 	<bean id="wordsFilterHelper" class="com.mall.base.helper.WordsFilterHelper">
         <constructor-arg index="0" value="[文件class路径]" />
    </bean>
 * 敏感词过滤器，并支持替换
 * @author Administrator
 *
 */
public class WordsFilterHelper {
	
	private static class Node {
		int code;
		int depth;
		int left;
		int right;
	};

	private int check[];
	private int base[];

	private boolean used[];
	private int size;
	private int allocSize;
	private List<String> keys;
	private int keySize;
	private int length[];
	private int value[];
	private int progress;
	private int nextCheckPos;
	
	private int resize(int newSize) {
		int[] base2 = new int[newSize];
		int[] check2 = new int[newSize];
		boolean used2[] = new boolean[newSize];
		if (allocSize > 0) {
			System.arraycopy(base, 0, base2, 0, allocSize);
			System.arraycopy(check, 0, check2, 0, allocSize);
			System.arraycopy(used2, 0, used2, 0, allocSize);
		}

		base = base2;
		check = check2;
		used = used2;

		return allocSize = newSize;
	}

	private int fetch(Node parent, List<Node> siblings) {
		int prev = 0;

		for (int i = parent.left; i < parent.right; i++) {
			if ((length != null ? length[i] : keys.get(i).length()) < parent.depth)
				continue;

			String tmp = keys.get(i);

			int cur = 0;
			if ((length != null ? length[i] : tmp.length()) != parent.depth)
				cur = (int) tmp.charAt(parent.depth) + 1;

			if (prev > cur) {
				return 0;
			}

			if (cur != prev || siblings.size() == 0) {
				Node tmp_node = new Node();
				tmp_node.depth = parent.depth + 1;
				tmp_node.code = cur;
				tmp_node.left = i;
				if (siblings.size() != 0)
					siblings.get(siblings.size() - 1).right = i;

				siblings.add(tmp_node);
			}

			prev = cur;
		}

		if (siblings.size() != 0)
			siblings.get(siblings.size() - 1).right = parent.right;

		return siblings.size();
	}

	private int insert(List<Node> siblings) {
		int begin = 0;
		int pos = ((siblings.get(0).code + 1 > nextCheckPos) ? siblings.get(0).code + 1
				: nextCheckPos) - 1;
		int nonzero_num = 0;
		int first = 0;

		if (allocSize <= pos)
			resize(pos + 1);

		outer: while (true) {
			pos++;

			if (allocSize <= pos)
				resize(pos + 1);

			if (check[pos] != 0) {
				nonzero_num++;
				continue;
			} else if (first == 0) {
				nextCheckPos = pos;
				first = 1;
			}

			begin = pos - siblings.get(0).code;
			if (allocSize <= (begin + siblings.get(siblings.size() - 1).code)) {
				// progress can be zero
				double l = (1.05 > 1.0 * keySize / (progress + 1)) ? 1.05 : 1.0
						* keySize / (progress + 1);
				resize((int) (allocSize * l));
			}

			if (used[begin])
				continue;

			for (int i = 1; i < siblings.size(); i++)
				if (check[begin + siblings.get(i).code] != 0)
					continue outer;

			break;
		}

		if (1.0 * nonzero_num / (pos - nextCheckPos + 1) >= 0.95)
			nextCheckPos = pos;

		used[begin] = true;
		size = (size > begin + siblings.get(siblings.size() - 1).code + 1) ? size
				: begin + siblings.get(siblings.size() - 1).code + 1;

		for (int i = 0; i < siblings.size(); i++)
			check[begin + siblings.get(i).code] = begin;

		for (int i = 0; i < siblings.size(); i++) {
			List<Node> new_siblings = new ArrayList<Node>();

			if (fetch(siblings.get(i), new_siblings) == 0) {
				base[begin + siblings.get(i).code] = (value != null) ? (-value[siblings
						.get(i).left] - 1) : (-siblings.get(i).left - 1);

				if (value != null && (-value[siblings.get(i).left] - 1) >= 0) {
					return 0;
				}
				progress++;
			} else {
				int h = insert(new_siblings);
				base[begin + siblings.get(i).code] = h;
			}
		}
		return begin;
	}

	/**
	 * 加载敏感词，每一个词占一行
	 * @param file
	 * @param charset
	 */
	public WordsFilterHelper(String fileClassPath, String charset) throws IOException {
		check = null;
		base = null;
		used = null;
		size = 0;
		allocSize = 0;
		BufferedReader br = null;
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileClassPath);
			ArrayList<String> keys = new ArrayList<String>(512);
			if(charset == null || "".equals(charset.trim())) {
				charset = "UTF-8";
			}
			br = new BufferedReader(new InputStreamReader(is, charset), 2048);
			String _temp = null;
			//方便后面随机访问
			while((_temp = br.readLine()) != null) {
				if(!"".equals(_temp.trim()) && !keys.contains(_temp)) {
					keys.add(_temp);
				}
			}
			//按字典排序，默认根据字符的ascii码排序
			Collections.sort(keys);
			build(keys, null, null, keys.size());
		} catch (IOException e) {
			throw e;
		} finally {
			if(br != null) {
				br.close();
				br = null;
			}
		}
	}

	private void build(List<String> _key, int _length[], int _value[],
			int _keySize) {
		if (_keySize > _key.size() || _key == null)
			return;
		keys = _key;
		length = _length;
		keySize = _keySize;
		value = _value;
		progress = 0;
		resize(65536 * 32);
		base[0] = 1;
		nextCheckPos = 0;
		Node root_node = new Node();
		root_node.left = 0;
		root_node.right = keySize;
		root_node.depth = 0;
		List<Node> siblings = new ArrayList<Node>();
		fetch(root_node, siblings);
		insert(siblings);
		used = null;
	}

	/**
	 * {[敏感词索引], [敏感词在源字符串的索引]}
	 * @param key 源字符串
	 * @return 如果找不到则返回 null
	 */
	public int[] searchReturn(String key) {
		return searchReturn(key, 0, 0, 0);
	}

	private int[] searchReturn(String key, int pos, int len, int nodePos) {
		if (len <= 0)
			len = key.length();
		if (nodePos <= 0)
			nodePos = 0;

		char[] keyChars = key.toCharArray();

		int b = base[nodePos];
		int n;
		int p;

		boolean t = false;
		for (int i = pos; i < len; i++) {
			p = b;
			n = base[p];

			if (b == check[p] && n < 0) {
				return new int[]{(-n - 1), i};
			}

			p = b + (int) (keyChars[i]) + 1;
			if (b == check[p]) {
				t = true;
				b = base[p];
			} else {
				if(t) {
					t = false;
					b = base[nodePos];
					i--;
				}
			}
		}

		p = b;
		n = base[p];

		if (b == check[p] && n < 0) {
			return new int[]{(-n - 1), len};
		}
		return null;
	}
	
	/**
	 * @param key 源字符串
	 * @param replacement 替换的字符
	 * @return
	 */
	public String serachAndReplace(String key, char replacement) {
		try {
			StringBuilder sb = new StringBuilder(key);
			int[] i = null; int lastIndex = 0;
			while((i = this.searchReturn(key)) != null) {
				String _word = keys.get(i[0]);
				int _l = _word.length();
				StringBuilder _replacements = new StringBuilder(_l);
				for(int j=0; j<_l; j++) {
					_replacements.append(replacement);
				}
				sb.replace(i[1] - _word.length() + lastIndex,  lastIndex += i[1], _replacements.toString().intern());
				key = key.substring(i[1]);
			}
			return sb.toString();
		}catch(Exception e) {
			
		}
		return key;
	}
	
	/**
	 * 敏感词列表
	 * @return
	 */
	public List<String> getKeys() {
		return keys;
	}
	
	/**
	 * 替换QQ
	 * @param key
	 * @return
	 */
	public static String replaceQQ(String key, String replacement) {
		return key.replaceAll("[1-9]\\d{4,9}", replacement);
	}
	
	/**
	 * 替换手机号
	 * @param key
	 * @param replacement
	 * @return
	 */
	public static String replaceMobilePhone(String key, String replacement) {
		return key.replaceAll("(13|15|18|17)\\d{9}", replacement);
	}
}