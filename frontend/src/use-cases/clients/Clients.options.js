import * as yup from "yup";

import {
    DigitAutocompleteSelectMultiple,
    DigitCheckbox,
    DigitTextArea,
    DigitTextField
} from "@cthit/react-digit-components";

import {
    CLIENT_DESCRIPTION_ENGLISH,
    CLIENT_DESCRIPTION_SWEDISH,
    CLIENT_NAME,
    CLIENT_REDIRECT,
    CLIENT_ID,
    CLIENT_GENERATE_API_KEY,
    CLIENT_RESTRICTIONS,
    CLIENT_EMAIL_SCOPE
} from "api/clients/props.clients.api";

export const validationSchema = text => {
    const schema = {};
    schema[CLIENT_NAME] = yup.string().required(text.Name + text.IsRequired);

    schema[CLIENT_REDIRECT] = yup
        .string()
        .required(text.RedirectURI + text.IsRequired);

    schema[CLIENT_DESCRIPTION_SWEDISH] = yup
        .string()
        .required(text.SwedishDescription + text.IsRequired);

    schema[CLIENT_DESCRIPTION_ENGLISH] = yup
        .string()
        .required(text.EnglishDescription + text.IsRequired);

    schema[CLIENT_GENERATE_API_KEY] = yup.bool().required();
    schema[CLIENT_EMAIL_SCOPE] = yup.bool().required();
    schema[CLIENT_RESTRICTIONS] = yup.array().of(yup.string());

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const initialValues = {};
    initialValues[CLIENT_NAME] = "";
    initialValues[CLIENT_REDIRECT] = "";
    initialValues[CLIENT_DESCRIPTION_SWEDISH] = "";
    initialValues[CLIENT_DESCRIPTION_ENGLISH] = "";
    initialValues[CLIENT_GENERATE_API_KEY] = false;
    initialValues[CLIENT_RESTRICTIONS] = [];
    initialValues[CLIENT_EMAIL_SCOPE] = false;

    return initialValues;
};

export const keysComponentData = (text, authorityLevels) => {
    const keysComponentData = {};
    keysComponentData[CLIENT_NAME] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 50
        }
    };

    keysComponentData[CLIENT_REDIRECT] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 100
        }
    };

    keysComponentData[CLIENT_DESCRIPTION_SWEDISH] = {
        component: DigitTextArea,
        componentProps: {
            outlined: true,
            rows: 3,
            maxRows: 5,
            maxLength: 500
        }
    };

    keysComponentData[CLIENT_DESCRIPTION_ENGLISH] = {
        component: DigitTextArea,
        componentProps: {
            outlined: true,
            rows: 3,
            maxRows: 5,
            maxLength: 500
        }
    };

    keysComponentData[CLIENT_EMAIL_SCOPE] = {
        component: DigitCheckbox,
        componentProps: {
            primary: true,
            label: text.EmailScope
        }
    };

    keysComponentData[CLIENT_GENERATE_API_KEY] = {
        component: DigitCheckbox,
        componentProps: {
            primary: true,
            label: text.GenerateApiKey
        }
    };

    keysComponentData[CLIENT_RESTRICTIONS] = {
        component: DigitAutocompleteSelectMultiple,
        componentProps: {
            outlined: true,
            options: authorityLevels.map(level => ({
                text: level.authorityLevelName,
                value: level.authorityLevelName
            }))
        }
    };

    return keysComponentData;
};

export const keysText = text => {
    const keysText = {};

    keysText[CLIENT_NAME] = text.Name;
    keysText[CLIENT_REDIRECT] = text.RedirectURI;
    keysText[CLIENT_DESCRIPTION_SWEDISH] = text.SwedishDescription;
    keysText[CLIENT_DESCRIPTION_ENGLISH] = text.EnglishDescription;
    keysText[CLIENT_ID] = text.ClientId;
    keysText[CLIENT_GENERATE_API_KEY] = text.GenerateApiKey;
    keysText[CLIENT_RESTRICTIONS] = text.Restrictions;
    keysText[CLIENT_EMAIL_SCOPE] = text.EmailScope;

    return keysText;
};

export const keysOrder = () => [
    CLIENT_NAME,
    CLIENT_REDIRECT,
    CLIENT_DESCRIPTION_SWEDISH,
    CLIENT_DESCRIPTION_ENGLISH
];

export const createKeysOrder = () => [
    CLIENT_NAME,
    CLIENT_REDIRECT,
    CLIENT_DESCRIPTION_SWEDISH,
    CLIENT_DESCRIPTION_ENGLISH,
    CLIENT_GENERATE_API_KEY,
    CLIENT_RESTRICTIONS,
    CLIENT_EMAIL_SCOPE
];

export const readAllKeysOrder = () => [CLIENT_NAME, CLIENT_REDIRECT];
