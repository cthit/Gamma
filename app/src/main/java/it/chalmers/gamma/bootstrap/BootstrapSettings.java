package it.chalmers.gamma.bootstrap;

public record BootstrapSettings(
    boolean adminSetup,
    boolean mocking,
    String mockClientRedirectUrl,
    boolean mockClientEmailScope,
    MockData.MockClientAuthority mockClientAuthority) {}
