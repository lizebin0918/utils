/**
 * 集合工具类<br/>
 * Created on : 2017-04-23 23:22
 * @author lizebin
 */
public class CollectionUtils {

    private CollectionUtils() {}

    /**
     * 指定元素向左或者向右移动n位
     * @param list
     * @param index 被移动元素在列表中的索引
     * @param offset -n:向左移n位,+n:向右移n位
     * @param <T>
     */
    public static <T> void move(List<T> list, int index, int offset) {
        int size = list.size();
        if(offset > 0) {//取后面元素
            int endIndex = index - offset + 1;
            Collections.rotate(list.subList(index, endIndex > size ? size : endIndex), -1);
        }
        if(offset < 0) {//取前面元素
            int startIndex = index - offset;
            Collections.rotate(list.subList(startIndex < 0 ? 0 : startIndex, index + 1), 1);
        }
    }

    /**
     * 删除列表中，符合条件的元素
     * @param list
     * @param predicate
     * @param <T>
     * @return 删除元素的个数
     */
    public static <T> int delete(List<T> list, Predicate<T> predicate) {
        int deleteCount = 0;
        if (Objects.isNull(list) || list.isEmpty()) {
            return deleteCount;
        }
        if (list instanceof RandomAccess) {
            for (int size = list.size() - 1; 0 <= size; size--) {
                if (predicate.test(list.get(size))) {
                    ++deleteCount;
                    list.remove(size);
                }
            }
        } else {
            Iterator<T> i = list.iterator();
            while(i.hasNext()) {
                if(predicate.test(i.next())) {
                    ++deleteCount;
                    i.remove();
                }
            }
        }
        return deleteCount;
    }

    /**
     * 删除区间元素
     * @param list
     * @param startIndex low endpoint (inclusive) of the subList
     * @param endIndex high endpoint (exclusive) of the subList
     * @param <T>
     */
    public static <T> void delete(List<T> list, int startIndex, int endIndex) {
        list.subList(startIndex, endIndex).clear();
    }

    /**
     * 分页处理逻辑
     * @param list 原集合
     * @param limit 每页最大条数限制
     * @param consumer 接受分页后的列表
     * @param <T>
     */
    public static <T> void dealWithPage(List<T> list, int limit, Consumer<List<T>> consumer) {
        if(Objects.isNull(list) || list.isEmpty() || Objects.isNull(consumer)) {
            return;
        }
        int page = 1;
        int totalPageSize = new Double(Math.ceil((double) list.size() / limit)).intValue();
        while (totalPageSize > 0) {
            consumer.accept(list.stream().skip((page - 1) * limit).limit(limit).collect(Collectors.toCollection(() -> new ArrayList<>(limit))));
            totalPageSize--;
            page++;
        }
    }
}
