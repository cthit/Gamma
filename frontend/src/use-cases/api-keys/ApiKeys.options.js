import * as yup from "yup";

import {
    DigitSelect,
    DigitTextArea,
    DigitTextField
} from "@cthit/react-digit-components";

import {
    API_DESCRIPTION_ENGLISH,
    API_DESCRIPTION_SWEDISH,
    API_KEY_TYPE,
    API_NAME
} from "api/api-keys/props.api-keys.api";

export const validationSchema = text => {
    const schema = {};
    schema[API_NAME] = yup.string().required(text.Name + text.IsRequired);

    schema[API_DESCRIPTION_SWEDISH] = yup
        .string()
        .required(text.SwedishDescription + text.IsRequired);

    schema[API_DESCRIPTION_ENGLISH] = yup
        .string()
        .required(text.EnglishDescription + text.IsRequired);

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const initialValues = {};
    initialValues[API_NAME] = "";
    initialValues[API_DESCRIPTION_SWEDISH] = "";
    initialValues[API_DESCRIPTION_ENGLISH] = "";

    return initialValues;
};

export const keysComponentData = (text, types) => {
    const keysComponentData = {};
    keysComponentData[API_NAME] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 50
        }
    };

    keysComponentData[API_DESCRIPTION_SWEDISH] = {
        component: DigitTextArea,
        componentProps: {
            outlined: true,
            rows: 3,
            maxLength: 500
        }
    };

    keysComponentData[API_DESCRIPTION_ENGLISH] = {
        component: DigitTextArea,
        componentProps: {
            outlined: true,
            rows: 3,
            maxLength: 500,
            onKeyPress: null
        }
    };

    const valueToTextMap = {};
    types.forEach(type => (valueToTextMap[type] = type));

    keysComponentData[API_KEY_TYPE] = {
        component: DigitSelect,
        componentProps: {
            outlined: true,
            valueToTextMap
        }
    };

    return keysComponentData;
};

export const keysText = text => {
    const keysText = {};

    keysText[API_NAME] = text.Name;
    keysText[API_DESCRIPTION_SWEDISH] = text.SwedishDescription;
    keysText[API_DESCRIPTION_ENGLISH] = text.EnglishDescription;
    keysText[API_KEY_TYPE] = text.ApiKeyType;

    return keysText;
};

export const keysOrder = () => [
    API_NAME,
    API_DESCRIPTION_SWEDISH,
    API_DESCRIPTION_ENGLISH,
    API_KEY_TYPE
];
