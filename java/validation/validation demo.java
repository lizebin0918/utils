/*简单的方法参数校验*/

/*0.包导入*/
import javax.validation.constraints.*;
import org.springframework.validation.beanvalidation.*;

/*1.声明需校验参数的 Bean, 并注入到 spring 容器*/
@Validated
public class TestService {

    public void validationId(@Min(value = 4) Integer id, @NotNull String name, @NotEmpty String pwd) {
        System.out.println(name);
        System.out.println(id.intValue());
        System.out.println(pwd);
    }
}

/*2.声明后置校验处理器*/
@Bean
public MethodValidationPostProcessor getValidationPostProcessor() {
    return new MethodValidationPostProcessor();
}

/*3.调用者捕获 ConstraintViolationException 异常，说明参数有误*/
@Test
public void test12() {
    try {
        testService.validationId(new Integer(3), "123", new String());
    } catch (ConstraintViolationException e) {
        log.error("数据不合法", e);
    }
}

