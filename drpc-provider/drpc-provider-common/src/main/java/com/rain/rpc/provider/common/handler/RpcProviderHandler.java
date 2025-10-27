package com.rain.rpc.provider.common.handler;

import com.alibaba.fastjson2.JSON;
import com.rain.rpc.common.helper.RpcServiceHelper;
import com.rain.rpc.common.threadpool.ServerThreadPool;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Handler for processing incoming RPC requests on the provider side.
 * This handler receives messages from clients and processes them according to the registered services.
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProviderHandler.class);

    private final Map<String, Object> handlerMap;

    /**
     * Constructs an RpcProviderHandler with the specified handler map.
     *
     * @param handlerMap the map containing service instances for processing requests
     */
    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> rpcProtocol) throws Exception {
        ServerThreadPool.submit(() -> {
            LOGGER.info("Received RPC request protocol: {}", JSON.toJSONString(rpcProtocol));
            
            // Prepare response header
            RpcHeader header = rpcProtocol.getHeader();
            header.setMessageType((byte) RpcType.RESPONSE.getType());
            
            // Extract request body
            RpcRequest request = rpcProtocol.getBody();
            LOGGER.info("Service provider received request data: {}", JSON.toJSONString(request));
            
            // Initialize response protocol
            RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();
            
            try {
                // Process the request and get result
                Object result = handle(request);
                response.setResult(result);
                response.setAsync(request.isAsync());
                response.setOneway(request.isOneway());
                header.setStatus((byte) RpcStatus.SUCCESS.getCode());
                LOGGER.info("RPC request processed successfully");
            } catch (Throwable t) {
                // Handle exceptions during request processing
                response.setError(t.toString());
                header.setStatus((byte) RpcStatus.FAIL.getCode());
                LOGGER.error("Failed to process RPC request", t);
            }
            
            // Set response protocol components
            responseRpcProtocol.setHeader(header);
            responseRpcProtocol.setBody(response);
            
            // Send response back to client
            channelHandlerContext.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    LOGGER.debug("Sent response for request ID: {}", header.getRequestId());
                }
            });
        });
    }

    private Object handle(RpcRequest request) throws Throwable {
        // Build service key from class name, version and group
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        
        // Retrieve service implementation from handler map
        Object serviceBean = handlerMap.get(serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("Service not found for: %s:%s", request.getClassName(), request.getMethodName()));
        }

        // Extract method details
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        LOGGER.debug("Invoking method on class: {}", serviceClass.getName());
        LOGGER.debug("Method name: {}", methodName);
        
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; ++i) {
                LOGGER.debug("Parameter type {}: {}", i, parameterTypes[i].getName());
            }
        }

        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; ++i) {
                LOGGER.debug("Parameter value {}: {}", i, parameters[i].toString());
            }
        }
        
        // Invoke the requested method
        return invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
    }

    // TODO: Currently using JDK dynamic proxy approach, placeholder for future enhancements
    private Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("An exception was caught in the RPC server", cause);
        ctx.close();
    }
}
