import com.rain.rpc.constants.RpcConstants;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.header.RpcHeader;
import com.rain.rpc.protocol.header.RpcHeaderFactory;
import com.rain.rpc.protocol.request.RpcRequest;

public class ProtocolTest {
    public static void main(String[] args) {
        RpcHeader rpcHeader = RpcHeaderFactory.getRequestHeader(RpcConstants.REFLECT_TYPE_JDK);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setOneway(false);
        rpcRequest.setAsync(false);
        rpcRequest.setClassName("com.rain.rpc.service.HelloService");
        rpcRequest.setMethodName("hello");
        rpcRequest.setParameters(new Object[]{"rain"});
        rpcRequest.setParameterTypes(new Class<?>[]{String.class});
        rpcRequest.setVersion("1.0.0");
        rpcRequest.setGroup("default");
        RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
        rpcProtocol.setHeader(rpcHeader);
        rpcProtocol.setBody(rpcRequest);
        System.out.println(rpcProtocol);
    }
}
