import useGammaUser from "../use-gamma-user/useGammaUser";

function useGammaHasAuthority(authority) {
    const user = useGammaUser();
    if (user == null) {
        return false;
    }

    return user.authorities.includes(authority);
}

export default useGammaHasAuthority;
