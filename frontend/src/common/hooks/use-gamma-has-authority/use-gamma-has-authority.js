import useGammaUser from "../use-gamma-user/useGammaUser";
import some from "lodash/some";

function useGammaHasAuthority(authority) {
    const user = useGammaUser();
    if (user == null) {
        return false;
    }

    return some(user.authorities, ["authority", authority]);
}

export default useGammaHasAuthority;
