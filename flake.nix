{
  description = "Nix flake for Gamma development";

  inputs.nixpkgs.url = "https://flakehub.com/f/NixOS/nixpkgs/0.1";

  outputs = { self, nixpkgs }:
    let
      # Change these values to update the whole stack
      javaVersion = 25;
      nodeVersion = 26;
      pnpmVersion = 11;

      supportedSystems = [ "x86_64-linux" "aarch64-linux" "aarch64-darwin" ];
      forEachSupportedSystem = f: nixpkgs.lib.genAttrs supportedSystems (system: f {
        pkgs = import nixpkgs { inherit system; overlays = [ self.overlays.default ]; };
      });
    in
    {
      overlays.default =
        final: prev: rec {
          temurin-bin = prev."temurin-bin-${toString javaVersion}";
          gradle = prev.gradle.override { java = temurin-bin; };
          nodejs = prev."nodejs_${toString nodeVersion}";
          pnpm = prev."pnpm_${toString pnpmVersion}";
        };

      devShells = forEachSupportedSystem ({ pkgs }: {
        default = pkgs.mkShell {
          packages = with pkgs; [
            gradle
            temurin-bin
            nodejs
            pnpm
          ];
        };
      });
    };
}
