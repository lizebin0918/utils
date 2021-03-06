public class MapUtils {

	@org.junit.Test
	public void test1() throws Exception {
		Map<String, Object> m = new HashMap<String, Object>(4);
		m.put("a", 1);
		m.put("b", 3);
		m.put("c", 4);
		//m.put("d", "2");
		System.out.println(sortMapByValue(m, null));
		System.out.println(m);
	}

	public static <K,V> Map<K,V> sortMapByValue(final Map<K, V> sourceMap, final Comparator<V> comparator) {
        //减少hash冲突和扩容
        int c = (int)((sourceMap.entrySet().size()/0.75) + 1.0F);
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        Map<K, V> sortedMap = new LinkedHashMap<K, V>(n);
        if(null != sourceMap && sourceMap.size() > 0) {
            List<Map.Entry<K, V>> entryList = new LinkedList<Map.Entry<K, V>>();
            Comparator<Map.Entry<K, V>> _comparator = new Comparator<Map.Entry<K, V>>() {
                @Override
                public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                    if(comparator == null) {
                        //两个 value 的类型是否一致，判断Value是否实现 Comparable 接口
                        if(o1.getValue().getClass() == o2.getValue().getClass()
                                && o1.getValue() instanceof Comparable
                                && o2.getValue() instanceof Comparable) {
                            return ((Comparable)o1.getValue()).compareTo((Comparable)o2.getValue());
                        } else {
                            return 0;
                        }
                    }
                    return comparator.compare(o1.getValue(), o2.getValue());
                }
            };
            Collections.sort(entryList, _comparator);
            Iterator<Map.Entry<K, V>> iter =  entryList.iterator();
            Map.Entry<K, V> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
                sourceMap.remove(tmpEntry.getKey());//移除源Map的元素
                iter.remove();//释放内存
            }
        }
        return sortedMap;
    }
}
