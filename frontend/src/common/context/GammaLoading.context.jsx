import React, { createContext, useCallback, useReducer } from "react";
const GammaLoadingContext = createContext(true);

const START_LOADING = "start-loading";
const END_LOADING = "end-loading";

const loadingReducer = (state, action) => {
    switch (action.type) {
        case START_LOADING:
            return true;
        case END_LOADING:
            return false;
        default:
            return state;
    }
};

const GammaLoadingSingletonProvider = ({ children }) => {
    const [loading, dispatch] = useReducer(loadingReducer, true);
    const startLoading = useCallback(() => {
        dispatch(START_LOADING);
    }, [dispatch]);
    const endLoading = useCallback(() => {
        dispatch(END_LOADING);
    }, [dispatch]);

    return (
        <GammaLoadingContext.Provider
            value={{
                loading,
                startLoading,
                endLoading
            }}
        >
            {children}
        </GammaLoadingContext.Provider>
    );
};

export { GammaLoadingSingletonProvider };
export default GammaLoadingContext;
