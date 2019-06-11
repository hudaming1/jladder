# netty-proxy
基于Netty开发的Http代理服务器

#### 待解决：
> 1. 传输优化，斟酌一下到底在heap上分配还是在direct上分配
> 2. 支持IPv6
> 3. 调整TCP参数，例如超时等
> 4. 开发socks协议
> 5. 支持监控：例如连接数，发送字节数，已接收字节数等
> 6. 配置通过main方法的args传入