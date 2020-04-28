import { useContext } from "react";
import GammaUserContext from "../../context/GammaUser.context";

function useGammaUser() {
    const [user] = useContext(GammaUserContext);
    return user;
}

export default useGammaUser;
