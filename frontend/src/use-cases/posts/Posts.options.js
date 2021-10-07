import * as yup from "yup";

import { DigitTextField } from "@cthit/react-digit-components";

import {
    EMAIL_PREFIX,
    POST_ENGLISH,
    POST_SWEDISH
} from "api/posts/props.posts.api";
import { ENGLISH_LANGUAGE, SWEDISH_LANGUAGE } from "api/utils/commonProps";

export const validationSchema = text => {
    const schema = {};

    schema[POST_SWEDISH] = yup
        .string()
        .required(text.SwedishInput + text.IsRequired);
    schema[POST_ENGLISH] = yup
        .string()
        .required(text.EnglishInput + text.IsRequired);
    schema[EMAIL_PREFIX] = yup.string();

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const output = {};

    output[POST_SWEDISH] = "";
    output[POST_ENGLISH] = "";
    output[EMAIL_PREFIX] = "";

    return output;
};

export const keysComponentData = text => {
    const componentData = {};

    componentData[POST_SWEDISH] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 50
        }
    };

    componentData[POST_ENGLISH] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 50
        }
    };

    componentData[EMAIL_PREFIX] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 20
        }
    };

    return componentData;
};

export const keysText = text => {
    const keysText = {};

    keysText[POST_SWEDISH] = text.Swedish;
    keysText[POST_ENGLISH] = text.English;
    keysText[EMAIL_PREFIX] = text.EmailPrefix;

    return keysText;
};

export const keysOrder = () => [POST_SWEDISH, POST_ENGLISH, EMAIL_PREFIX];
