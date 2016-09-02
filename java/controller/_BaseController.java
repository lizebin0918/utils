/**
 * 所有Controller的基类
 */
public class _BaseController {
	
	protected final Logger log = LoggerFactory.getLogger(_BaseController.class);
	
	/**
	 * 5天免登陆
	 */
	private static final int SESSION_EXPIRED_INTERVAL = 5 * 24 * 3600;
	
	/** 
     * 用于处理异常的 
     * @return 
     */  
    @ExceptionHandler({Exception.class})  
    public ResponseEntity<String> exception(Exception e) {  
        log.error("通用异常处理", e);
        ResponseResult<WxMpMaterialUploadResult> result = new ResponseResult<>();
        result.setCode(Problems.ERROR_500);
        result.setMsg("系统异常");
        return response(result);
    }
	
	/**
	 * 直接把entity对象序列化成JSON字符串，最终用流输出
	 * @param entity
	 * @param response
	 */
	protected <T> void responseWithStream(T entity, HttpServletResponse response) {
		responseWithStream(entity, null, response);
	}
	
	/**
	 * 直接把entity对象序列化成JSON字符串，最终用流输出
	 * @param entity
	 * @param filter 包含声明字段，若为null则取全部 PropertyPreFilter filter = new SimplePropertyPreFilter(Class<?> clazz, "id");
	 * @param response
	 */
	protected <T> void responseWithStream(T entity, PropertyPreFilter filter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");  
        response.setContentType("text/html;charset=UTF-8");  
        try {
			response.getWriter().write(FastJsonUtils.toJSONString(entity, filter));
		} catch (IOException e) {
			log.error("输出流报错", e);
		}
	}
	
	/**
	 * @param responseResult
	 * @return
	 */
	protected <T> ResponseEntity<String> response(ResponseResult<T> responseResult) {
		return response(responseResult, null);
	}
	
	/**
	 * @param responseResult
	 * @param filter 包含声明字段，若为null则取全部 PropertyPreFilter filter = new SimplePropertyPreFilter(Class<?> clazz, "id"); 
	 * @return
	 */
	protected <T> ResponseEntity<String> response(ResponseResult<T> responseResult, PropertyPreFilter filter) {
		return ResponseEntity.ok(FastJsonUtils.toJSONString(responseResult, filter));
	}
	
	/**
	 * 获取请求域名
	 * @param request
	 * @return
	 */
	protected String getDomain(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String domain = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString().replace("http://", "");
		return domain;
	}
	
	/**
	 * 获取请求IP
	 * @param request
	 * @return
	 */
	protected String getIp(HttpServletRequest request) {
		int ipLenth = 20;//IP地址的最大长度
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.length() < ipLenth ? ip : "";
	}
	
	/**
	 * 如果参数值为null,""都会返回默认值(defaultValue)
	 * @param request
	 * @param paramterName
	 * @param defaultValue
	 * @return
	 */
	protected String getParameterByName(HttpServletRequest request, String parameterName, String defaultValue) {
		String _value = request.getParameter(parameterName);
		if(_value == null || "".equals(_value.trim())) {
			return defaultValue;
		}
		return _value;
	}
}