import { find } from "lodash";
import { useGammaUser } from "@cthit/react-digit-components";

function useIsAdmin() {
    const user = useGammaUser();

    if (user == null) {
        return false;
    }

    const adminAuthority = find(user.authorities, { authority: "admin" });

    return adminAuthority != null;
}

export default useIsAdmin;
