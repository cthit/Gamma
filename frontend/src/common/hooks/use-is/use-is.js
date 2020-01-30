import { useSelector } from "react-redux";
import { find } from "lodash";

function useIs(authority) {
    const user = useSelector(state => state.user);

    const adminAuthority = find(user.authorities, { authority: authority });

    return adminAuthority != null;
}

export default useIs;