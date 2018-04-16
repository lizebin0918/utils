package com.lzb.redpackage;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;


/**
 * 红包工具类
 * @author lizebin
 *
 */
public class RedEnvelopeUtils {

	/*Random.nextInt()的确是线程安全，但是对于高并发的情况，使用ThreadLocalRandom效率更高*/
	//private static final Random random = new Random();
	/*
	Normally to generate Random numbers, we either do Create an instance of java.util.Random OR Math.random() - which internally creates an instance of java.util.Random on first invocation. However in a concurrent applications usage of above leads to contention issues
	Random is thread safe for use by multiple threads. But if multiple threads use the same instance of Random, the same seed is shared by multiple threads. It leads to contention between multiple threads and so to performance degradation.
	ThreadLocalRandom is solution to above problem. ThreadLocalRandom has a Random instance per thread and safeguards against contention.
	*/
	private static final ThreadLocalRandom random = ThreadLocalRandom.current();

	
	/**
	 * @param moneyForFen 以分为单位的金额
	 * @param size 红包数
	 * @return 随机分成以“分”为单位的红包
	 */
	public static int[] calculate(int moneyForFen, int size) {
		int min = 1;
		//简单的数据校验
		if(moneyForFen <= 0 || size <= 0 || size > moneyForFen/min) {
			throw new RuntimeException("红包数量不合法");
		}
		int[] packets = new int[size];
		if(size == moneyForFen/min) {
			for(int i=0; i<size; i++) {
				packets[i] = min;
			}
			return packets;
		}
		for(int i=0; i<size - 1; i++) {
			//随机数规则:
			//moneyForFen - (size - 1 -i) * min // 剩下的钱足够分给剩下的人，最少应该是：剩下人数 * min
			int _r1 = random.nextInt(0, moneyForFen - (size - 1 - i) * min);
			//2.上述产生的随机数再除以一个在红包数之间的因子
			int _r2 = 1 + random.nextInt(size);
			int _r3 = _r1 / _r2;
			int _moneyForFen = min + _r3;
			packets[i] = _moneyForFen;
			moneyForFen = moneyForFen - _moneyForFen;
		}
		packets[size - 1] = moneyForFen; 
		return packets;
	}
	
	/**
	 * 金额转换工具类
	 * @param moneyForFen
	 * @return
	 */
	public static String fenToYuan(String moneyForFen){
		String result = "";
		try{
			if(moneyForFen.indexOf(".") >= 0)
				result = moneyForFen;
			else if(moneyForFen != null && !moneyForFen.equals("")){
				if(moneyForFen.length() > 2)
					result = moneyForFen.substring(0,moneyForFen.length() - 2) + "." + moneyForFen.substring(moneyForFen.length() - 2);
				else if(moneyForFen.length() == 2 )
					result = "0." + moneyForFen;
				else if(moneyForFen.length() == 1 )
					result = "0.0" + moneyForFen;
			}
		}catch(Exception e){
			throw new RuntimeException("金额转换出错");
		}
		return result;
	}
	
	@org.junit.Test
	public void test() {
		int money = 9;
		int[] l = calculate(money, 9);
		System.out.println(Arrays.toString(l));
		int sum = 0;
		for(int i=0, size=l.length; i<size; i++) {
			sum += l[i];
		}
		Assert.assertTrue(money == sum);
	}
}
