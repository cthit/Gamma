import React from "react";
import { DigitProviders } from "@cthit/react-digit-components";
import { GammaLoadingSingletonProvider } from "../common/context/GammaLoading.context";

const theme = {
    breakpoints: {
        xs: 0,
        sm: 600,
        md: 960,
        lg: 1280,
        xl: 1920
    }
};

const ProvidersForApp = ({ children }) => (
    <DigitProviders defaultLanguage="en" theme={theme}>
        <GammaLoadingSingletonProvider>
            {children}
        </GammaLoadingSingletonProvider>
    </DigitProviders>
);

export default ProvidersForApp;
