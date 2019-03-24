import React from "react";
import { DigitText, DigitButton, DigitProviders } from "@cthit/react-digit-components";
import { rootReducer } from "./App.reducer";

const preloadedState = {};

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
    <DigitProviders
        rootReducer={rootReducer}
        preloadedState={preloadedState}
        defaultLanguage="en"
        theme={theme}
    >
        {children}
    </DigitProviders>
);

export default ProvidersForApp;
//
// <div>
//     <DigitButton text={"Styling knapp"}/>
//     <DigitText.Text text={"Styling text"}/>
// </div>
//