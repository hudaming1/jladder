//
//import javax.net.ServerSocketFactory;  
//import java.io.DataInputStream;  
//import java.io.DataOutputStream;  
//import java.net.ServerSocket;  
//import java.net.Socket;  
//import java.security.Key;  
//import java.security.SecureRandom;  
//import java.util.concurrent.ExecutorService;  
//import java.util.concurrent.Executors;  
//  
//  
//public class HttpsMockServer extends HttpsMockBase {  
//    static DataInputStream in;  
//    static DataOutputStream out;  
//    static String hash;  
//    static Key key;  
//    static ExecutorService executorService= Executors.newFixedThreadPool(20);  
//    public static void main(String args[]) throws Exception{  
//        int port=80;  
//        ServerSocket ss= ServerSocketFactory.getDefault().createServerSocket(port);  
//        ss.setReceiveBufferSize(102400);  
//        ss.setReuseAddress(false);  
//        while(true){  
//            try {  
//                final Socket s = ss.accept();  
//                doHttpsShakeHands(s);  
//                executorService.execute(new Runnable() {  
//                    @Override  
//                    public void run() {  
//                        doSocketTransport(s);  
//                    }  
//                });  
//  
//            }catch (Exception e){  
//                e.printStackTrace();  
//            }  
//        }  
//    }  
//  
//    private static void doSocketTransport(Socket s){  
//        try{  
//            System.out.println("--------------------------------------------------------");  
//            int length=in.readInt();  
//            byte[] clientMsg=readBytes(length);  
//            System.out.println("客户端指令内容为:" + byte2hex(clientMsg));  
//  
//            writeBytes("服务器已经接受请求".getBytes());  
//        }catch (Exception ex){  
//            ex.printStackTrace();  
//        }  
//    }  
//  
//    public static byte[] readBytes(int length) throws  Exception{  
//        byte[] undecrpty=SocketUtils.readBytes(in,length);  
//        System.out.println("读取未解密消息:"+byte2hex(undecrpty));  
//        return DesCoder.decrypt(undecrpty,key);  
//    }  
//  
//    public static void writeBytes(byte[] data) throws  Exception{  
//        byte[] encrpted=DesCoder.encrypt(data,key);  
//        System.out.println("写入加密后消息:"+byte2hex(encrpted));  
//        SocketUtils.writeBytes(out,encrpted,encrpted.length);  
//    }  
//  
//    private static void doHttpsShakeHands(Socket s) throws Exception {  
//         in=new DataInputStream(s.getInputStream());  
//         out=new DataOutputStream(s.getOutputStream());  
//  
//        //第一步 获取客户端发送的支持的验证规则，包括hash算法，这里选用SHA1作为hash  
//        int length=in.readInt();  
//        in.skipBytes(4);  
//        byte[] clientSupportHash=SocketUtils.readBytes(in,length);  
//        String clientHash=new String(clientSupportHash);  
//        hash=clientHash;  
//        System.out.println("客户端发送了hash算法为:"+clientHash);  
//  
//        //第二步，发送服务器证书到客户端  
//        byte[] certificateBytes=CertifcateUtils.readCertifacates();  
//        privateKey=CertifcateUtils.readPrivateKeys();  
//        System.out.println("发送证书给客户端,字节长度为:"+certificateBytes.length);  
//        System.out.println("证书内容为:" + byte2hex(certificateBytes));  
//        SocketUtils.writeBytes(out, certificateBytes, certificateBytes.length);  
//  
//        System.out.println("获取客户端通过公钥加密后的随机数");  
//        int secureByteLength=in.readInt();  
//        byte[] secureBytes=SocketUtils.readBytes(in, secureByteLength);  
//  
//        System.out.println("读取到的客户端的随机数为:"+byte2hex(secureBytes));  
//        byte secureSeed[]=decrypt(secureBytes);  
//        System.out.println("解密后的随机数密码为:" +byte2hex(secureSeed));  
//  
//        //第三步 获取客户端加密字符串  
//        int skip=in.readInt();  
//        System.out.println("第三步 获取客户端加密消息,消息长度为 ：" +skip);  
//        byte[] data=SocketUtils.readBytes(in,skip);  
//  
//        System.out.println("客户端发送的加密消息为 : " +byte2hex(data));  
//        System.out.println("用私钥对消息解密，并计算SHA1的hash值");  
//        byte message[] =decrypt(data,new SecureRandom(secureBytes));  
//        byte serverHash[]=cactHash(message);  
//  
//  
//        System.out.println("获取客户端计算的SHA1摘要");  
//        int hashSkip=in.readInt();  
//        byte[] clientHashBytes=SocketUtils.readBytes(in,hashSkip);  
//        System.out.println("客户端SHA1摘要为 : " + byte2hex(clientHashBytes));  
//  
//        System.out.println("开始比较客户端hash和服务器端从消息中计算的hash值是否一致");  
//        boolean isHashEquals=byteEquals(serverHash,clientHashBytes);  
//        System.out.println("是否一致结果为 ： " + isHashEquals);  
//  
//  
//  
//        System.out.println("第一次校验客户端发送过来的消息和摘译一致，服务器开始向客户端发送消息和摘要");  
//        System.out.println("生成密码用于加密服务器端消息,secureRandom : "+byte2hex(secureSeed));  
//        SecureRandom secureRandom=new SecureRandom(secureSeed);  
//  
//        String randomMessage=random();  
//        System.out.println("服务器端生成的随机消息为 : "+randomMessage);  
//  
//        System.out.println("用DES算法并使用客户端生成的随机密码对消息加密");  
//        byte[] desKey=DesCoder.initSecretKey(secureRandom);  
//        key=DesCoder.toKey(desKey);  
//  
//        byte serverMessage[]=DesCoder.encrypt(randomMessage.getBytes(), key);  
//        SocketUtils.writeBytes(out,serverMessage,serverMessage.length);  
//        System.out.println("服务器端发送的机密后的消息为:"+byte2hex(serverMessage)+",加密密码为:"+byte2hex(secureSeed));  
//  
//        System.out.println("服务器端开始计算hash摘要值");  
//        byte serverMessageHash[]=cactHash(randomMessage.getBytes());  
//        System.out.println("服务器端计算的hash摘要值为 :" +byte2hex(serverMessageHash));  
//        SocketUtils.writeBytes(out,serverMessageHash,serverMessageHash.length);  
//  
//        System.out.println("握手成功，之后所有通信都将使用DES加密算法进行加密");  
//    }  
//  
//}  