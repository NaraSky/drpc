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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Handler for processing incoming RPC requests on the provider side.
 * This handler receives messages from clients and processes them according to the registered services.
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProviderHandler.class);

    private final Map<String, Object> handlerMap;
    private final String reflectType;

    /**
     * Constructs an RpcProviderHandler with the specified handler map.
     *
     * @param handlerMap the map containing service instances for processing requests
     */
    public RpcProviderHandler(String reflectType, Map<String, Object> handlerMap) {
        this.reflectType = reflectType;
        this.handlerMap = handlerMap;
        LOGGER.info("RpcProviderHandler initialized with reflectType: {}", reflectType);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> rpcProtocol) throws Exception {
        ServerThreadPool.submit(() -> {
            long requestId = rpcProtocol.getHeader().getRequestId();
            LOGGER.info("Received RPC request, request ID: {}", requestId);

            // Prepare response header
            RpcHeader header = rpcProtocol.getHeader();
            header.setMessageType((byte) RpcType.RESPONSE.getType());

            // Extract request body
            RpcRequest request = rpcProtocol.getBody();
            LOGGER.info("Processing RPC request: service={}, method={}, version={}, group={}", 
                request.getClassName(), request.getMethodName(), request.getVersion(), request.getGroup());

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
                LOGGER.info("RPC request processed successfully, request ID: {}", requestId);
            } catch (Throwable t) {
                // Handle exceptions during request processing
                response.setError(t.toString());
                header.setStatus((byte) RpcStatus.FAIL.getCode());
                LOGGER.error("Failed to process RPC request, request ID: {}", requestId, t);
            }

            // Set response protocol components
            responseRpcProtocol.setHeader(header);
            responseRpcProtocol.setBody(response);

            // Send response back to client
            channelHandlerContext.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        LOGGER.debug("Response sent successfully for request ID: {}", requestId);
                    } else {
                        LOGGER.warn("Failed to send response for request ID: {}", requestId, channelFuture.cause());
                    }
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
            String errorMsg = String.format("Service not found for: %s:%s, version: %s, group: %s", 
                request.getClassName(), request.getMethodName(), request.getVersion(), request.getGroup());
            LOGGER.error(errorMsg);
            throw new RuntimeException(errorMsg);
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
        switch (this.reflectType) {
            case RpcConstants.REFLECT_TYPE_JDK:
                return this.invokeJDKMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
            case RpcConstants.REFLECT_TYPE_CGLIB:
                return this.invokeCGLIBMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
            default:
                String errorMsg = "Unsupported reflect type: " + this.reflectType;
                LOGGER.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
        }
    }

    // --add-opens java.base/java.lang=ALL-UNNAMED
    private Object invokeCGLIBMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws InvocationTargetException {
        LOGGER.debug("Using CGLIB for method invocation: {}.{}", serviceClass.getSimpleName(), methodName);
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

    private Object invokeJDKMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LOGGER.debug("Using JDK dynamic proxy for method invocation: {}.{}", serviceClass.getSimpleName(), methodName);
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("An exception was caught in the RPC server, closing connection", cause);
        ctx.close();
    }
}