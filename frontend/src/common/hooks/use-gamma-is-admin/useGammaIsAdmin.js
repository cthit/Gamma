import useGammaHasAuthority from "../use-gamma-has-authority/use-gamma-has-authority";

function useGammaIsAdmin() {
    return useGammaHasAuthority("admin");
}

export default useGammaIsAdmin;
