package it.chalmers.gamma.bootstrap;

public record BootstrapSettings(
    boolean adminSetup,
    boolean mocking,
    String mockDefaultClientRedirectUrl,
    boolean mockDefaultClientEmailScope,
    MockData.MockClientAuthority mockDefaultClientAuthority) {}
