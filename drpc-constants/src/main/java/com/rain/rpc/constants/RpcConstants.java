package com.rain.rpc.constants;

public class RpcConstants {

    /**
     * Message header, fixed 32 bytes
     */
    public static final int HEADER_TOTAL_LEN = 32;

    /**
     * Magic number
     */
    public static final short MAGIC = 0x10;

    /**
     * Version number
     */
    public static final byte VERSION = 0x1;

    /**
     * JDK reflection type
     */
    public static final String REFLECT_TYPE_JDK = "jdk";

    /**
     * CGLIB reflection type
     */
    public static final String REFLECT_TYPE_CGLIB = "cglib";

    /**
     * JDK dynamic proxy
     */
    public static final String PROXY_JDK = "jdk";
    /**
     * Javassist dynamic proxy
     */
    public static final String PROXY_JAVASSIST = "javassist";
    /**
     * CGLIB dynamic proxy
     */
    public static final String PROXY_CGLIB = "cglib";

    /**
     * Initialization method name
     */
    public static final String INIT_METHOD_NAME = "init";

    /**
     * ZooKeeper registry center
     */
    public static final String REGISTRY_CENTER_ZOOKEEPER = "zookeeper";
    /**
     * Nacos registry center
     */
    public static final String REGISTRY_CENTER_NACOS = "nacos";
    /**
     * Apollo registry center
     */
    public static final String REGISTRY_CENTER_APOLLO = "apollo";
    /**
     * Etcd registry center
     */
    public static final String REGISTRY_CENTER_ETCD = "etcd";
    /**
     * Eureka registry center
     */
    public static final String REGISTRY_CENTER_EUREKA = "eureka";

    /**
     * Protostuff serialization
     */
    public static final String SERIALIZATION_PROTOSTUFF = "protostuff";
    /**
     * FST serialization
     */
    public static final String SERIALIZATION_FST = "fst";
    /**
     * Hessian2 serialization
     */
    public static final String SERIALIZATION_HESSIAN2 = "hessian2";
    /**
     * JDK serialization
     */
    public static final String SERIALIZATION_JDK = "jdk";
    /**
     * JSON serialization
     */
    public static final String SERIALIZATION_JSON = "json";
    /**
     * Kryo serialization
     */
    public static final String SERIALIZATION_KRYO = "kryo";
    /**
     * Consistent hash load balancer based on ZooKeeper
     */
    public static final String SERVICE_LOAD_BALANCER_ZKCONSISTENTHASH = "zkconsistenthash";

}

