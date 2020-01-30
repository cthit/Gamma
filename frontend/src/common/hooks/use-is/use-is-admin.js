import { useSelector } from "react-redux";
import { find } from "lodash";

function useIsAdmin() {
    const user = useSelector(state => state.user);

    const adminAuthority = find(user.authorities, { authority: "admin" });

    return adminAuthority != null;
}

export default useIsAdmin;
