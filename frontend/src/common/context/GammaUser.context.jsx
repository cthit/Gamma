import React, { createContext, useState } from "react";
const GammaUserContext = createContext(null);

const GammaUserSingletonProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    return (
        <GammaUserContext.Provider value={[user, setUser]}>
            {children}
        </GammaUserContext.Provider>
    );
};

export { GammaUserSingletonProvider };
export default GammaUserContext;
