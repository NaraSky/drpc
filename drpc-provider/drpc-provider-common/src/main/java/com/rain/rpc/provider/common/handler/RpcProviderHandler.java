package com.rain.rpc.provider.common.handler;

import com.rain.rpc.common.helper.RpcServiceHelper;
import com.rain.rpc.common.threadpool.ServerThreadPool;
import com.rain.rpc.constants.RpcConstants;
import com.rain.rpc.protocol.RpcProtocol;
import com.rain.rpc.protocol.enumeration.RpcStatus;
import com.rain.rpc.protocol.enumeration.RpcType;
import com.rain.rpc.protocol.header.RpcHeader;
import com.rain.rpc.protocol.request.RpcRequest;
import com.rain.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);
    // Store the mapping relationship between service name#version#group and object instances
    private final Map<String, Object> handlerMap;
    // Which type of reflection to use when invoking the actual method
    private final String reflectType;

    public RpcProviderHandler(String reflectType, Map<String, Object> handlerMap) {
        this.reflectType = reflectType;
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        ServerThreadPool.submit(() -> {
            RpcHeader header = protocol.getHeader();
            header.setMessageType((byte) RpcType.RESPONSE.getType());
            RpcRequest request = protocol.getBody();
            
            logger.debug("Request received - ID: {}, method: {}#{}", 
                header.getRequestId(), request.getClassName(), request.getMethodName());
            
            RpcProtocol<RpcResponse> responseProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();
            
            try {
                Object result = handle(request);
                response.setResult(result);
                response.setAsync(request.isAsync());
                response.setOneway(request.isOneway());
                header.setStatus((byte) RpcStatus.SUCCESS.getCode());
            } catch (Throwable t) {
                response.setError(t.toString());
                header.setStatus((byte) RpcStatus.FAIL.getCode());
                logger.error("Request handling failed - ID: {}", header.getRequestId(), t);
            }
            
            responseProtocol.setHeader(header);
            responseProtocol.setBody(response);
            ctx.writeAndFlush(responseProtocol).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.debug("Response sent - ID: {}", header.getRequestId());
                }
            });
        });
    }

    private Object handle(RpcRequest request) throws Throwable {
        String serviceKey = RpcServiceHelper.buildServiceKey(
            request.getClassName(), request.getVersion(), request.getGroup());
        Object serviceBean = handlerMap.get(serviceKey);
        
        if (serviceBean == null) {
            throw new RuntimeException(String.format("Service not found: %s#%s", 
                request.getClassName(), request.getMethodName()));
        }

        return invokeMethod(serviceBean, serviceBean.getClass(), 
            request.getMethodName(), request.getParameterTypes(), request.getParameters());
    }

    private Object invokeMethod(Object bean, Class<?> clazz, String method, 
                               Class<?>[] paramTypes, Object[] params) throws Throwable {
        switch (this.reflectType) {
            case RpcConstants.REFLECT_TYPE_JDK:
                return invokeJDKMethod(bean, clazz, method, paramTypes, params);
            case RpcConstants.REFLECT_TYPE_CGLIB:
                return invokeCGLibMethod(bean, clazz, method, paramTypes, params);
            default:
                throw new IllegalArgumentException("Unsupported reflect type: " + reflectType);
        }
    }

    private Object invokeCGLibMethod(Object bean, Class<?> clazz, String method, 
                                    Class<?>[] paramTypes, Object[] params) throws Throwable {
        logger.debug("Invoking via CGLIB - {}#{}", clazz.getName(), method);
        FastClass fastClass = FastClass.create(clazz);
        FastMethod fastMethod = fastClass.getMethod(method, paramTypes);
        return fastMethod.invoke(bean, params);
    }

    private Object invokeJDKMethod(Object bean, Class<?> clazz, String method, 
                                  Class<?>[] paramTypes, Object[] params) throws Throwable {
        logger.debug("Invoking via JDK reflection - {}#{}", clazz.getName(), method);
        Method m = clazz.getMethod(method, paramTypes);
        m.setAccessible(true);
        return m.invoke(bean, params);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Server caught exception", cause);
        ctx.close();
    }
}