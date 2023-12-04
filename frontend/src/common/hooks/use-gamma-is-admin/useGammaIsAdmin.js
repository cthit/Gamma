import useGammaHasAuthority from "../use-gamma-has-authority/use-gamma-has-authority";
import useGammaUser from "../use-gamma-user/useGammaUser";

function useGammaIsAdmin() {
    const user = useGammaUser();

    return user.isAdmin;
}

export default useGammaIsAdmin;
