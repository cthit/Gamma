import React, { createContext, useCallback, useState } from "react";
import { getRequest } from "../../api/utils/api";
import { on401 } from "../utils/error-handling/error-handling";
const GammaUserContext = createContext(null);

const GammaUserSingletonProvider = ({ children }) => {
    const [[loading, error], setStatus] = useState([true, false]);
    const [user, setUser] = useState(null);

    const update = useCallback(
        (redirect = true) => {
            setStatus([true, false]);
            return new Promise((resolve, reject) => {
                getRequest("/users/me", null, redirect)
                    .then(response => {
                        const user = response.data;
                        if (user.phone == null) {
                            //since phone isn't required. :(
                            user.phone = "";
                        }
                        setUser(user);
                        setStatus([false, false]);
                        resolve(response);
                    })
                    .catch(error => {
                        if (
                            error.response != null &&
                            error.response.status === 401 &&
                            redirect
                        ) {
                            on401();
                        } else {
                            setStatus([false, true]);
                        }
                        reject(error);
                    });
            });
        },
        [setUser]
    );

    const ignore = useCallback(() => {
        setStatus([false, false]);
    }, [setStatus]);

    return (
        <GammaUserContext.Provider
            value={[user, update, [loading, error], ignore]}
        >
            {children}
        </GammaUserContext.Provider>
    );
};

export { GammaUserSingletonProvider };
export default GammaUserContext;
