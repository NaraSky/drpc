package com.rain.rpc.protocol.request;

import com.rain.rpc.protocol.base.RpcMessage;

import java.util.Arrays;

public class RpcRequest extends RpcMessage {

    private static final long serialVersionUID = -9181148010800107734L;

    /**
     * Class name
     */
    private String className;

    /**
     * Method name
     */
    private String methodName;

    /**
     * Parameter types array
     */
    private Class<?>[] parameterTypes;

    /**
     * Parameters array
     */
    private Object[] parameters;

    /**
     * Version number
     */
    private String version;

    /**
     * Service group
     */
    private String group;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                ", version='" + version + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
