package com.rain.rpc.registry.api.config;

import java.io.Serializable;

public class RegistryConfig implements Serializable {

    private static final long serialVersionUID = 4019022327854747070L;

    private String registryAddress;
    private String registryType;

    public RegistryConfig(String registryAddress, String registryType) {
        this.registryAddress = registryAddress;
        this.registryType = registryType;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }
}