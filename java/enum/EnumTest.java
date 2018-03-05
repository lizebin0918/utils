package lesson14.protobuf;

import java.util.Optional;
import java.util.stream.Stream;

public class EnumTest {

	private String cycleUnit;

	public String getCycleUnit() {
		return cycleUnit;
	}

	public void setCycleUnit(String cycleUnit) {
		this.cycleUnit = cycleUnit;
	}

	/**
	 * 循环周期
	 */
	public enum CYCLE_UNIT_ENUM {
    	YEAR("y"), MONTH("m"), WEEK("w"), DAY("d");
    	CYCLE_UNIT_ENUM(String value) {
    		this.value = value;
	    }
    	private String value;
	    public String getValue() {
		    return value;
	    }
	    public static Optional<CYCLE_UNIT_ENUM> get(String value) {
		    return Stream.of(values()).filter(item -> item.value.equals(value)).findFirst();
	    }
    }

	public static void main(String[] args) {
		EnumTest task = new EnumTest();
		//前端请求参数或者读取数据库的值
		task.setCycleUnit("d");
		//程序处理，采用switch判断
		CYCLE_UNIT_ENUM cycleUnitEnum = CYCLE_UNIT_ENUM.get(task.getCycleUnit()).orElseThrow(() -> new RuntimeException(""));
		switch(cycleUnitEnum) {
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