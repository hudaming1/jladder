# netty-proxy
基于Netty开发的Http代理服务器

#### 待解决：
> 1. 传输优化，斟酌一下到底在heap上分配还是在direct上分配
> 2. 支持IPv6
> 3. 调整TCP参数，例如超时等
> 4. 开发socks协议
> 5. 支持监控：例如连接数，发送字节数，已接收字节数等
> 6. 配置通过main方法的args传入
> 7. 是否HTTPS不要通过端口判断，传入类型判断
> 8. HTTP的Get请求带中文，貌似处理不了
> 9. 从HTTP头中摘除Proxy-Connection头.  OK
> 10. 增加日志sfl4j


#### 测试连接：
> 1. http://pos.baidu.com/auto_dup?psi=2825367ec24f0f2315cbfc2e69f5a2c0&di=0&dri=0&dis=0&dai=0&ps=0&enu=encoding&dcb=___baidu_union_callback_&dtm=AUTO_JSONP&dvi=0.0&dci=-1&dpt=none&tsr=0&tpr=1560287949982&ti=%E8%BF%993%E4%B8%AA%E6%9C%89%E5%85%B3%E6%8A%A4%E8%82%A4%E7%9A%84%E5%B0%8F%E7%AA%8D%E9%97%A8%EF%BC%8C%E7%94%A8%E8%BF%87%E4%BC%9A%E7%AB%8B%E9%A9%AC%E6%8F%90%E5%8D%87%E9%A2%9C%E5%80%BC%E5%93%A6%EF%BC%8C%E4%BD%A0%E7%9F%A5%E9%81%93%E4%BA%86%E5%90%97&ari=2&dbv=0&drs=3&pcs=1680x439&pss=1680x3139&cfv=0&cpl=0&chi=1&cce=true&cec=UTF-8&tlm=1560259149&rw=439&ltu=http%3A%2F%2Fbaijiahao.baidu.com%2Fs%3Fid%3D1636038282713233707&ltr=http%3A%2F%2Fnews.baidu.com%2F&ecd=1&uc=1680x961&pis=-1x-1&sr=1680x1050&tcn=1560287950&dc=4
> 2. http://www.cqcoal.com/index.html
> 3. 