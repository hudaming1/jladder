## 创建证书

#### 1.创建ca基本目录
> 随便找一个目录，执行命令：mkdir -p myca/rootca   
> 后续我们所有操作都是在「myca」目录下执行

#### 2.准备根证书的配置文件
> 参见「rootca.cnf」文件   
> 值得注意的是，里面的相对路径要换成当前「myca」所在的目录

#### 3.准备目录和文件
> 执行「roothelper.sh」文件   
> $ ./roothelpler.sh

#### 4.创建 root 秘钥
> $ cd rootca   
> $ openssl genrsa -aes256 -out private/rootca.key.pem 4096  
> $ chmod 400 private/rootca.key.pem

#### 5.创建 Certificate Signing Requests(csr)
> $ openssl req -new -config rootca.cnf -sha256 -key private/rootca.key.pem -out csr/rootca.csr.pem   
> 输出csr内容：$ openssl req -text -noout -in csr/rootca.csr.pem

#### 6.创建 CA 的根证书
> $ openssl ca -selfsign -config rootca.cnf -in csr/rootca.csr.pem -extensions v3_ca -days 7300 -out certs/rootca.cert.pem

#### 7.合并证书 & 导出p12文件
> cat private/rootca.key.pem certs/rootca.cert.pem > server_cert.pem   
> openssl pkcs12 -export -in server_cert.pem -out server_cert.p12 -caname nickli

#### 8.使用CA签发证书
> 进入「server」目录   
> 生成私钥：openssl genrsa -out server.key 1024   
> 创建请求：openssl req -new -key server.key -out server.csr -subj /CN=*.baidu.com
> 使用CA颁发证书：openssl x509 -req -days 3650 -in server.csr -CA ../server_cert.pem -CAkey ../server_cert.pem -CAcreateserial -out server.crt    
> 给Java程序导出p12文件：openssl pkcs12 -export -in server.crt -inkey server.key -out baidu_server.p12



## 参考资料
> 创建CA：https://www.cnblogs.com/sparkdev/p/10369313.html