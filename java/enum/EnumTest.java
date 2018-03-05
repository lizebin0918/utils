package lesson14.protobuf;

import java.util.Optional;
import java.util.stream.Stream;

public class EnumTest {

	/**
	类型：年/月/周/日
	*/
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 循环周期
	 */
	public enum TYPE_ENUM {
    	YEAR("y"), MONTH("m"), WEEK("w"), DAY("d");
    	TYPE_ENUM(String value) {
    		this.value = value;
	    }
    	private String value;
	    public String getValue() {
		    return value;
	    }
	    public static Optional<TYPE_ENUM> get(String value) {
		    return Stream.of(values()).filter(item -> item.value.equals(value)).findFirst();
	    }
    }

	public static void main(String[] args) {
		EnumTest test = new EnumTest();
		//前端请求参数或者读取数据库的值
		test.setType("d");
		//程序处理，采用switch判断
		TYPE_ENUM type = TYPE_ENUM.get(test.getType()).orElseThrow(() -> new RuntimeException("枚举值不合法"));
		switch(type) {
			case DAY:
				System.out.println("it is day");
				break;
			case MONTH:
				System.out.println("it is month");
				break;
			case WEEK:
				System.out.println("it is week");
				break;
			case YEAR:
				System.out.println("it is year");
				break;
			default:
				System.out.println("default");
		}
	}

}