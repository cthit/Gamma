import * as yup from "yup";
import {
    DigitCheckbox,
    DigitTextArea,
    DigitTextField
} from "@cthit/react-digit-components";
import {
    CLIENT_DESCRIPTION_ENGLISH,
    CLIENT_DESCRIPTION_SWEDISH,
    CLIENT_OAUTH_ID,
    CLIENT_NAME,
    CLIENT_REDIRECT,
    CLIENT_AUTO_APPROVE
} from "../../api/clients/props.clients.api";

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

    schema[CLIENT_AUTO_APPROVE] = yup.bool().required();

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const initialValues = {};
    initialValues[CLIENT_NAME] = "";
    initialValues[CLIENT_REDIRECT] = "";
    initialValues[CLIENT_DESCRIPTION_SWEDISH] = "";
    initialValues[CLIENT_DESCRIPTION_ENGLISH] = "";
    initialValues[CLIENT_AUTO_APPROVE] = false;

    return initialValues;
};

export const keysComponentData = text => {
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

    keysComponentData[CLIENT_AUTO_APPROVE] = {
        component: DigitCheckbox,
        componentProps: {
            primary: true,
            label: text.AutoApprove
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
    keysText[CLIENT_OAUTH_ID] = text.ClientId;
    keysText[CLIENT_AUTO_APPROVE] = text.AutoApprove;

    return keysText;
};

export const keysOrder = () => [
    CLIENT_NAME,
    CLIENT_OAUTH_ID,
    CLIENT_REDIRECT,
    CLIENT_DESCRIPTION_SWEDISH,
    CLIENT_DESCRIPTION_ENGLISH,
    CLIENT_AUTO_APPROVE
];
