# 调度平台后台开发规范

- JSON转换工具统一使用Jackson,不允许使用FastJson和Gson。已经封装Jackson工具类`JacksonUtil.java`

- 系统日志
    
    - 系统日志需要记录请求的参数，但是如果接口参数是文件，会导致参数特别大，可能会报堆内存溢出，因此如果参数是文件的接口必须使用`@SysLogIgnoreParam`


