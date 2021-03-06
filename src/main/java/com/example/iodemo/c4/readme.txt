* TCP粘包/拆包
    TCP协议是个流协议，所谓流，就是指没有界限的一串数据。河里的流水，是连成一片的，没有分界线。
    TCP底层并不了解上层业务数据的具体意义，他会根据TCP缓冲区的实际情况进行包的划分，所以在业务上一个完整的包，
有可能会被TCP拆分为多个包进行发送，也有可能把业务上多个小包封装成一个大的数据包发送，这就是所谓的TCP粘包和拆包问题。

TCP粘包/拆包
TCP协议是个流协议，所谓流，就是指没有界限的一串数据。河里的流水，是连成一片的，没有分界线。TCP底层并不了解上层业务数据的具体意义，他会根据TCP缓冲区的实际情况进行包的划分，所以在业务上一个完整的包，有可能会被TCP拆分为多个包进行发送，也有可能把业务上多个小包封装成一个大的数据包发送，这就是所谓的TCP粘包和拆包问题。

粘包拆包说明
现在假设客户端向服务端连续发送了两个数据包，用packet1和packet2来表示，那么服务端收到的数据可以分为三种，现列举如下：

第一种情况，接收端正常收到两个数据包，即没有发生拆包和粘包的现象。



第二种情况，接收端只收到一个数据包，由于TCP是不会出现丢包的，所以这一个数据包中包含了发送端发送的两个数据包的信息，这种现象即为粘包。这种情况由于接收端不知道这两个数据包的界限，所以对于接收端来说很难处理。



第三种情况，这种情况有两种表现形式，如下图。接收端收到了两个数据包，但是这两个数据包要么是不完整的，要么就是多出来一块，这种情况即发生了拆包和粘包。这两种情况如果不加特殊处理，对于接收端同样是不好处理的。



如果此时服务器端TCP接收窗口非常小，而数据包Packet1和Packet2比较大，很有可能会发生另一种情况——服务器分多次才能将Packet1和Packet2完全接收，期间会发生多次拆包。

TCP粘包拆包说明
1.要发送的数据大于TCP发送缓冲区剩余空间大小，将会发生拆包即应用程序写入数据的字节大小大于套接字发送缓冲区的大小。

2.进行MSS大小的TCP分段。MSS是最大报文段长度的缩写。MSS是TCP报文段中的数据字段的最大长度。数据字段加上TCP首部才等于整个的TCP报文段。所以MSS并不是TCP报文段的最大长度，而是：MSS=TCP报文段长度-TCP首部长度，待发送数据大于MSS（最大报文长度），TCP在传输前将进行拆包。

3.要发送的数据小于TCP发送缓冲区的大小，TCP将多次写入缓冲区的数据一次发送出去，将会发生粘包。

4.接收数据端的应用层没有及时读取接收缓冲区中的数据，将发生粘包。

5.以太网的payload大于MTU进行IP分片。MTU指：一种通信协议的某一层上面所能通过的最大数据包大小。如果IP层有一个数据包要传，而且数据的长度比链路层的MTU大，那么IP层就会进行分片，把数据包分成若干片，让每一片都不超过MTU。注意，IP分片可以发生在原始发送端主机上，也可以发生在中间路由器上。

TCP粘包拆包的解决策略
由于底层的TCP无法理解上层的业务数据，所以在底层是无法保证数据不被拆包和重组的，这样问题需要通过上层的应用协议栈设计来解决。

1. 消息定长。例如100字节。如果不够，空位补空格。

2. 在包尾部增加回车或者空格符等特殊字符进行分割，典型的如FTP协议，发送端将每个数据包封装为固定长度（不够的可以通过补0填充），这样接收端每次从接收缓冲区中读取固定长度的数据就自然而然的把每个数据包拆分开来。

3. 将消息分为消息头和消息体。消息头中包含消息总长度的字段，这样接收端每次从接收缓冲区中读取固定长度的数据就自然而然的把每个数据包拆分开来。

4. 其它复杂的协议，如RTMP协议等。