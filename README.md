# Jladder
基于Netty开发的Http/Socks代理服务器

> 1.本地项目clone后启动ServerRun.java    
> 2.客户端挂http/https代理，默认端口用52007，搭梯子完成。   
> 3.为了防止DNS污染，推荐客户端配置DNS：8.8.8.8

#### TODO
> outside的线程模型还可以继续优化：需要再独立出一个线程池，负责与remote交互用，避免其中一个连接与remote超时，导致IO线程池阻塞